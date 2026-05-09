package academic.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:akademik.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal membuka koneksi database: " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal menutup koneksi: " + e.getMessage());
        }
    }

    private void initializeDatabase() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS mahasiswa (" +
            "nim TEXT PRIMARY KEY, " +
            "nama TEXT NOT NULL, " +
            "prodi TEXT NOT NULL)");

        stmt.execute("CREATE TABLE IF NOT EXISTS dosen (" +
            "nidn TEXT PRIMARY KEY, " +
            "nama TEXT NOT NULL, " +
            "fakultas TEXT NOT NULL)");

        stmt.execute("CREATE TABLE IF NOT EXISTS mata_kuliah (" +
            "kode TEXT PRIMARY KEY, " +
            "nama TEXT NOT NULL, " +
            "sks INTEGER NOT NULL)");

        stmt.execute("CREATE TABLE IF NOT EXISTS nilai (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nim TEXT NOT NULL, " +
            "kode_mk TEXT NOT NULL, " +
            "huruf_mutu TEXT NOT NULL, " +
            "semester INTEGER NOT NULL DEFAULT 1, " +
            "FOREIGN KEY (nim) REFERENCES mahasiswa(nim), " +
            "FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode), " +
            "UNIQUE(nim, kode_mk))");

        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
            "username TEXT PRIMARY KEY, " +
            "password_hash TEXT NOT NULL, " +
            "role TEXT NOT NULL, " +
            "ref_id TEXT)");

        stmt.execute("CREATE TABLE IF NOT EXISTS audit_log (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL, " +
            "action TEXT NOT NULL, " +
            "detail TEXT, " +
            "timestamp TEXT NOT NULL)");

        // Buat akun admin default jika belum ada
        // Password default: admin123 (SHA-256)
        stmt.execute("INSERT OR IGNORE INTO users (username, password_hash, role, ref_id) " +
            "VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', NULL)");

        stmt.close();
    }
}
