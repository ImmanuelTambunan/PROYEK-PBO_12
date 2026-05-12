package academic.driver;

import academic.db.DatabaseManager;
<<<<<<< HEAD
import academic.enums.HurufMutu;
import academic.enums.StatusAkademik;
import academic.interfaces.Printable;
=======
>>>>>>> c931b482621974a2d0cd18da577e510820955552
import academic.model.*;
import academic.model.Nilai;
import academic.record.RingkasanTranskrip;
import academic.repository.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Driver1 — Entry point CLI Sistem Pengelolaan Akademik.
 *
 * KONSEP DITERAPKAN DI FILE INI:
 *   [10] LOCAL CLASS      — class MenuFormatter di dalam method tampilkanMenu().
 *   [11] ANONYMOUS CLASS  — Comparator sorting & Printable handler.
 *   [13] JCF              — ArrayList & HashMap untuk buffer & lookup.
 *   [3]  POLYMORPHISM     — objek diperlakukan via interface Printable.
 *   [8]  GENERICS         — Repository<T> digunakan secara generic.
 *
 * FORMAT PERINTAH LANGSUNG (tetap didukung untuk CI/testing):
 *   TAMBAH_MAHASISWA#NIM#Nama#Prodi[#Status]
 *   TAMBAH_DOSEN#NIDN#Nama#Fakultas
 *   TAMBAH_MATAKULIAH#Kode#Nama#SKS
 *   INPUT_NILAI#NIM#KodeMK#HurufMutu
 *   SIMPAN_KE_DB
 *   CETAK_TRANSKRIP#NIM
 *   LIHAT_SEMUA
 *   HAPUS_MAHASISWA#NIM
 *   HAPUS_DOSEN#NIDN
 *   HAPUS_MATAKULIAH#Kode
 *   HAPUS_NILAI#NIM#KodeMK
 *   HAPUS_DATABASE
 *   ---    <- keluar program
 */
public class Driver1 {

    // ===================================================================
    //  [13] JCF — Buffer memori (ArrayList & HashMap)
    // ===================================================================
    private static final List<Mahasiswa>  bufferMahasiswa  = new ArrayList<>();
    private static final List<Dosen>      bufferDosen      = new ArrayList<>();
    private static final List<MataKuliah> bufferMataKuliah = new ArrayList<>();
    private static final List<Nilai>      bufferNilai      = new ArrayList<>();

    // HashMap untuk lookup cepat di buffer — O(1)
    private static final Map<String, Mahasiswa>  mapMahasiswa  = new HashMap<>();
    private static final Map<String, MataKuliah> mapMataKuliah = new HashMap<>();

    // Repository instances (Generic + SOLID DIP)
    private static final MahasiswaRepository  repoMhs   = MahasiswaRepository.getInstance();
    private static final DosenRepository      repoDsn   = DosenRepository.getInstance();
    private static final MataKuliahRepository repoMK    = MataKuliahRepository.getInstance();
    private static final NilaiRepository      repoNilai = NilaiRepository.getInstance();

    private static final Scanner scanner = new Scanner(System.in);

