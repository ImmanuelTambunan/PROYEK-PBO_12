package academic.service;

import academic.model.*;

import java.sql.SQLException;
import java.util.*;

public class AkademikService {

    private static AkademikService instance;
    private final AkademikORM orm;
    private final AuthService auth;

    // ── Buffer (JCF) ────────────────────────────────────────────────────
    private final List<Mahasiswa>  bufferMahasiswa   = new ArrayList<>();
    private final List<Dosen>      bufferDosen       = new ArrayList<>();
    private final List<MataKuliah> bufferMataKuliah  = new ArrayList<>();
    private final List<Nilai>      bufferNilai        = new ArrayList<>();

    // HashMap untuk lookup cepat di buffer
    private final Map<String, MataKuliah> mapMkBuffer = new HashMap<>();

    private AkademikService() {
        this.orm  = new AkademikORM();
        this.auth = AuthService.getInstance();
    }

    public static AkademikService getInstance() {
        if (instance == null) instance = new AkademikService();
        return instance;
    }

    // =====================================================================
    // TAMBAH KE BUFFER
    // =====================================================================

    public void tambahMahasiswa(String nim, String nama, String prodi) throws Exception {
        auth.requireRole(Role.ADMIN);
        Mahasiswa m = new Mahasiswa(nim, nama, prodi);
        bufferMahasiswa.add(m);
        System.out.println("[OK] Mahasiswa ditambahkan ke buffer: " + nama + " (NIM: " + nim + ")");
        auth.audit("TAMBAH_MAHASISWA", "NIM=" + nim + ", nama=" + nama);
    }

    public void tambahDosen(String nidn, String nama, String fakultas) throws Exception {
        auth.requireRole(Role.ADMIN);
        Dosen d = new Dosen(nidn, nama, fakultas);
        bufferDosen.add(d);
        System.out.println("[OK] Dosen ditambahkan ke buffer: " + nama + " (NIDN: " + nidn + ")");
        auth.audit("TAMBAH_DOSEN", "NIDN=" + nidn + ", nama=" + nama);
    }

    public void tambahMataKuliah(String kode, String nama, int sks) throws Exception {
        auth.requireRole(Role.ADMIN);
        MataKuliah mk = new MataKuliah(kode, nama, sks);
        bufferMataKuliah.add(mk);
        mapMkBuffer.put(kode, mk);
        System.out.println("[OK] Mata kuliah ditambahkan ke buffer: " + nama +
            " (" + kode + ", " + sks + " SKS)");
        auth.audit("TAMBAH_MATAKULIAH", "kode=" + kode + ", nama=" + nama);
    }

