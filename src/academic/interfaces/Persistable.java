package academic.interfaces;

import java.sql.SQLException;

/**
 * Persistable — Kontrak untuk objek yang bisa disimpan/dihapus ke database.
 *
 * KONSEP DITERAPKAN:
 *   [5] INTERFACE — mendefinisikan kontrak tanpa implementasi.
 *   [15] SOLID (Interface Segregation) — interface kecil, fokus pada persistence.
 */
public interface Persistable {
    /**
     * Simpan objek ini ke database.
     * Implementasi menggunakan INSERT atau INSERT OR REPLACE.
     */
    void simpan() throws SQLException;

    /**
     * Hapus objek ini dari database berdasarkan ID-nya.
     */
    void hapus() throws SQLException;
}