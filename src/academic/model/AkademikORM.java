package academic.model;

import academic.db.DatabaseManager;
import academic.enums.HurufMutu;
import academic.enums.StatusAkademik;
import academic.record.RingkasanTranskrip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AkademikORM — Custom Object-Relational Mapping.
 * KONSEP: [14] CUSTOM ORM, [12] JDBC, [13] JCF, [7] RECORD,
 *         [15] SOLID SRP & OCP
 */
public class AkademikORM {

    private AkademikORM() {}

    // ===== MAHASISWA =====

    public static void insertMahasiswa(Mahasiswa m) throws SQLException {
        String sql = "INSERT INTO mahasiswa (nim, nama, prodi, status) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, m.getNim());
            ps.setString(2, m.getNama());
            ps.setString(3, m.getProdi());
            ps.setString(4, m.getStatus().name());
            ps.executeUpdate();
        }
    }

    public static void updateStatusMahasiswa(String nim, StatusAkademik status) throws SQLException {
        String sql = "UPDATE mahasiswa SET status=? WHERE nim=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, nim);
            ps.executeUpdate();
        }
    }

    public static void deleteMahasiswa(String nim) throws SQLException {
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("DELETE FROM nilai WHERE nim=?")) {
            ps.setString(1, nim); ps.executeUpdate();
        }
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("DELETE FROM mahasiswa WHERE nim=?")) {
            ps.setString(1, nim); ps.executeUpdate();
        }
    }

    public static Optional<Mahasiswa> selectMahasiswaByNim(String nim) throws SQLException {
        String sql = "SELECT nim,nama,prodi,status FROM mahasiswa WHERE nim=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapMahasiswa(rs));
            }
        }
        return Optional.empty();
    }

    public static List<Mahasiswa> selectAllMahasiswa() throws SQLException {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT nim,nama,prodi,status FROM mahasiswa ORDER BY nim";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapMahasiswa(rs));
        }
        return list;
    }

    public static boolean existsMahasiswa(String nim) throws SQLException {
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("SELECT 1 FROM mahasiswa WHERE nim=?")) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static Mahasiswa mapMahasiswa(ResultSet rs) throws SQLException {
        return new Mahasiswa.Builder(
            rs.getString("nim"), rs.getString("nama"), rs.getString("prodi")
        ).status(rs.getString("status")).build();
    }

    // ===== DOSEN =====

    public static void insertDosen(Dosen d) throws SQLException {
        String sql = "INSERT INTO dosen (nidn,nama,fakultas) VALUES (?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, d.getNidn());
            ps.setString(2, d.getNama());
            ps.setString(3, d.getFakultas());
            ps.executeUpdate();
        }
    }

    public static void deleteDosen(String nidn) throws SQLException {
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("DELETE FROM dosen WHERE nidn=?")) {
            ps.setString(1, nidn); ps.executeUpdate();
        }
    }

    public static Optional<Dosen> selectDosenByNidn(String nidn) throws SQLException {
        String sql = "SELECT nidn,nama,fakultas FROM dosen WHERE nidn=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nidn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapDosen(rs));
            }
        }
        return Optional.empty();
    }

    public static List<Dosen> selectAllDosen() throws SQLException {
        List<Dosen> list = new ArrayList<>();
        String sql = "SELECT nidn,nama,fakultas FROM dosen ORDER BY nidn";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapDosen(rs));
        }
        return list;
    }

    public static boolean existsDosen(String nidn) throws SQLException {
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("SELECT 1 FROM dosen WHERE nidn=?")) {
            ps.setString(1, nidn);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static Dosen mapDosen(ResultSet rs) throws SQLException {
        return new Dosen.Builder(
            rs.getString("nidn"), rs.getString("nama"), rs.getString("fakultas")
        ).build();
    }

    // ===== MATA KULIAH =====

    public static void insertMataKuliah(MataKuliah mk) throws SQLException {
        String sql = "INSERT INTO mata_kuliah (kode,nama,sks) VALUES (?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, mk.getKode());
            ps.setString(2, mk.getNama());
            ps.setInt(3, mk.getSks());
            ps.executeUpdate();
        }
    }

    public static void deleteMataKuliah(String kode) throws SQLException {
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("DELETE FROM nilai WHERE kode_mk=?")) {
            ps.setString(1, kode); ps.executeUpdate();
        }
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("DELETE FROM mata_kuliah WHERE kode=?")) {
            ps.setString(1, kode); ps.executeUpdate();
        }
    }

    public static Optional<MataKuliah> selectMataKuliahByKode(String kode) throws SQLException {
        String sql = "SELECT kode,nama,sks FROM mata_kuliah WHERE kode=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapMataKuliah(rs));
            }
        }
        return Optional.empty();
    }

    public static List<MataKuliah> selectAllMataKuliah() throws SQLException {
        List<MataKuliah> list = new ArrayList<>();
        String sql = "SELECT kode,nama,sks FROM mata_kuliah ORDER BY kode";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapMataKuliah(rs));
        }
        return list;
    }

    public static boolean existsMataKuliah(String kode) throws SQLException {
        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement("SELECT 1 FROM mata_kuliah WHERE kode=?")) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static MataKuliah mapMataKuliah(ResultSet rs) throws SQLException {
        return new MataKuliah.Builder(
            rs.getString("kode"), rs.getString("nama"), rs.getInt("sks")
        ).build();
    }

    // ===== NILAI =====

    public static void insertNilai(Nilai n) throws SQLException {
        String sql = "INSERT OR REPLACE INTO nilai (nim,kode_mk,huruf_mutu) VALUES (?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getNim());
            ps.setString(2, n.getKodeMK());
            ps.setString(3, n.getHurufMutuStr());
            ps.executeUpdate();
        }
    }

    public static void deleteNilai(String nim, String kodeMK) throws SQLException {
        String sql = "DELETE FROM nilai WHERE nim=? AND kode_mk=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nim); ps.setString(2, kodeMK); ps.executeUpdate();
        }
    }

    public static List<Nilai> selectNilaiByNim(String nim) throws SQLException {
        List<Nilai> list = new ArrayList<>();
        String sql = "SELECT n.nim,n.kode_mk,n.huruf_mutu,mk.nama AS nama_mk,mk.sks "
                   + "FROM nilai n JOIN mata_kuliah mk ON n.kode_mk=mk.kode "
                   + "WHERE n.nim=? ORDER BY n.kode_mk";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Nilai n2 = new Nilai();
                    n2.setNim(rs.getString("nim"));
                    n2.setKodeMK(rs.getString("kode_mk"));
                    n2.setHurufMutu(HurufMutu.fromString(rs.getString("huruf_mutu")));
                    n2.setNamaMK(rs.getString("nama_mk"));
                    n2.setSks(rs.getInt("sks"));
                    list.add(n2);
                }
            }
        }
        return list;
    }

    /**
     * Hitung RingkasanTranskrip (Record) untuk satu mahasiswa.
     * KONSEP: [7] RECORD — data carrier immutable
     */
    public static Optional<RingkasanTranskrip> selectRingkasanTranskrip(String nim)
            throws SQLException {
        Optional<Mahasiswa> mhsOpt = selectMahasiswaByNim(nim);
        if (mhsOpt.isEmpty()) return Optional.empty();
        Mahasiswa mhs = mhsOpt.get();
        List<Nilai> daftarNilai = selectNilaiByNim(nim);
        int totalSks = 0; double totalBobot = 0.0;
        for (Nilai n : daftarNilai) {
            totalSks   += n.getSks();
            totalBobot += n.getBobot() * n.getSks();
        }
        double ipk = (totalSks > 0) ? totalBobot / totalSks : 0.0;
        return Optional.of(new RingkasanTranskrip(
            mhs.getNim(), mhs.getNama(), mhs.getProdi(), totalSks, ipk
        ));
    }
}