    // ===================================================================
    //  MAIN
    // ===================================================================
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        cetakHeader();
        tampilkanMenu();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.equals("---")) break;
            if (line.isEmpty())     { tampilkanMenu(); continue; }

            if (line.contains("#") || isDirectCommand(line)) {
                prosesPerintahLangsung(line);
            } else {
                prosesPilihanMenu(line);
            }
        }

        // Cek buffer belum disimpan sebelum keluar
        if (!bufferMahasiswa.isEmpty() || !bufferDosen.isEmpty()
                || !bufferMataKuliah.isEmpty() || !bufferNilai.isEmpty()) {
            System.out.println("\n[INFO] Masih ada data di buffer yang belum disimpan.");
            System.out.print("Simpan ke database sebelum keluar? (y/n): ");
            if (scanner.hasNextLine()) {
                String jawab = scanner.nextLine().trim();
                if (jawab.equalsIgnoreCase("y")) {
                    try { handleSimpanKeDB(); }
                    catch (SQLException e) { System.out.println("[ERROR] " + e.getMessage()); }
                }
            }
        }

        DatabaseManager.closeConnection();
        scanner.close();
        System.out.println("\nProgram selesai. Sampai jumpa!");
    }

    // ===================================================================
    //  [10] LOCAL CLASS — tampilkanMenu menggunakan MenuFormatter
    //  yang hanya hidup dalam scope method ini
    // ===================================================================
    private static void tampilkanMenu() {
        /**
         * MenuFormatter adalah LOCAL CLASS — hanya visible di dalam method ini.
         * Bertugas memformat tampilan menu secara konsisten.
         * Konsep: [10] LOCAL CLASS
         */
        class MenuFormatter {
            static final String LINE = "  ----------------------------------------";

            String item(String no, String label) {
                return String.format("  [%s]  %-34s", no, label);
            }
            void line()  { System.out.println(LINE); }
            void grup(String judul) {
                line();
                System.out.printf("  %-40s%n", judul);
                line();
            }
        }

        MenuFormatter fmt = new MenuFormatter();
        System.out.println();
        System.out.println("  MENU UTAMA");
        fmt.grup("[ Data Akademik ]");
        System.out.println(fmt.item("1", "Tambah Mahasiswa"));
        System.out.println(fmt.item("2", "Tambah Dosen"));
        System.out.println(fmt.item("3", "Tambah Mata Kuliah"));
        System.out.println(fmt.item("4", "Input Nilai Mahasiswa"));
        fmt.grup("[ Database & Laporan ]");
        System.out.println(fmt.item("5", "Simpan Semua ke Database"));
        System.out.println(fmt.item("6", "Cetak Transkrip Nilai"));
        System.out.println(fmt.item("7", "Lihat Semua Data"));
        System.out.println(fmt.item("8", "Hapus Data (Mhs/Dosen/MK/Nilai)"));
        System.out.println(fmt.item("9", "Hapus Seluruh Database"));
        fmt.grup("[ Sistem ]");
        System.out.println(fmt.item("0", "Keluar  (atau ketik ---)"));
        fmt.line();
        System.out.print("\n  Pilih menu  : ");
    }

    private static void cetakHeader() {
        System.out.println("========================================================");
        System.out.println("  SISTEM PENGELOLAAN NILAI & REKAM JEJAK AKADEMIK");
        System.out.println("  Inheritance | JCF | JDBC | Custom ORM | SOLID");
        System.out.println("========================================================");
        System.out.println("  Masukkan nomor menu atau perintah langsung.");
        System.out.println("  Ketik '---' untuk mengakhiri program.");
    }

    // ===================================================================
    //  PROSES PILIHAN MENU INTERAKTIF
    // ===================================================================
    private static void prosesPilihanMenu(String pilihan) {
        try {
            switch (pilihan.trim()) {
                case "1" -> menuTambahMahasiswa();
                case "2" -> menuTambahDosen();
                case "3" -> menuTambahMataKuliah();
                case "4" -> menuInputNilai();
                case "5" -> handleSimpanKeDB();
                case "6" -> menuCetakTranskrip();
                case "7" -> handleLihatSemua();
                case "8" -> menuHapusData();
                case "9" -> menuHapusDatabase();
                case "0" -> {
                    System.out.println("Keluar dari program...");
                    DatabaseManager.closeConnection();
                    System.exit(0);
                }
                default  -> System.out.println("[WARN] Pilihan tidak dikenali: " + pilihan);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        if (!pilihan.equals("0")) tampilkanMenu();
    }

    // ===================================================================
    //  MENU INTERAKTIF — dipandu input satu per satu
    // ===================================================================

    private static void menuTambahMahasiswa() {
        System.out.println("\n--- Tambah Mahasiswa ---");
        System.out.print("  NIM    : "); String nim   = scanner.nextLine().trim();
        System.out.print("  Nama   : "); String nama  = scanner.nextLine().trim();
        System.out.print("  Prodi  : "); String prodi = scanner.nextLine().trim();
        System.out.print("  Status (AKTIF/CUTI/LULUS/DO) [Enter=AKTIF]: ");
        String statusStr = scanner.nextLine().trim();
        StatusAkademik status = statusStr.isEmpty()
            ? StatusAkademik.AKTIF : StatusAkademik.fromString(statusStr);
        handleTambahMahasiswa(new String[]{"", nim, nama, prodi, status.name()});
    }

    private static void menuTambahDosen() {
        System.out.println("\n--- Tambah Dosen ---");
        System.out.print("  NIDN     : "); String nidn = scanner.nextLine().trim();
        System.out.print("  Nama     : "); String nama = scanner.nextLine().trim();
        System.out.print("  Fakultas : "); String fak  = scanner.nextLine().trim();
        handleTambahDosen(new String[]{"", nidn, nama, fak});
    }

    private static void menuTambahMataKuliah() {
        System.out.println("\n--- Tambah Mata Kuliah ---");
        System.out.print("  Kode MK : "); String kode = scanner.nextLine().trim();
        System.out.print("  Nama MK : "); String nama = scanner.nextLine().trim();
        System.out.print("  SKS     : "); String sks  = scanner.nextLine().trim();
        handleTambahMataKuliah(new String[]{"", kode, nama, sks});
    }

    private static void menuInputNilai() {
        System.out.println("\n--- Input Nilai Mahasiswa ---");

        // ================================================================
        //  [11] ANONYMOUS CLASS — Printable anonim untuk tampilkan info enum
        //  Tidak perlu buat class baru hanya untuk fungsi display sekali pakai.
        // ================================================================
        Printable infoHurufMutu = new Printable() {
            @Override
            public void cetak() {
                System.out.println("  Huruf mutu yang tersedia:");
                for (HurufMutu hm : HurufMutu.values()) {
                    System.out.printf("    %-3s -> %.1f (%s)%n",
                        hm.name(), hm.getBobot(), hm.getKeterangan());
                }
            }
            @Override public String getRingkasan() { return "Info HurufMutu"; }
        };
        infoHurufMutu.cetak(); // [3] POLYMORPHISM — dipanggil via interface

        System.out.print("\n  NIM Mahasiswa : "); String nim   = scanner.nextLine().trim();
        System.out.print("  Kode MK       : "); String kode  = scanner.nextLine().trim();
        System.out.print("  Huruf Mutu    : "); String huruf = scanner.nextLine().trim();
        handleInputNilai(new String[]{"", nim, kode, huruf});
    }

    private static void menuCetakTranskrip() throws SQLException {
        System.out.println("\n--- Cetak Transkrip Nilai ---");
        System.out.print("  Masukkan NIM: ");
        String nim = scanner.nextLine().trim();
        handleCetakTranskrip(new String[]{"", nim});
    }

    private static void menuHapusData() throws SQLException {
        System.out.println("\n--- Hapus Data ---");
        System.out.println("  [1] Hapus Mahasiswa (termasuk nilai)");
        System.out.println("  [2] Hapus Dosen");
        System.out.println("  [3] Hapus Mata Kuliah (termasuk nilai)");
        System.out.println("  [4] Hapus Nilai Tertentu");
        System.out.print("  Pilih: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("  NIM: ");
                handleHapusMahasiswa(new String[]{"", scanner.nextLine().trim()});
            }
            case "2" -> {
                System.out.print("  NIDN: ");
                handleHapusDosen(new String[]{"", scanner.nextLine().trim()});
            }
            case "3" -> {
                System.out.print("  Kode MK: ");
                handleHapusMataKuliah(new String[]{"", scanner.nextLine().trim()});
            }
            case "4" -> {
                System.out.print("  NIM: ");     String nim  = scanner.nextLine().trim();
                System.out.print("  Kode MK: "); String kode = scanner.nextLine().trim();
                handleHapusNilai(new String[]{"", nim, kode});
            }
            default -> System.out.println("[WARN] Pilihan tidak valid.");
        }
    }

    private static void menuHapusDatabase() {
        System.out.println("\n[PERINGATAN] Aksi ini akan menghapus SELURUH database secara permanen!");
        System.out.print("Ketik 'HAPUS' untuk konfirmasi: ");
        String k = scanner.nextLine().trim();
        if (k.equals("HAPUS")) {
            boolean ok = DatabaseManager.dropDatabase();
            if (ok) {
                System.out.println("[OK] Database dihapus. Membuat database baru...");
                DatabaseManager.initializeDatabase();
            }
        } else {
            System.out.println("[BATAL] Penghapusan database dibatalkan.");
        }
    }

    // ===================================================================
    //  PROSES PERINTAH LANGSUNG (#-separated)
    // ===================================================================
    private static final String[] DIRECT_CMDS = {
        "TAMBAH_MAHASISWA","TAMBAH_DOSEN","TAMBAH_MATAKULIAH","INPUT_NILAI",
        "SIMPAN_KE_DB","CETAK_TRANSKRIP","LIHAT_SEMUA",
        "HAPUS_MAHASISWA","HAPUS_DOSEN","HAPUS_MATAKULIAH","HAPUS_NILAI","HAPUS_DATABASE"
    };

    private static boolean isDirectCommand(String line) {
        String up = line.toUpperCase();
        for (String cmd : DIRECT_CMDS) if (up.startsWith(cmd)) return true;
        return false;
    }

    private static void prosesPerintahLangsung(String line) {
        String[] token   = line.split("#");
        String   perintah = token[0].trim().toUpperCase();
        try {
            switch (perintah) {
                case "TAMBAH_MAHASISWA"  -> handleTambahMahasiswa(token);
                case "TAMBAH_DOSEN"      -> handleTambahDosen(token);
                case "TAMBAH_MATAKULIAH" -> handleTambahMataKuliah(token);
                case "INPUT_NILAI"       -> handleInputNilai(token);
                case "SIMPAN_KE_DB"      -> handleSimpanKeDB();
                case "CETAK_TRANSKRIP"   -> handleCetakTranskrip(token);
                case "LIHAT_SEMUA"       -> handleLihatSemua();
                case "HAPUS_MAHASISWA"   -> handleHapusMahasiswa(token);
                case "HAPUS_DOSEN"       -> handleHapusDosen(token);
                case "HAPUS_MATAKULIAH"  -> handleHapusMataKuliah(token);
                case "HAPUS_NILAI"       -> handleHapusNilai(token);
                case "HAPUS_DATABASE"    -> menuHapusDatabase();
                default -> System.out.println("[WARN] Perintah tidak dikenali: " + perintah);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // ===================================================================
    //  HANDLER TAMBAH
    // ===================================================================

    private static void handleTambahMahasiswa(String[] t) {
        if (t.length < 4) { System.out.println("[ERROR] Format: TAMBAH_MAHASISWA#NIM#Nama#Prodi"); return; }
        String nim = t[1].trim(), nama = t[2].trim(), prodi = t[3].trim();
        String statusStr = (t.length >= 5) ? t[4].trim() : "AKTIF";
        if (mapMahasiswa.containsKey(nim)) {
            System.out.println("[SKIP] NIM " + nim + " sudah ada di buffer."); return;
        }
        Mahasiswa mhs = new Mahasiswa.Builder(nim, nama, prodi).status(statusStr).build();
        bufferMahasiswa.add(mhs);
        mapMahasiswa.put(nim, mhs);
        System.out.println("[OK] " + mhs.getPeran() + " -> buffer: "
            + mhs.getNama() + " (NIM: " + mhs.getNim() + ") | " + mhs.getStatus());
    }

    private static void handleTambahDosen(String[] t) {
        if (t.length < 4) { System.out.println("[ERROR] Format: TAMBAH_DOSEN#NIDN#Nama#Fakultas"); return; }
        Dosen dsn = new Dosen.Builder(t[1].trim(), t[2].trim(), t[3].trim()).build();
        bufferDosen.add(dsn);
        System.out.println("[OK] " + dsn.getPeran() + " -> buffer: "
            + dsn.getNama() + " (NIDN: " + dsn.getNidn() + ")");
    }

    private static void handleTambahMataKuliah(String[] t) {
        if (t.length < 4) { System.out.println("[ERROR] Format: TAMBAH_MATAKULIAH#Kode#Nama#SKS"); return; }
        String kode = t[1].trim();
        if (mapMataKuliah.containsKey(kode)) {
            System.out.println("[SKIP] Kode " + kode + " sudah ada di buffer."); return;
        }
        int sks;
        try { sks = Integer.parseInt(t[3].trim()); }
        catch (NumberFormatException e) { System.out.println("[ERROR] SKS harus angka."); return; }
        MataKuliah mk = new MataKuliah.Builder(kode, t[2].trim(), sks).build();
        bufferMataKuliah.add(mk);
        mapMataKuliah.put(kode, mk);
        System.out.println("[OK] MataKuliah -> buffer: "
            + mk.getNama() + " (" + mk.getKode() + ", " + mk.getSks() + " SKS)");
    }

    private static void handleInputNilai(String[] t) {
        if (t.length < 4) { System.out.println("[ERROR] Format: INPUT_NILAI#NIM#KodeMK#HurufMutu"); return; }
        HurufMutu hm;
        try { hm = HurufMutu.fromString(t[3].trim()); }
        catch (IllegalArgumentException e) { System.out.println("[ERROR] " + e.getMessage()); return; }
        Nilai nilai = new Nilai(t[1].trim(), t[2].trim(), hm);
        bufferNilai.add(nilai);
        System.out.println("[OK] Nilai -> buffer: NIM " + t[1].trim()
            + " | MK " + t[2].trim() + " | " + hm.name()
            + " (bobot: " + hm.getBobot() + ") — " + hm.getKeterangan());
    }

    // ===================================================================
    //  HANDLER SIMPAN KE DB
    // ===================================================================

    private static void handleSimpanKeDB() throws SQLException {
        System.out.println("\n--- Menyimpan data ke database ---");
        int total = 0;

        // [11] ANONYMOUS CLASS — Comparator sort mahasiswa by NIM sebelum insert
        bufferMahasiswa.sort(new Comparator<Mahasiswa>() {
            @Override public int compare(Mahasiswa a, Mahasiswa b) {
                return a.getNim().compareTo(b.getNim());
            }
        });

        for (Mahasiswa mhs : bufferMahasiswa) {
            try { repoMhs.save(mhs); total++; }
            catch (SQLException e) { skipOrThrow(e, "Mahasiswa", mhs.getNim()); }
        }
        for (Dosen dsn : bufferDosen) {
            try { repoDsn.save(dsn); total++; }
            catch (SQLException e) { skipOrThrow(e, "Dosen", dsn.getNidn()); }
        }
        for (MataKuliah mk : bufferMataKuliah) {
            try { repoMK.save(mk); total++; }
            catch (SQLException e) { skipOrThrow(e, "MataKuliah", mk.getKode()); }
        }
        for (Nilai n : bufferNilai) {
            repoNilai.save(n); total++;
        }

        System.out.println("[OK] " + total + " record berhasil disimpan ke database.");
        clearBuffer();
    }

    private static void skipOrThrow(SQLException e, String tipe, String id) {
        String msg = e.getMessage();
        if (msg != null && (msg.contains("UNIQUE") || msg.contains("PRIMARY KEY")))
            System.out.println("[SKIP] " + tipe + " " + id + " sudah ada di database.");
        else
            System.out.println("[ERROR] " + tipe + " " + id + ": " + msg);
    }

    private static void clearBuffer() {
        bufferMahasiswa.clear(); bufferDosen.clear();
        bufferMataKuliah.clear(); bufferNilai.clear();
        mapMahasiswa.clear(); mapMataKuliah.clear();
        System.out.println("[OK] Buffer memori dibersihkan.\n");
    }

    // ===================================================================
    //  HANDLER CETAK TRANSKRIP
    // ===================================================================

<<<<<<< HEAD
    private static void handleCetakTranskrip(String[] t) throws SQLException {
        if (t.length < 2) { System.out.println("[ERROR] Format: CETAK_TRANSKRIP#NIM"); return; }
        String nim = t[1].trim();

        Optional<Mahasiswa> mhsOpt = repoMhs.findById(nim);
        if (mhsOpt.isEmpty()) {
            System.out.println("[ERROR] Mahasiswa NIM " + nim + " tidak ditemukan."); return;
=======
        // ORM: query database → objek Mahasiswa
        Mahasiswa mhs = AkademikORM.selectMahasiswaByNim(nim).orElse(null);
        if (mhs == null) {
            System.out.println("[ERROR] Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
            return;
>>>>>>> c931b482621974a2d0cd18da577e510820955552
        }

        List<Nilai> daftarNilai = repoNilai.findByNim(nim);
        Optional<RingkasanTranskrip> rOpt = repoNilai.getRingkasanTranskrip(nim);

        // [11] ANONYMOUS CLASS — sort nilai by kode MK
        daftarNilai.sort(new Comparator<Nilai>() {
            @Override public int compare(Nilai a, Nilai b) {
                return a.getKodeMK().compareTo(b.getKodeMK());
            }
        });

        System.out.println();
        System.out.println("====================================================");
        System.out.println("           TRANSKRIP NILAI AKADEMIK");
        System.out.println("====================================================");
        // [3] POLYMORPHISM — cetak() dipanggil via parent abstract / interface
        mhsOpt.get().cetak();
        System.out.println("====================================================");
        System.out.printf("%-10s %-32s %5s %6s %6s%n",
            "Kode", "Mata Kuliah", "SKS", "Nilai", "Bobot");
        System.out.println("----------------------------------------------------");

        // [3] POLYMORPHISM — n.cetak() / getRingkasan() via Printable interface
        for (Nilai n : daftarNilai) {
            System.out.printf("%-10s %-32s %3d   %-5s  %.1f%n",
                n.getKodeMK(), n.getNamaMK(), n.getSks(),
                n.getHurufMutu().name(), n.getBobot());
        }

        System.out.println("----------------------------------------------------");
        // [7] RECORD — akses field immutable RingkasanTranskrip
        rOpt.ifPresentOrElse(r -> {
            System.out.printf("Total SKS : %d%n",     r.totalSks());
            System.out.printf("IPK       : %s%n",     r.getIpkFormatted());
            System.out.printf("Predikat  : %s%n",     r.getPredikat());
        }, () -> {
            System.out.println("Total SKS : 0");
            System.out.println("IPK       : 0.00");
        });
        System.out.println("====================================================");
        System.out.println();
    }

    // ===================================================================
    //  HANDLER LIHAT SEMUA
    // ===================================================================

    private static void handleLihatSemua() throws SQLException {
        List<Mahasiswa>  mhsList = repoMhs.findAll();
        List<Dosen>      dsnList = repoDsn.findAll();
        List<MataKuliah> mkList  = repoMK.findAll();

        // [11] ANONYMOUS CLASS — helper cetak section header
        Runnable cetakPemisah = new Runnable() {
            @Override public void run() {
                System.out.println("  --------------------------------------------------");
            }
        };

        System.out.println("\n--- Data Mahasiswa (dari Database) ---");
        cetakPemisah.run();
        if (mhsList.isEmpty()) System.out.println("  (belum ada data)");
        // [3] POLYMORPHISM — getRingkasan() dipanggil via Printable interface
        mhsList.forEach(m -> System.out.println(m.getRingkasan()));

        System.out.println("\n--- Data Dosen (dari Database) ---");
        cetakPemisah.run();
        if (dsnList.isEmpty()) System.out.println("  (belum ada data)");
        dsnList.forEach(d -> System.out.println(d.getRingkasan()));

        System.out.println("\n--- Data Mata Kuliah (dari Database) ---");
        cetakPemisah.run();
        if (mkList.isEmpty()) System.out.println("  (belum ada data)");
        mkList.forEach(mk -> System.out.println(mk.getRingkasan()));

        System.out.println();
    }

    // ===================================================================
    //  HANDLER HAPUS
    // ===================================================================

    private static void handleHapusMahasiswa(String[] t) throws SQLException {
        if (t.length < 2) { System.out.println("[ERROR] Format: HAPUS_MAHASISWA#NIM"); return; }
        String nim = t[1].trim();
        if (!repoMhs.existsById(nim)) {
            System.out.println("[ERROR] Mahasiswa NIM " + nim + " tidak ditemukan."); return;
        }
        repoMhs.deleteById(nim);
        System.out.println("[OK] Mahasiswa NIM " + nim + " dan nilainya berhasil dihapus.");
    }

    private static void handleHapusDosen(String[] t) throws SQLException {
        if (t.length < 2) { System.out.println("[ERROR] Format: HAPUS_DOSEN#NIDN"); return; }
        String nidn = t[1].trim();
        if (!repoDsn.existsById(nidn)) {
            System.out.println("[ERROR] Dosen NIDN " + nidn + " tidak ditemukan."); return;
        }
        repoDsn.deleteById(nidn);
        System.out.println("[OK] Dosen NIDN " + nidn + " berhasil dihapus.");
    }

    private static void handleHapusMataKuliah(String[] t) throws SQLException {
        if (t.length < 2) { System.out.println("[ERROR] Format: HAPUS_MATAKULIAH#Kode"); return; }
        String kode = t[1].trim();
        if (!repoMK.existsById(kode)) {
            System.out.println("[ERROR] MataKuliah " + kode + " tidak ditemukan."); return;
        }
        repoMK.deleteById(kode);
        System.out.println("[OK] MataKuliah " + kode + " dan nilainya berhasil dihapus.");
    }

    private static void handleHapusNilai(String[] t) throws SQLException {
        if (t.length < 3) { System.out.println("[ERROR] Format: HAPUS_NILAI#NIM#KodeMK"); return; }
        repoNilai.deleteByNimAndKode(t[1].trim(), t[2].trim());
        System.out.println("[OK] Nilai NIM " + t[1].trim() + " | MK " + t[2].trim() + " dihapus.");
    }
}