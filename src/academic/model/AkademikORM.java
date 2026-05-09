package academic.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AkademikORM {
    private Connection conn;

    public AkademikORM() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // =====================================================================
    // MAHASISWA
    // =====================================================================

    public void insertMahasiswa(Mahasiswa m) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO mahasiswa (nim, nama, prodi) VALUES (?, ?, ?)");
        ps.setString(1, m.getNim());
        ps.setString(2, m.getNama());
        ps.setString(3, m.getProdi());
        ps.executeUpdate();
        ps.close();
    }

    public void updateMahasiswa(String nim, String nama, String prodi) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE mahasiswa SET nama=?, prodi=? WHERE nim=?");
        ps.setString(1, nama);
        ps.setString(2, prodi);
        ps.setString(3, nim);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteMahasiswa(String nim) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM mahasiswa WHERE nim=?");
        ps.setString(1, nim);
        ps.executeUpdate();
        ps.close();
    }

    public List<Mahasiswa> selectAllMahasiswa() throws SQLException {
        List<Mahasiswa> list = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM mahasiswa ORDER BY nama");
        while (rs.next()) {
            list.add(new Mahasiswa(rs.getString("nim"), rs.getString("nama"), rs.getString("prodi")));
        }
        rs.close();
        stmt.close();
        return list;
    }

    public Mahasiswa findMahasiswaById(String nim) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM mahasiswa WHERE nim=?");
        ps.setString(1, nim);
        ResultSet rs = ps.executeQuery();
        Mahasiswa m = null;
        if (rs.next()) {
            m = new Mahasiswa(rs.getString("nim"), rs.getString("nama"), rs.getString("prodi"));
        }
        rs.close();
        ps.close();
        return m;
    }

    public List<Mahasiswa> searchMahasiswa(String keyword) throws SQLException {
        List<Mahasiswa> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM mahasiswa WHERE nama LIKE ? OR nim LIKE ? OR prodi LIKE ? ORDER BY nama");
        String q = "%" + keyword + "%";
        ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Mahasiswa(rs.getString("nim"), rs.getString("nama"), rs.getString("prodi")));
        }
        rs.close();
        ps.close();
        return list;
    }

    // =====================================================================
    // DOSEN
    // =====================================================================

    public void insertDosen(Dosen d) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO dosen (nidn, nama, fakultas) VALUES (?, ?, ?)");
        ps.setString(1, d.getNidn());
        ps.setString(2, d.getNama());
        ps.setString(3, d.getFakultas());
        ps.executeUpdate();
        ps.close();
    }

    public void updateDosen(String nidn, String nama, String fakultas) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE dosen SET nama=?, fakultas=? WHERE nidn=?");
        ps.setString(1, nama);
        ps.setString(2, fakultas);
        ps.setString(3, nidn);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteDosen(String nidn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM dosen WHERE nidn=?");
        ps.setString(1, nidn);
        ps.executeUpdate();
        ps.close();
    }

    public List<Dosen> selectAllDosen() throws SQLException {
        List<Dosen> list = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM dosen ORDER BY nama");
        while (rs.next()) {
            list.add(new Dosen(rs.getString("nidn"), rs.getString("nama"), rs.getString("fakultas")));
        }
        rs.close();
        stmt.close();
        return list;
    }

    // =====================================================================
    // MATA KULIAH
    // =====================================================================

    public void insertMataKuliah(MataKuliah mk) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO mata_kuliah (kode, nama, sks) VALUES (?, ?, ?)");
        ps.setString(1, mk.getKode());
        ps.setString(2, mk.getNama());
        ps.setInt(3, mk.getSks());
        ps.executeUpdate();
        ps.close();
    }

    public void updateMataKuliah(String kode, String nama, int sks) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE mata_kuliah SET nama=?, sks=? WHERE kode=?");
        ps.setString(1, nama);
        ps.setInt(2, sks);
        ps.setString(3, kode);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteMataKuliah(String kode) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM mata_kuliah WHERE kode=?");
        ps.setString(1, kode);
        ps.executeUpdate();
        ps.close();
    }

    public List<MataKuliah> selectAllMataKuliah() throws SQLException {
        List<MataKuliah> list = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM mata_kuliah ORDER BY kode");
        while (rs.next()) {
            list.add(new MataKuliah(rs.getString("kode"), rs.getString("nama"), rs.getInt("sks")));
        }
        rs.close();
        stmt.close();
        return list;
    }

    public MataKuliah findMataKuliahById(String kode) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM mata_kuliah WHERE kode=?");
        ps.setString(1, kode);
        ResultSet rs = ps.executeQuery();
        MataKuliah mk = null;
        if (rs.next()) {
            mk = new MataKuliah(rs.getString("kode"), rs.getString("nama"), rs.getInt("sks"));
        }
        rs.close();
        ps.close();
        return mk;
    }

    // =====================================================================
    // NILAI
    // =====================================================================

    public void insertNilai(Nilai n) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT OR REPLACE INTO nilai (nim, kode_mk, huruf_mutu, semester) VALUES (?, ?, ?, ?)");
        ps.setString(1, n.getNim());
        ps.setString(2, n.getKodeMk());
        ps.setString(3, n.getHurufMutu());
        ps.setInt(4, n.getSemester());
        ps.executeUpdate();
        ps.close();
    }

    public void deleteNilai(String nim, String kodeMk) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM nilai WHERE nim=? AND kode_mk=?");
        ps.setString(1, nim);
        ps.setString(2, kodeMk);
        ps.executeUpdate();
        ps.close();
    }

    public List<Nilai> selectNilaiByNim(String nim) throws SQLException {
        List<Nilai> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT n.nim, n.kode_mk, mk.nama, mk.sks, n.huruf_mutu, n.semester " +
            "FROM nilai n JOIN mata_kuliah mk ON n.kode_mk = mk.kode " +
            "WHERE n.nim=? ORDER BY n.semester, n.kode_mk");
        ps.setString(1, nim);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Nilai(
                rs.getString("nim"), rs.getString("kode_mk"), rs.getString("nama"),
                rs.getInt("sks"), rs.getString("huruf_mutu"), rs.getInt("semester")));
        }
        rs.close();
        ps.close();
        return list;
    }

    public List<Nilai> selectNilaiByNimDanSemester(String nim, int semester) throws SQLException {
        List<Nilai> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT n.nim, n.kode_mk, mk.nama, mk.sks, n.huruf_mutu, n.semester " +
            "FROM nilai n JOIN mata_kuliah mk ON n.kode_mk = mk.kode " +
            "WHERE n.nim=? AND n.semester=? ORDER BY n.kode_mk");
        ps.setString(1, nim);
        ps.setInt(2, semester);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Nilai(
                rs.getString("nim"), rs.getString("kode_mk"), rs.getString("nama"),
                rs.getInt("sks"), rs.getString("huruf_mutu"), rs.getInt("semester")));
        }
        rs.close();
        ps.close();
        return list;
    }

    // =====================================================================
    // STATISTIK
    // =====================================================================

    public Map<String, Double> getIpkPerProdi() throws SQLException {
        Map<String, Double> result = new HashMap<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT m.prodi, " +
            "  SUM(CASE n.huruf_mutu WHEN 'A' THEN 4.0 WHEN 'AB' THEN 3.5 WHEN 'B' THEN 3.0 " +
            "    WHEN 'BC' THEN 2.5 WHEN 'C' THEN 2.0 WHEN 'D' THEN 1.0 ELSE 0.0 END * mk.sks) " +
            "  / SUM(mk.sks) AS avg_ipk " +
            "FROM nilai n " +
            "JOIN mahasiswa m ON n.nim = m.nim " +
            "JOIN mata_kuliah mk ON n.kode_mk = mk.kode " +
            "GROUP BY m.prodi");
        while (rs.next()) {
            result.put(rs.getString("prodi"), rs.getDouble("avg_ipk"));
        }
        rs.close();
        stmt.close();
        return result;
    }

    public Map<String, Integer> getDistribusiNilai() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT huruf_mutu, COUNT(*) as jumlah FROM nilai GROUP BY huruf_mutu ORDER BY huruf_mutu");
        while (rs.next()) {
            result.put(rs.getString("huruf_mutu"), rs.getInt("jumlah"));
        }
        rs.close();
        stmt.close();
        return result;
    }

    // =====================================================================
    // USER (AUTH)
    // =====================================================================

    public User findUserByUsername(String username) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        User user = null;
        if (rs.next()) {
            user = new User(
                rs.getString("username"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role")),
                rs.getString("ref_id"));
        }
        rs.close();
        ps.close();
        return user;
    }

    public void insertUser(User u) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO users (username, password_hash, role, ref_id) VALUES (?, ?, ?, ?)");
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getPasswordHash());
        ps.setString(3, u.getRole().name());
        ps.setString(4, u.getRefId());
        ps.executeUpdate();
        ps.close();
    }

    public void deleteUser(String username) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username=?");
        ps.setString(1, username);
        ps.executeUpdate();
        ps.close();
    }

    // =====================================================================
    // AUDIT LOG
    // =====================================================================

    public void insertAuditLog(AuditLog log) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO audit_log (username, action, detail, timestamp) VALUES (?, ?, ?, ?)");
        ps.setString(1, log.getUsername());
        ps.setString(2, log.getAction());
        ps.setString(3, log.getDetail());
        ps.setString(4, log.getTimestamp());
        ps.executeUpdate();
        ps.close();
    }

    public List<AuditLog> selectAuditLog(int limit) throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM audit_log ORDER BY id DESC LIMIT ?");
        ps.setInt(1, limit);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new AuditLog(
                rs.getInt("id"), rs.getString("username"),
                rs.getString("action"), rs.getString("detail"), rs.getString("timestamp")));
        }
        rs.close();
        ps.close();
        return list;
    }
}
