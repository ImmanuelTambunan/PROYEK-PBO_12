package academic.driver;
//12S24002_Petra Ignatius Sitorus
//12S24004_Silvia Eklesiana Sitorus
//12S24021_Ika Maria Manurung
//12S24034_Immanuel Alexander Tambunan
import academic.model.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Driver1.java — Program utama (entry point) CLI.
 *
 * Program ini mengintegrasikan 4 konsep dalam satu alur:
 *   1. INHERITANCE  → CivitasAkademika → Mahasiswa, Dosen
 *   2. JCF          → ArrayList & HashMap untuk proses data di memori
 *   3. JDBC         → Koneksi dan query ke SQLite via DatabaseManager
 *   4. ORM (Custom) → AkademikORM memetakan Objek ↔ SQL
 *
 * Format perintah yang didukung:
 *   TAMBAH_MAHASISWA#NIM#Nama#Prodi
 *   TAMBAH_DOSEN#NIDN#Nama#Fakultas
 *   TAMBAH_MATAKULIAH#Kode#Nama#SKS
 *   INPUT_NILAI#NIM#KodeMK#HurufMutu
 *   SIMPAN_KE_DB
 *   CETAK_TRANSKRIP#NIM
 *   LIHAT_SEMUA
 *   ---
 */
public class Driver1 {

    // =====================================================================
    // JCF: Data disimpan sementara di memori (ArrayList & HashMap)
    // sebelum di-flush ke database saat perintah SIMPAN_KE_DB dipanggil.
    // =====================================================================
    private static List<Mahasiswa> bufferMahasiswa = new ArrayList<>();
    private static List<Dosen> bufferDosen = new ArrayList<>();
    private static List<MataKuliah> bufferMataKuliah = new ArrayList<>();
    private static List<Nilai> bufferNilai = new ArrayList<>();

    // HashMap untuk lookup cepat di memori (JCF)
    private static Map<String, Mahasiswa> mapMahasiswa = new HashMap<>();
    private static Map<String, MataKuliah> mapMataKuliah = new HashMap<>();

    public static void main(String[] args) {

        // 1. Inisialisasi database (JDBC — buat tabel jika belum ada)
        DatabaseManager.initializeDatabase();

        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================================");
        System.out.println("  SISTEM PENGELOLAAN NILAI & REKAM JEJAK AKADEMIK");
        System.out.println("  Inheritance | JCF | JDBC | Custom ORM");
        System.out.println("========================================================");
        System.out.println("Masukkan perintah (ketik '---' untuk mengakhiri):");
        System.out.println();

        // 2. Loop baca perintah dari user
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            // Sentinel: akhiri program
            if (line.equals("---")) {
                break;
            }

            if (line.isEmpty()) continue;

            // Parse perintah
            String[] token = line.split("#");
            String perintah = token[0].toUpperCase();

            try {
                switch (perintah) {
                    case "TAMBAH_MAHASISWA":
                        handleTambahMahasiswa(token);
                        break;
                    case "TAMBAH_DOSEN":
                        handleTambahDosen(token);
                        break;
                    case "TAMBAH_MATAKULIAH":
                        handleTambahMataKuliah(token);
                        break;
                    case "INPUT_NILAI":
                        handleInputNilai(token);
                        break;
                    case "SIMPAN_KE_DB":
                        handleSimpanKeDB();
                        break;
                    case "CETAK_TRANSKRIP":
                        handleCetakTranskrip(token);
                        break;
                    case "LIHAT_SEMUA":
                        handleLihatSemua();
                        break;
                    default:
                        System.out.println("[WARN] Perintah tidak dikenali: " + perintah);
                }
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }

        // 3. Tutup koneksi database
        DatabaseManager.closeConnection();
        scanner.close();
        System.out.println("\nProgram selesai. Koneksi database ditutup.");
    }

    // =================================================================
    //  HANDLER PERINTAH — masing-masing menggabungkan 4 konsep
    // =================================================================

