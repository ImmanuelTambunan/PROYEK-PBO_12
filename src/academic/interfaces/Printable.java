package academic.interfaces;

/**
 * Printable — Kontrak untuk objek yang bisa dicetak ke konsol.
 *
 * KONSEP DITERAPKAN:
 *   [5] INTERFACE — kontrak behavior cetak.
 *   [15] SOLID (Interface Segregation) — dipisah dari Persistable.
 *   [3] POLYMORPHISM — tiap kelas implementasi mencetak formatnya sendiri.
 */
public interface Printable {
    /**
     * Cetak representasi lengkap objek ini ke konsol.
     */
    void cetak();

    /**
     * Kembalikan representasi ringkas satu baris (untuk daftar).
     */
    String getRingkasan();
}