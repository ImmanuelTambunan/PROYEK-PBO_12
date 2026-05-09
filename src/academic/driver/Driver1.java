package academic.driver;

import academic.model.DatabaseManager;
import academic.model.Role;
import academic.service.AkademikService;
import academic.service.AuthService;
import academic.service.ReportService;
import java.util.Scanner;

public class Driver1 {

    public static void main(String[] args) {
        printBanner();

        AuthService auth = AuthService.getInstance();
        AkademikService akademik = AkademikService.getInstance();
        ReportService report = ReportService.getInstance();

        Scanner sc = new Scanner(System.in);
        System.out.println("Masukkan perintah (ketik '---' untuk mengakhiri):\n");

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            if (line.equals("---")) break;

            String[] parts = line.split("#");
            String cmd = parts[0].toUpperCase();

            try {
                switch (cmd) {

                    // ── AUTH ──────────────────────────────────────────────
                    case "LOGIN":
                        // LOGIN#username#password
                        require(parts, 3, "LOGIN#username#password");
                        auth.login(parts[1], parts[2]);
                        break;

                    case "LOGOUT":
                        auth.logout();
                        break;

                    case "TAMBAH_USER":
                        // TAMBAH_USER#username#password#ROLE#refId (refId opsional)
                        require(parts, 4, "TAMBAH_USER#username#password#ROLE[#refId]");
                        Role role = Role.valueOf(parts[3].toUpperCase());
                        String refId = parts.length >= 5 ? parts[4] : null;
                        auth.registerUser(parts[1], parts[2], role, refId);
                        break;

                    case "HAPUS_USER":
                        // HAPUS_USER#username
                        require(parts, 2, "HAPUS_USER#username");
                        auth.hapusUser(parts[1]);
                        break;

                    // ── TAMBAH (ke buffer) ────────────────────────────────
                    case "TAMBAH_MAHASISWA":
                        // TAMBAH_MAHASISWA#NIM#Nama#Prodi
                        require(parts, 4, "TAMBAH_MAHASISWA#NIM#Nama#Prodi");
                        akademik.tambahMahasiswa(parts[1], parts[2], parts[3]);
                        break;

                    case "TAMBAH_DOSEN":
                        // TAMBAH_DOSEN#NIDN#Nama#Fakultas
                        require(parts, 4, "TAMBAH_DOSEN#NIDN#Nama#Fakultas");
                        akademik.tambahDosen(parts[1], parts[2], parts[3]);
                        break;

                    case "TAMBAH_MATAKULIAH":
                        // TAMBAH_MATAKULIAH#Kode#Nama#SKS
                        require(parts, 4, "TAMBAH_MATAKULIAH#Kode#Nama#SKS");
                        akademik.tambahMataKuliah(parts[1], parts[2], Integer.parseInt(parts[3]));
                        break;

                    case "INPUT_NILAI":
                        // INPUT_NILAI#NIM#KodeMK#HurufMutu[#Semester]
                        require(parts, 4, "INPUT_NILAI#NIM#KodeMK#HurufMutu[#Semester]");
                        int semester = parts.length >= 5 ? Integer.parseInt(parts[4]) : 1;
                        akademik.inputNilai(parts[1], parts[2], parts[3], semester);
                        break;

                    case "SIMPAN_KE_DB":
                        akademik.simpanKeDb();
                        break;

                    // ── EDIT ──────────────────────────────────────────────
                    case "EDIT_MAHASISWA":
                        // EDIT_MAHASISWA#NIM#NamaBaru#ProdiBaru
                        require(parts, 4, "EDIT_MAHASISWA#NIM#NamaBaru#ProdiBaru");
                        akademik.editMahasiswa(parts[1], parts[2], parts[3]);
                        break;

                    case "EDIT_DOSEN":
                        // EDIT_DOSEN#NIDN#NamaBaru#FakultasBaru
                        require(parts, 4, "EDIT_DOSEN#NIDN#NamaBaru#FakultasBaru");
                        akademik.editDosen(parts[1], parts[2], parts[3]);
                        break;

                    case "EDIT_MATAKULIAH":
                        // EDIT_MATAKULIAH#Kode#NamaBaru#SksBaru
                        require(parts, 4, "EDIT_MATAKULIAH#Kode#NamaBaru#SksBaru");
                        akademik.editMataKuliah(parts[1], parts[2], Integer.parseInt(parts[3]));
                        break;

                    // ── HAPUS ─────────────────────────────────────────────
                    case "HAPUS_MAHASISWA":
                        // HAPUS_MAHASISWA#NIM
                        require(parts, 2, "HAPUS_MAHASISWA#NIM");
                        akademik.hapusMahasiswa(parts[1]);
                        break;

                    case "HAPUS_NILAI":
                        // HAPUS_NILAI#NIM#KodeMK
                        require(parts, 3, "HAPUS_NILAI#NIM#KodeMK");
                        akademik.hapusNilai(parts[1], parts[2]);
                        break;

                    // ── LIHAT & CARI ──────────────────────────────────────
                    case "LIHAT_SEMUA":
                        akademik.lihatSemua();
                        break;

                    case "CETAK_TRANSKRIP":
                        // CETAK_TRANSKRIP#NIM
                        require(parts, 2, "CETAK_TRANSKRIP#NIM");
                        akademik.cetakTranskrip(parts[1]);
                        break;

                    case "FILTER_MAHASISWA":
                        // FILTER_MAHASISWA#keyword
                        require(parts, 2, "FILTER_MAHASISWA#keyword");
                        akademik.filterMahasiswa(parts[1]);
                        break;

                    case "STATISTIK":
                        akademik.tampilkanStatistik();
                        break;

                    // ── EKSPOR ────────────────────────────────────────────
                    case "EKSPOR_CSV":
                        // EKSPOR_CSV atau EKSPOR_CSV#namafile.csv
                        String csvFile = parts.length >= 2 ? parts[1] : null;
                        report.eksporCsv(csvFile);
                        break;

                    case "EKSPOR_TRANSKRIP":
                        // EKSPOR_TRANSKRIP#NIM
                        require(parts, 2, "EKSPOR_TRANSKRIP#NIM");
                        report.eksporTranskrip(parts[1]);
                        break;

                    // ── AUDIT LOG ─────────────────────────────────────────
                    case "LIHAT_AUDIT":
                        // LIHAT_AUDIT atau LIHAT_AUDIT#20
                        int limit = parts.length >= 2 ? Integer.parseInt(parts[1]) : 20;
                        akademik.lihatAuditLog(limit);
                        break;

                    case "BANTUAN":
                        printBantuan();
                        break;

                    default:
                        System.out.println("[?] Perintah tidak dikenal: " + cmd +
                            ". Ketik BANTUAN untuk daftar perintah.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] Format salah atau nilai tidak valid: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }

        System.out.println("\nProgram selesai. Koneksi database ditutup.");
        if (auth.isLoggedIn()) auth.logout();
        DatabaseManager.getInstance().closeConnection();
    }

    // =====================================================================
    // HELPER
    // =====================================================================

    private static void require(String[] parts, int min, String format) {
        if (parts.length < min) {
            throw new IllegalArgumentException("Format: " + format);
        }
    }

    private static void printBanner() {
        System.out.println("========================================================");
        System.out.println("  SISTEM PENGELOLAAN NILAI & REKAM JEJAK AKADEMIK v2.0");
        System.out.println("  Inheritance | JCF | JDBC | ORM | RBAC | Reporting");
        System.out.println("========================================================");
        System.out.println("  Login default: admin / admin123");
        System.out.println("  Ketik BANTUAN untuk daftar perintah.");
        System.out.println("========================================================\n");
    }

    private static void printBantuan() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              DAFTAR PERINTAH SISTEM                 ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  AUTH                                                ║");
        System.out.println("║  LOGIN#username#password                             ║");
        System.out.println("║  LOGOUT                                              ║");
        System.out.println("║  TAMBAH_USER#username#password#ROLE[#refId]          ║");
        System.out.println("║    ROLE: ADMIN | DOSEN | MAHASISWA                   ║");
        System.out.println("║  HAPUS_USER#username                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  TAMBAH (ke buffer → SIMPAN_KE_DB)                  ║");
        System.out.println("║  TAMBAH_MAHASISWA#NIM#Nama#Prodi                     ║");
        System.out.println("║  TAMBAH_DOSEN#NIDN#Nama#Fakultas                     ║");
        System.out.println("║  TAMBAH_MATAKULIAH#Kode#Nama#SKS                     ║");
        System.out.println("║  INPUT_NILAI#NIM#KodeMK#Nilai[#Semester]             ║");
        System.out.println("║  SIMPAN_KE_DB                                        ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  EDIT & HAPUS (langsung ke DB)                       ║");
        System.out.println("║  EDIT_MAHASISWA#NIM#NamaBaru#ProdiBaru               ║");
        System.out.println("║  EDIT_DOSEN#NIDN#NamaBaru#FakultasBaru               ║");
        System.out.println("║  EDIT_MATAKULIAH#Kode#NamaBaru#SksBaru               ║");
        System.out.println("║  HAPUS_MAHASISWA#NIM                                 ║");
        System.out.println("║  HAPUS_NILAI#NIM#KodeMK                              ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  LIHAT & LAPORAN                                     ║");
        System.out.println("║  LIHAT_SEMUA                                         ║");
        System.out.println("║  CETAK_TRANSKRIP#NIM                                 ║");
        System.out.println("║  FILTER_MAHASISWA#keyword                            ║");
        System.out.println("║  STATISTIK                                           ║");
        System.out.println("║  EKSPOR_CSV[#namafile.csv]                           ║");
        System.out.println("║  EKSPOR_TRANSKRIP#NIM                                ║");
        System.out.println("║  LIHAT_AUDIT[#limit]                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  ---  (akhiri program)                               ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