    /**
     * TAMBAH_MAHASISWA#NIM#Nama#Prodi
     * Inheritance: membuat objek Mahasiswa (turunan CivitasAkademika)
     * JCF: menyimpan ke bufferMahasiswa (ArrayList) & mapMahasiswa (HashMap)
     */
    private static void handleTambahMahasiswa(String[] token) {
        if (token.length < 4) {
            System.out.println("[ERROR] Format: TAMBAH_MAHASISWA#NIM#Nama#Prodi");
            return;
        }
        String nim = token[1];
        String nama = token[2];
        String prodi = token[3];

        // Inheritance: instansiasi subclass Mahasiswa
        Mahasiswa mhs = new Mahasiswa(nim, nama, prodi);

        // JCF: simpan ke ArrayList & HashMap
        bufferMahasiswa.add(mhs);
        mapMahasiswa.put(nim, mhs);

        // Polymorphism: memanggil getPeran() yang di-override
        System.out.println("[OK] " + mhs.getPeran() + " ditambahkan ke buffer: "
                + mhs.getNama() + " (NIM: " + mhs.getNim() + ")");
    }

    /**
     * TAMBAH_DOSEN#NIDN#Nama#Fakultas
     */
    private static void handleTambahDosen(String[] token) {
        if (token.length < 4) {
            System.out.println("[ERROR] Format: TAMBAH_DOSEN#NIDN#Nama#Fakultas");
            return;
        }
        String nidn = token[1];
        String nama = token[2];
        String fakultas = token[3];

        Dosen dsn = new Dosen(nidn, nama, fakultas);
        bufferDosen.add(dsn);

        System.out.println("[OK] " + dsn.getPeran() + " ditambahkan ke buffer: "
                + dsn.getNama() + " (NIDN: " + dsn.getNidn() + ")");
    }

    /**
     * TAMBAH_MATAKULIAH#Kode#Nama#SKS
     */
    private static void handleTambahMataKuliah(String[] token) {
        if (token.length < 4) {
            System.out.println("[ERROR] Format: TAMBAH_MATAKULIAH#Kode#Nama#SKS");
            return;
        }
        String kode = token[1];
        String nama = token[2];
        int sks = Integer.parseInt(token[3]);

        MataKuliah mk = new MataKuliah(kode, nama, sks);
        bufferMataKuliah.add(mk);
        mapMataKuliah.put(kode, mk);

        System.out.println("[OK] Mata kuliah ditambahkan ke buffer: "
                + mk.getNama() + " (" + mk.getKode() + ", " + mk.getSks() + " SKS)");
    }

    /**
     * INPUT_NILAI#NIM#KodeMK#HurufMutu
     */
    private static void handleInputNilai(String[] token) {
        if (token.length < 4) {
            System.out.println("[ERROR] Format: INPUT_NILAI#NIM#KodeMK#HurufMutu");
            return;
        }
        String nim = token[1];
        String kodeMK = token[2];
        String hurufMutu = token[3];

        Nilai nilai = new Nilai(nim, kodeMK, hurufMutu);
        bufferNilai.add(nilai);

        System.out.println("[OK] Nilai ditambahkan ke buffer: NIM "
                + nim + " | MK " + kodeMK + " | Nilai: " + hurufMutu
                + " (bobot: " + nilai.getBobot() + ")");
    }

    /**
     * SIMPAN_KE_DB
     * JDBC + ORM: semua data di buffer (JCF) dipetakan menjadi SQL
     * dan disimpan ke database secara permanen.
     */
    private static void handleSimpanKeDB() throws SQLException {
        System.out.println("\n--- Menyimpan data ke database ---");
        int total = 0;

        // ORM + JDBC: Objek Mahasiswa → INSERT SQL
        for (Mahasiswa mhs : bufferMahasiswa) {
            try {
                AkademikORM.insertMahasiswa(mhs);
                total++;
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY KEY")) {
                    System.out.println("[SKIP] Mahasiswa " + mhs.getNim() + " sudah ada di database.");
                } else {
                    throw e;
                }
            }
        }

