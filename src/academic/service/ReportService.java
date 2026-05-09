package academic.service;

import academic.model.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {

    private static ReportService instance;
    private final AuthService    auth;
    private final AkademikORM   orm;

    private static final DateTimeFormatter FMT_FILE =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ReportService() {
        this.auth = AuthService.getInstance();
        this.orm  = AkademikService.getInstance().getOrm();
    }

    public static ReportService getInstance() {
        if (instance == null) instance = new ReportService();
        return instance;
    }

    // =====================================================================
    // EKSPOR CSV — semua mahasiswa + IPK
    // =====================================================================

    public void eksporCsv(String namaFile) throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN);

        if (namaFile == null || namaFile.isBlank()) {
            namaFile = "ekspor_" + LocalDateTime.now().format(FMT_FILE) + ".csv";
        }

        try {
            List<Mahasiswa> mahasiswaList = orm.selectAllMahasiswa();

            PrintWriter pw = new PrintWriter(new FileWriter(namaFile));
            pw.println("NIM,Nama,Prodi,TotalSKS,IPK");

            for (Mahasiswa m : mahasiswaList) {
                List<Nilai> nilaiList = orm.selectNilaiByNim(m.getNim());
                int    totalSks   = 0;
                double totalBobot = 0;
                for (Nilai n : nilaiList) {
                    totalSks   += n.getSks();
                    totalBobot += n.getBobot() * n.getSks();
                }
                double ipk = totalSks > 0 ? totalBobot / totalSks : 0.0;
                pw.printf("%s,%s,%s,%d,%.2f%n",
                    m.getNim(), m.getNama(), m.getProdi(), totalSks, ipk);
            }
            pw.close();

            System.out.println("[OK] Data berhasil diekspor ke: " + namaFile);
            auth.audit("EKSPOR_CSV", "file=" + namaFile + ", rows=" + mahasiswaList.size());

        } catch (SQLException | IOException e) {
            throw new Exception("Gagal ekspor CSV: " + e.getMessage());
        }
    }

    // =====================================================================
    // EKSPOR TRANSKRIP ke file .txt
    // =====================================================================

    public void eksporTranskrip(String nim) throws Exception {
        auth.requireRole(Role.ADMIN, Role.DOSEN, Role.MAHASISWA);

        // Mahasiswa hanya boleh ekspor transkrip sendiri
        if (auth.getCurrentUser().getRole() == Role.MAHASISWA) {
            String refId = auth.getCurrentUser().getRefId();
            if (refId != null && !refId.equals(nim)) {
                throw new Exception("Akses ditolak. Anda hanya bisa mengekspor transkrip sendiri.");
            }
        }

        try {
            Mahasiswa m = orm.findMahasiswaById(nim);
            if (m == null) {
                System.out.println("[ERROR] Mahasiswa NIM " + nim + " tidak ditemukan.");
                return;
            }

            List<Nilai> nilaiList = orm.selectNilaiByNim(nim);
            String namaFile = "transkrip_" + nim + ".txt";

            PrintWriter pw = new PrintWriter(new FileWriter(namaFile));
            pw.println("====================================================");
            pw.println("           TRANSKRIP NILAI AKADEMIK");
            pw.println("====================================================");
            pw.println("NIM    : " + m.getNim());
            pw.println("Nama   : " + m.getNama());
            pw.println("Prodi  : " + m.getProdi());
            pw.println("Status : " + m.getPeran());
            pw.println("Dicetak: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            pw.println("====================================================");
            pw.printf("%-10s %-35s %4s  %-5s %s%n",
                "Kode", "Mata Kuliah", "SKS", "Nilai", "Bobot");
            pw.println("----------------------------------------------------");

            int    totalSks   = 0;
            double totalBobot = 0;
            for (Nilai n : nilaiList) {
                pw.printf("%-10s %-35s %3d  %-6s %.1f%n",
                    n.getKodeMk(), n.getNamaMk(), n.getSks(), n.getHurufMutu(), n.getBobot());
                totalSks   += n.getSks();
                totalBobot += n.getBobot() * n.getSks();
            }

            pw.println("----------------------------------------------------");
            pw.println("Total SKS : " + totalSks);
            double ipk = totalSks > 0 ? totalBobot / totalSks : 0.0;
            pw.printf("IPK       : %.2f%n", ipk);
            pw.println("====================================================");
            pw.close();

            System.out.println("[OK] Transkrip berhasil diekspor ke: " + namaFile);
            auth.audit("EKSPOR_TRANSKRIP", "NIM=" + nim + ", file=" + namaFile);

        } catch (SQLException | IOException e) {
            throw new Exception("Gagal ekspor transkrip: " + e.getMessage());
        }
    }
}
