package academic.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository<T> — Interface generic untuk operasi CRUD ke database.
 *
 * KONSEP DITERAPKAN:
 *   [5] INTERFACE — mendefinisikan kontrak CRUD.
 *   [8] GENERICS — parameter tipe T membuat interface ini reusable
 *       untuk Mahasiswa, Dosen, MataKuliah, dst tanpa duplikasi kode.
 *   [15] SOLID (Dependency Inversion) — Driver bergantung pada interface ini,
 *       bukan implementasi konkret.
 *   [15] SOLID (Open/Closed) — tambah entity baru cukup buat implementasi baru.
 */
public interface Repository<T> {

    /** Simpan satu entitas (INSERT atau UPDATE). */
    void save(T entity) throws SQLException;

    /** Cari entitas berdasarkan ID-nya. */
    Optional<T> findById(String id) throws SQLException;

    /** Ambil semua entitas dalam tabel. */
    List<T> findAll() throws SQLException;

    /** Hapus entitas berdasarkan ID. */
    void deleteById(String id) throws SQLException;

    /** Cek apakah entitas dengan ID tertentu sudah ada. */
    boolean existsById(String id) throws SQLException;
}