        // ORM + JDBC: Objek Dosen → INSERT SQL
        for (Dosen dsn : bufferDosen) {
            try {
                AkademikORM.insertDosen(dsn);
                total++;
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY KEY")) {
                    System.out.println("[SKIP] Dosen " + dsn.getNidn() + " sudah ada di database.");
                } else {
                    throw e;
                }
            }
        }

        // ORM + JDBC: Objek MataKuliah → INSERT SQL
        for (MataKuliah mk : bufferMataKuliah) {
            try {
                AkademikORM.insertMataKuliah(mk);
                total++;
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY KEY")) {
                    System.out.println("[SKIP] Mata kuliah " + mk.getKode() + " sudah ada di database.");
                } else {
                    throw e;
                }
            }
        }

        // ORM + JDBC: Objek Nilai → INSERT/REPLACE SQL
        for (Nilai n : bufferNilai) {
            AkademikORM.insertNilai(n);
            total++;
        }

        System.out.println("[OK] " + total + " record berhasil disimpan ke database.");

        // Bersihkan buffer setelah flush
        bufferMahasiswa.clear();
        bufferDosen.clear();
        bufferMataKuliah.clear();
        bufferNilai.clear();
        System.out.println("[OK] Buffer memori dibersihkan.\n");
    }

    /**
     * CETAK_TRANSKRIP#NIM
     * Menggabungkan semua 4 konsep:
     * - JDBC: query JOIN ke database
     * - ORM: ResultSet → objek Nilai (dengan data MataKuliah)
     * - JCF: hasil ditampung di ArrayList, lalu diiterasi
     * - Inheritance: data mahasiswa diambil sebagai CivitasAkademika
     */
    private static void handleCetakTranskrip(String[] token) throws SQLException {
        if (token.length < 2) {
            System.out.println("[ERROR] Format: CETAK_TRANSKRIP#NIM");
            return;
        }
        String nim = token[1];

        // ORM: query database → objek Mahasiswa
        Mahasiswa mhs = AkademikORM.selectMahasiswaByNim(nim);
        if (mhs == null) {
            System.out.println("[ERROR] Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
            return;
        }

        // ORM + JDBC: query JOIN → List<Nilai> (JCF)
        List<Nilai> daftarNilai = AkademikORM.selectNilaiByNim(nim);

        // Cetak header transkrip
        System.out.println();
        System.out.println("====================================================");
        System.out.println("           TRANSKRIP NILAI AKADEMIK");
        System.out.println("====================================================");
        System.out.println("NIM    : " + mhs.getNim());
        System.out.println("Nama   : " + mhs.getNama());
        System.out.println("Prodi  : " + mhs.getProdi());
        // Polymorphism: memanggil getPeran() dari subclass
        System.out.println("Status : " + mhs.getPeran());
        System.out.println("====================================================");
        System.out.printf("%-10s %-30s %5s %6s %6s%n",
                "Kode", "Mata Kuliah", "SKS", "Nilai", "Bobot");
        System.out.println("----------------------------------------------------");

        // JCF: iterasi ArrayList untuk menghitung total SKS dan total bobot
        int totalSKS = 0;
        double totalBobot = 0.0;

        for (Nilai n : daftarNilai) {
            System.out.printf("%-10s %-30s %3d   %-5s  %.1f%n",
                    n.getKodeMK(),
                    n.getNamaMK(),
                    n.getSks(),
                    n.getHurufMutu(),
                    n.getBobot());
            totalSKS += n.getSks();
            totalBobot += n.getBobot() * n.getSks();
        }

        // Hitung IPK
        double ipk = (totalSKS > 0) ? totalBobot / totalSKS : 0.0;

        System.out.println("----------------------------------------------------");
        System.out.printf("Total SKS : %d%n", totalSKS);
        System.out.printf("IPK       : %.2f%n", ipk);
        System.out.println("====================================================");
        System.out.println();
    }

    /**
     * LIHAT_SEMUA — Menampilkan seluruh data dari database.
     * JCF: data ditampung di ArrayList hasil query ORM.
     */
    private static void handleLihatSemua() throws SQLException {
        System.out.println("\n--- Data Mahasiswa (dari Database) ---");
        // ORM + JCF: ResultSet → ArrayList<Mahasiswa>
        List<Mahasiswa> mahasiswaList = AkademikORM.selectAllMahasiswa();
        if (mahasiswaList.isEmpty()) {
            System.out.println("  (belum ada data)");
        }
        for (Mahasiswa m : mahasiswaList) {
            // Inheritance: memanggil toString() yang di-override
            System.out.println("  " + m);
        }

        System.out.println("\n--- Data Dosen (dari Database) ---");
        List<Dosen> dosenList = AkademikORM.selectAllDosen();
        if (dosenList.isEmpty()) {
            System.out.println("  (belum ada data)");
        }
        for (Dosen d : dosenList) {
            System.out.println("  " + d);
        }

        System.out.println("\n--- Data Mata Kuliah (dari Database) ---");
        List<MataKuliah> mkList = AkademikORM.selectAllMataKuliah();
        if (mkList.isEmpty()) {
            System.out.println("  (belum ada data)");
        }
        for (MataKuliah mk : mkList) {
            System.out.println("  " + mk);
        }
        System.out.println();
    }
}
