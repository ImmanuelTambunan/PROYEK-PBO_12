package academic.service;

import academic.model.AkademikORM;
import academic.model.AuditLog;
import academic.model.Role;
import academic.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthService {

    private static AuthService instance;
    private final AkademikORM orm;
    private User currentUser;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuthService() {
        this.orm = new AkademikORM();
        this.currentUser = null;
    }

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    // ── PUBLIC API ──────────────────────────────────────────────────────

    public void login(String username, String password) throws Exception {
        User user = orm.findUserByUsername(username);
        if (user == null) {
            throw new Exception("Username tidak ditemukan: " + username);
        }
        if (!user.getPasswordHash().equals(hash(password))) {
            throw new Exception("Password salah.");
        }
        this.currentUser = user;
        audit("LOGIN", "Berhasil login sebagai " + user.getRole());
        System.out.println("[OK] Login berhasil. Selamat datang, " + username +
            " (" + user.getRole() + ")");
    }

    public void logout() {
        if (currentUser == null) {
            System.out.println("[INFO] Tidak ada sesi aktif.");
            return;
        }
        audit("LOGOUT", "Logout dari sistem");
        System.out.println("[OK] Logout berhasil. Sampai jumpa, " + currentUser.getUsername() + "!");
        this.currentUser = null;
    }

    public void registerUser(String username, String password, Role role, String refId)
            throws Exception {
        requireRole(Role.ADMIN);
        User newUser = new User(username, hash(password), role, refId);
        orm.insertUser(newUser);
        audit("TAMBAH_USER", "username=" + username + ", role=" + role);
        System.out.println("[OK] User '" + username + "' berhasil didaftarkan dengan role " + role);
    }

    public void hapusUser(String username) throws Exception {
        requireRole(Role.ADMIN);
        if (currentUser.getUsername().equals(username)) {
            throw new Exception("Tidak bisa menghapus akun sendiri.");
        }
        orm.deleteUser(username);
        audit("HAPUS_USER", "username=" + username);
        System.out.println("[OK] User '" + username + "' berhasil dihapus.");
    }

    // ── HELPER ──────────────────────────────────────────────────────────

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Lempar exception jika user belum login atau bukan role yang diizinkan.
     * Bisa dipakai service lain: requireRole(Role.ADMIN)
     */
    public void requireRole(Role... allowedRoles) throws Exception {
        if (currentUser == null) {
            throw new Exception("Anda harus LOGIN terlebih dahulu.");
        }
        for (Role r : allowedRoles) {
            if (currentUser.getRole() == r) return;
        }
        throw new Exception("Akses ditolak. Role Anda (" + currentUser.getRole() +
            ") tidak memiliki izin untuk operasi ini.");
    }

    public void audit(String action, String detail) {
        try {
            String user = currentUser != null ? currentUser.getUsername() : "system";
            orm.insertAuditLog(new AuditLog(user, action, detail, now()));
        } catch (SQLException e) {
            // audit log gagal tidak boleh crash program
            System.err.println("[WARN] Gagal menulis audit log: " + e.getMessage());
        }
    }

    // ── PRIVATE ─────────────────────────────────────────────────────────

    private String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 tidak tersedia", e);
        }
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