    public void inputNilai(String nim, String kodeMk, String hurufMutu, int semester)
            throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN);

        // Cari nama & SKS mata kuliah: cek buffer dulu, lalu DB
        String namaMk = "-";
        int sks = 0;
        if (mapMkBuffer.containsKey(kodeMk)) {
            MataKuliah mk = mapMkBuffer.get(kodeMk);
            namaMk = mk.getNama();
            sks    = mk.getSks();
        } else {
            MataKuliah mk = orm.findMataKuliahById(kodeMk);
            if (mk != null) { namaMk = mk.getNama(); sks = mk.getSks(); }
        }

        Nilai n = new Nilai(nim, kodeMk, namaMk, sks, hurufMutu, semester);
        bufferNilai.add(n);
        System.out.printf("[OK] Nilai ditambahkan ke buffer: NIM %s | MK %s | Nilai: %s (bobot: %.1f)%n",
            nim, kodeMk, hurufMutu, n.getBobot());
        auth.audit("INPUT_NILAI", "NIM=" + nim + ", MK=" + kodeMk + ", nilai=" + hurufMutu);
    }

    // =====================================================================
    // SIMPAN KE DB (flush buffer)
    // =====================================================================

    public void simpanKeDb() throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN);
        System.out.println("\n--- Menyimpan data ke database ---");
        int count = 0;
        try {
            for (Mahasiswa m  : bufferMahasiswa)  { orm.insertMahasiswa(m);  count++; }
            for (Dosen d      : bufferDosen)       { orm.insertDosen(d);      count++; }
            for (MataKuliah mk: bufferMataKuliah)  { orm.insertMataKuliah(mk);count++; }
            for (Nilai n      : bufferNilai)       { orm.insertNilai(n);      count++; }
        } catch (SQLException e) {
            throw new Exception("Gagal menyimpan ke DB: " + e.getMessage());
        }
        System.out.println("[OK] " + count + " record berhasil disimpan ke database.");
        clearBuffer();
        System.out.println("[OK] Buffer memori dibersihkan.");
        auth.audit("SIMPAN_KE_DB", count + " record disimpan");
    }

    private void clearBuffer() {
        bufferMahasiswa.clear();
        bufferDosen.clear();
        bufferMataKuliah.clear();
        bufferNilai.clear();
        mapMkBuffer.clear();
    }

    // =====================================================================
    // EDIT (langsung ke DB)
    // =====================================================================

    public void editMahasiswa(String nim, String nama, String prodi) throws Exception {
        auth.requireRole(Role.ADMIN);
        orm.updateMahasiswa(nim, nama, prodi);
        System.out.println("[OK] Data mahasiswa NIM " + nim + " berhasil diupdate.");
        auth.audit("EDIT_MAHASISWA", "NIM=" + nim);
    }

    public void editDosen(String nidn, String nama, String fakultas) throws Exception {
        auth.requireRole(Role.ADMIN);
        orm.updateDosen(nidn, nama, fakultas);
        System.out.println("[OK] Data dosen NIDN " + nidn + " berhasil diupdate.");
        auth.audit("EDIT_DOSEN", "NIDN=" + nidn);
    }

    public void editMataKuliah(String kode, String nama, int sks) throws Exception {
        auth.requireRole(Role.ADMIN);
        orm.updateMataKuliah(kode, nama, sks);
        System.out.println("[OK] Data mata kuliah " + kode + " berhasil diupdate.");
        auth.audit("EDIT_MATAKULIAH", "kode=" + kode);
    }

    // =====================================================================
    // HAPUS (langsung ke DB)
    // =====================================================================

    public void hapusMahasiswa(String nim) throws Exception {
        auth.requireRole(Role.ADMIN);
        orm.deleteMahasiswa(nim);
        System.out.println("[OK] Mahasiswa NIM " + nim + " berhasil dihapus dari database.");
        auth.audit("HAPUS_MAHASISWA", "NIM=" + nim);
    }

    public void hapusNilai(String nim, String kodeMk) throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN);
        orm.deleteNilai(nim, kodeMk);
        System.out.println("[OK] Nilai NIM " + nim + " MK " + kodeMk + " berhasil dihapus.");
        auth.audit("HAPUS_NILAI", "NIM=" + nim + ", MK=" + kodeMk);
    }

    // =====================================================================
    // LIHAT SEMUA
    // =====================================================================

    public void lihatSemua() throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN);
        try {
            System.out.println("\n--- Data Mahasiswa (dari Database) ---");
            List<Mahasiswa> mahasiswaList = orm.selectAllMahasiswa();
            if (mahasiswaList.isEmpty()) System.out.println("  (kosong)");
            else for (Mahasiswa m : mahasiswaList) System.out.println("  " + m);

            System.out.println("\n--- Data Dosen (dari Database) ---");
            List<Dosen> dosenList = orm.selectAllDosen();
            if (dosenList.isEmpty()) System.out.println("  (kosong)");
            else for (Dosen d : dosenList) System.out.println("  " + d);

            System.out.println("\n--- Data Mata Kuliah (dari Database) ---");
            List<MataKuliah> mkList = orm.selectAllMataKuliah();
            if (mkList.isEmpty()) System.out.println("  (kosong)");
            else for (MataKuliah mk : mkList)
                System.out.println("  " + mk.getKode() + " | " + mk.getNama() + " | " + mk.getSks() + " SKS");

        } catch (SQLException e) {
            throw new Exception("Gagal membaca data: " + e.getMessage());
        }
    }

    // =====================================================================
    // CETAK TRANSKRIP
    // =====================================================================

    public void cetakTranskrip(String nim) throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN, Role.MAHASISWA);

        // Jika role MAHASISWA, hanya boleh lihat transkrip sendiri
        if (auth.getCurrentUser().getRole() == Role.MAHASISWA) {
            String refId = auth.getCurrentUser().getRefId();
            if (refId != null && !refId.equals(nim)) {
                throw new Exception("Akses ditolak. Anda hanya bisa melihat transkrip sendiri.");
            }
        }

        try {
            Mahasiswa m = orm.findMahasiswaById(nim);
            if (m == null) {
                System.out.println("[ERROR] Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
                return;
            }

            List<Nilai> nilaiList = orm.selectNilaiByNim(nim);

            System.out.println("\n====================================================");
            System.out.println("           TRANSKRIP NILAI AKADEMIK");
            System.out.println("====================================================");
            System.out.println("NIM    : " + m.getNim());
            System.out.println("Nama   : " + m.getNama());
            System.out.println("Prodi  : " + m.getProdi());
            System.out.println("Status : " + m.getPeran());
            System.out.println("====================================================");
            System.out.printf("%-10s %-35s %4s  %-5s %s%n",
                "Kode", "Mata Kuliah", "SKS", "Nilai", "Bobot");
            System.out.println("----------------------------------------------------");

            int totalSks = 0;
            double totalBobot = 0;

            for (Nilai n : nilaiList) {
                System.out.printf("%-10s %-35s %3d  %-6s %.1f%n",
                    n.getKodeMk(), n.getNamaMk(), n.getSks(), n.getHurufMutu(), n.getBobot());
                totalSks   += n.getSks();
                totalBobot += n.getBobot() * n.getSks();
            }

            System.out.println("----------------------------------------------------");
            System.out.println("Total SKS : " + totalSks);
            double ipk = totalSks > 0 ? totalBobot / totalSks : 0.0;
            System.out.printf("IPK       : %.2f%n", ipk);
            System.out.println("====================================================");

            auth.audit("CETAK_TRANSKRIP", "NIM=" + nim);

        } catch (SQLException e) {
            throw new Exception("Gagal membaca transkrip: " + e.getMessage());
        }
    }

    // =====================================================================
    // FILTER MAHASISWA
    // =====================================================================

    public void filterMahasiswa(String keyword) throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN);
        try {
            List<Mahasiswa> list = orm.searchMahasiswa(keyword);
            System.out.println("\n--- Hasil Pencarian: '" + keyword + "' ---");
            if (list.isEmpty()) System.out.println("  Tidak ada data yang cocok.");
            else for (Mahasiswa m : list) System.out.println("  " + m);
        } catch (SQLException e) {
            throw new Exception("Gagal mencari data: " + e.getMessage());
        }
    }

    // =====================================================================
    // STATISTIK
    // =====================================================================

    public void tampilkanStatistik() throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN);
        try {
            System.out.println("\n========== STATISTIK AKADEMIK ==========");

            System.out.println("\n-- Rata-rata IPK per Prodi --");
            Map<String, Double> ipkPerProdi = orm.getIpkPerProdi();
            if (ipkPerProdi.isEmpty()) System.out.println("  (belum ada data nilai)");
            else {
                List<String> prodis = new ArrayList<>(ipkPerProdi.keySet());
                Collections.sort(prodis);
                for (String prodi : prodis)
                    System.out.printf("  %-40s : %.2f%n", prodi, ipkPerProdi.get(prodi));
            }

            System.out.println("\n-- Distribusi Nilai --");
            Map<String, Integer> dist = orm.getDistribusiNilai();
            if (dist.isEmpty()) System.out.println("  (belum ada data nilai)");
            else {
                List<String> grades = new ArrayList<>(dist.keySet());
                Collections.sort(grades);
                for (String g : grades)
                    System.out.printf("  %-4s : %d mahasiswa%n", g, dist.get(g));
            }

            System.out.println("=========================================");
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil statistik: " + e.getMessage());
        }
    }

    // =====================================================================
    // AUDIT LOG
    // =====================================================================

    public void lihatAuditLog(int limit) throws Exception {
        auth.requireRole(Role.ADMIN);
        try {
            List<AuditLog> logs = orm.selectAuditLog(limit);
            System.out.println("\n========== AUDIT LOG (last " + limit + ") ==========");
            if (logs.isEmpty()) System.out.println("  (kosong)");
            else for (AuditLog log : logs)
                System.out.printf("  [%s] %-12s %-20s %s%n",
                    log.getTimestamp(), log.getUsername(), log.getAction(),
                    log.getDetail() != null ? log.getDetail() : "");
            System.out.println("=====================================================");
        } catch (SQLException e) {
            throw new Exception("Gagal membaca audit log: " + e.getMessage());
        }
    }

    // ── Accessor untuk ReportService ────────────────────────────────────
    public AkademikORM getOrm() { return orm; }
}
