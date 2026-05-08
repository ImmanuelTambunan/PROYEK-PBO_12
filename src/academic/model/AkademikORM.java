package academic.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AkademikORM - Custom Object-Relational Mapping (tanpa framework eksternal).
 *
 * Konsep: ORM — kelas ini bertugas:
 *   1. Mengubah Objek Java → data SQL (INSERT/UPDATE)
 *   2. Merakit ulang ResultSet dari database → Objek Java (SELECT)
 *
 * Konsep: JCF — menggunakan ArrayList untuk menampung koleksi objek
 *         hasil query sebelum dikembalikan ke pemanggil.
 *
 * Konsep: JDBC — menggunakan PreparedStatement untuk eksekusi query CRUD.
 */
public class AkademikORM {

    // ===================================================================
    //  MAHASISWA — CRUD + ORM Mapping
    // ===================================================================

    /**
     * ORM: Objek Mahasiswa → INSERT SQL
     */
    public static void insertMahasiswa(Mahasiswa mhs) throws SQLException {
        String sql = "INSERT INTO mahasiswa (nim, nama, prodi) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, mhs.getNim());
            ps.setString(2, mhs.getNama());
            ps.setString(3, mhs.getProdi());
            ps.executeUpdate();
        }
    }

    /**
     * ORM: ResultSet → List<Mahasiswa> (JCF ArrayList)
     */
    public static List<Mahasiswa> selectAllMahasiswa() throws SQLException {
        List<Mahasiswa> daftar = new ArrayList<>();
        String sql = "SELECT nim, nama, prodi FROM mahasiswa ORDER BY nim";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Manual ORM: baris ResultSet → objek Mahasiswa
                Mahasiswa mhs = new Mahasiswa();
                mhs.setId(rs.getString("nim"));
                mhs.setNama(rs.getString("nama"));
                mhs.setUnitKerja(rs.getString("prodi"));
                daftar.add(mhs);
            }
        }
        return daftar;
    }

    /**
     * ORM: ResultSet → Mahasiswa (single object, by NIM)
     */
    public static Mahasiswa selectMahasiswaByNim(String nim) throws SQLException {
        String sql = "SELECT nim, nama, prodi FROM mahasiswa WHERE nim = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mahasiswa mhs = new Mahasiswa();
                    mhs.setId(rs.getString("nim"));
                    mhs.setNama(rs.getString("nama"));
                    mhs.setUnitKerja(rs.getString("prodi"));
                    return mhs;
                }
            }
        }
        return null;
    }

    // ===================================================================
    //  DOSEN — CRUD + ORM Mapping
    // ===================================================================

    /**
     * ORM: Objek Dosen → INSERT SQL
     */
    public static void insertDosen(Dosen dsn) throws SQLException {
        String sql = "INSERT INTO dosen (nidn, nama, fakultas) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, dsn.getNidn());
            ps.setString(2, dsn.getNama());
            ps.setString(3, dsn.getFakultas());
            ps.executeUpdate();
        }
    }

    /**
     * ORM: ResultSet → List<Dosen> (JCF ArrayList)
     */
    public static List<Dosen> selectAllDosen() throws SQLException {
        List<Dosen> daftar = new ArrayList<>();
        String sql = "SELECT nidn, nama, fakultas FROM dosen ORDER BY nidn";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Dosen dsn = new Dosen();
                dsn.setId(rs.getString("nidn"));
                dsn.setNama(rs.getString("nama"));
                dsn.setUnitKerja(rs.getString("fakultas"));
                daftar.add(dsn);
            }
        }
        return daftar;
    }

    // ===================================================================
    //  MATA KULIAH — CRUD + ORM Mapping
    // ===================================================================

    /**
     * ORM: Objek MataKuliah → INSERT SQL
     */
    public static void insertMataKuliah(MataKuliah mk) throws SQLException {
        String sql = "INSERT INTO mata_kuliah (kode, nama, sks) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, mk.getKode());
            ps.setString(2, mk.getNama());
            ps.setInt(3, mk.getSks());
            ps.executeUpdate();
        }
    }

    /**
     * ORM: ResultSet → MataKuliah (single, by kode)
     */
    public static MataKuliah selectMataKuliahByKode(String kode) throws SQLException {
        String sql = "SELECT kode, nama, sks FROM mata_kuliah WHERE kode = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MataKuliah mk = new MataKuliah();
                    mk.setKode(rs.getString("kode"));
                    mk.setNama(rs.getString("nama"));
                    mk.setSks(rs.getInt("sks"));
                    return mk;
                }
            }
        }
        return null;
    }

    /**
     * ORM: ResultSet → List<MataKuliah>
     */
    public static List<MataKuliah> selectAllMataKuliah() throws SQLException {
        List<MataKuliah> daftar = new ArrayList<>();
        String sql = "SELECT kode, nama, sks FROM mata_kuliah ORDER BY kode";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MataKuliah mk = new MataKuliah();
                mk.setKode(rs.getString("kode"));
                mk.setNama(rs.getString("nama"));
                mk.setSks(rs.getInt("sks"));
                daftar.add(mk);
            }
        }
        return daftar;
    }

    // ===================================================================
    //  NILAI — CRUD + ORM Mapping
    // ===================================================================

    /**
     * ORM: Objek Nilai → INSERT SQL
     */
    public static void insertNilai(Nilai nilai) throws SQLException {
        String sql = "INSERT OR REPLACE INTO nilai (nim, kode_mk, huruf_mutu) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nilai.getNim());
            ps.setString(2, nilai.getKodeMK());
            ps.setString(3, nilai.getHurufMutu());
            ps.executeUpdate();
        }
    }

    /**
     * ORM: ResultSet (JOIN) → List<Nilai> untuk transkrip.
     * Query ini melakukan JOIN antara tabel nilai dan mata_kuliah
     * sehingga setiap objek Nilai juga terisi namaMK dan sks-nya.
     *
     * Konsep JCF: hasil dikumpulkan ke ArrayList sebelum dikembalikan.
     */
    public static List<Nilai> selectNilaiByNim(String nim) throws SQLException {
        List<Nilai> daftar = new ArrayList<>();
        String sql = "SELECT n.nim, n.kode_mk, n.huruf_mutu, mk.nama AS nama_mk, mk.sks "
                   + "FROM nilai n "
                   + "JOIN mata_kuliah mk ON n.kode_mk = mk.kode "
                   + "WHERE n.nim = ? "
                   + "ORDER BY n.kode_mk";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Manual ORM: ResultSet → Objek Nilai (dengan data JOIN)
                    Nilai n = new Nilai();
                    n.setNim(rs.getString("nim"));
                    n.setKodeMK(rs.getString("kode_mk"));
                    n.setHurufMutu(rs.getString("huruf_mutu"));
                    n.setNamaMK(rs.getString("nama_mk"));
                    n.setSks(rs.getInt("sks"));
                    daftar.add(n);
                }
            }
        }
        return daftar;
    }
}
