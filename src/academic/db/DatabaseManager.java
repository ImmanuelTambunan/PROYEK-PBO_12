package academic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager — Mengelola koneksi JDBC ke SQLite (Singleton Pattern).
 *
 * KONSEP DITERAPKAN:
 *   [12] JDBC — DriverManager, Connection, Statement untuk DDL.
 *   [15] SOLID (Single Responsibility) — hanya mengurus koneksi & skema DB.
 *   [4]  ENCAPSULATION — constructor private, akses hanya via getConnection().
 *
 * CATATAN PENTING:
 *   - Database TIDAK otomatis dihapus saat program berjalan.
 *   - Penghapusan database dilakukan melalui perintah CLI atau method
 *     dropDatabase() yang harus dipanggil secara eksplisit.
 */
public class DatabaseManager {

    private static final String DB_URL  = "jdbc:sqlite:akademik.db";
    private static final String DB_FILE = "akademik.db";
    private static Connection   connection;

    /** Constructor private — mencegah instansiasi (pure static utility). */
    private DatabaseManager() {}

    /**
     * Mendapatkan koneksi aktif (Singleton).
     * Membuat koneksi baru jika belum ada atau sudah ditutup.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            // Aktifkan foreign key enforcement di SQLite
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    /** Menutup koneksi database dengan aman. */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("[DB] Koneksi database ditutup.");
                }
            } catch (SQLException e) {
                System.err.println("[DB-ERROR] Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }

    /**
     * Inisialisasi skema database (DDL — CREATE TABLE IF NOT EXISTS).
     * Aman dipanggil berulang kali; hanya membuat tabel jika belum ada.
     */
    public static void initializeDatabase() {
        String[] ddlStatements = {
            // Tabel mahasiswa
            "CREATE TABLE IF NOT EXISTS mahasiswa ("
            + " nim     TEXT PRIMARY KEY,"
            + " nama    TEXT NOT NULL,"
            + " prodi   TEXT NOT NULL,"
            + " status  TEXT NOT NULL DEFAULT 'AKTIF'"
            + ")",

            // Tabel dosen
            "CREATE TABLE IF NOT EXISTS dosen ("
            + " nidn     TEXT PRIMARY KEY,"
            + " nama     TEXT NOT NULL,"
            + " fakultas TEXT NOT NULL"
            + ")",

            // Tabel mata_kuliah
            "CREATE TABLE IF NOT EXISTS mata_kuliah ("
            + " kode TEXT PRIMARY KEY,"
            + " nama TEXT NOT NULL,"
            + " sks  INTEGER NOT NULL CHECK(sks > 0)"
            + ")",

            // Tabel nilai (relasi mahasiswa ↔ mata_kuliah)
            "CREATE TABLE IF NOT EXISTS nilai ("
            + " id          INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " nim         TEXT NOT NULL,"
            + " kode_mk     TEXT NOT NULL,"
            + " huruf_mutu  TEXT NOT NULL,"
            + " FOREIGN KEY (nim)     REFERENCES mahasiswa(nim),"
            + " FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode),"
            + " UNIQUE(nim, kode_mk)"
            + ")"
        };

        try (Statement stmt = getConnection().createStatement()) {
            for (String ddl : ddlStatements) {
                stmt.execute(ddl);
            }
            System.out.println("[DB] Database siap. File: " + DB_FILE);
        } catch (SQLException e) {
            System.err.println("[DB-ERROR] Gagal inisialisasi database: " + e.getMessage());
        }
    }

    /**
     * Menghapus file database secara permanen.
     * PERINGATAN: seluruh data akan hilang. Harus dipanggil eksplisit.
     * Koneksi akan ditutup terlebih dahulu sebelum file dihapus.
     */
    public static boolean dropDatabase() {
        closeConnection();
        java.io.File dbFile = new java.io.File(DB_FILE);
        if (dbFile.exists()) {
            boolean deleted = dbFile.delete();
            if (deleted) {
                System.out.println("[DB] Database '" + DB_FILE + "' berhasil dihapus.");
            } else {
                System.err.println("[DB-ERROR] Gagal menghapus file database.");
            }
            return deleted;
        } else {
            System.out.println("[DB] File database tidak ditemukan, tidak ada yang dihapus.");
            return false;
        }
    }

    /** Kembalikan path file database yang digunakan. */
    public static String getDbFilePath() { return DB_FILE; }
}