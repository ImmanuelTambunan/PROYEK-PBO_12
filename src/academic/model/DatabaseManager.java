package academic.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager - Mengelola koneksi JDBC ke database SQLite.
 * 
 * Konsep: JDBC — menggunakan DriverManager untuk membuat koneksi
 *         ke database file-based SQLite (tidak perlu server terpisah).
 *         Menggunakan pattern Singleton agar hanya ada 1 koneksi aktif.
 */
public class DatabaseManager {

    // SQLite menyimpan database sebagai file lokal
    private static final String DB_URL = "jdbc:sqlite:akademik.db";
    private static Connection connection;

    /**
     * Mendapatkan koneksi ke database (Singleton).
     * Jika belum ada koneksi, buat baru.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    /**
     * Menutup koneksi database.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("[ERROR] Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }

    /**
     * Inisialisasi skema database (DDL).
     * Membuat tabel-tabel jika belum ada.
     * Dipanggil sekali saat program pertama kali berjalan.
     */
    public static void initializeDatabase() {
        String sqlMahasiswa = "CREATE TABLE IF NOT EXISTS mahasiswa ("
                + " nim TEXT PRIMARY KEY,"
                + " nama TEXT NOT NULL,"
                + " prodi TEXT NOT NULL"
                + ")";

        String sqlDosen = "CREATE TABLE IF NOT EXISTS dosen ("
                + " nidn TEXT PRIMARY KEY,"
                + " nama TEXT NOT NULL,"
                + " fakultas TEXT NOT NULL"
                + ")";

        String sqlMataKuliah = "CREATE TABLE IF NOT EXISTS mata_kuliah ("
                + " kode TEXT PRIMARY KEY,"
                + " nama TEXT NOT NULL,"
                + " sks INTEGER NOT NULL"
                + ")";

        String sqlNilai = "CREATE TABLE IF NOT EXISTS nilai ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nim TEXT NOT NULL,"
                + " kode_mk TEXT NOT NULL,"
                + " huruf_mutu TEXT NOT NULL,"
                + " FOREIGN KEY (nim) REFERENCES mahasiswa(nim),"
                + " FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode),"
                + " UNIQUE(nim, kode_mk)"
                + ")";

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sqlMahasiswa);
            stmt.execute(sqlDosen);
            stmt.execute(sqlMataKuliah);
            stmt.execute(sqlNilai);
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal inisialisasi database: " + e.getMessage());
        }
    }
}
