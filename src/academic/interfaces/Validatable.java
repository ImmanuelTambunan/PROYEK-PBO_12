package academic.interfaces;

/**
 * Validatable — Kontrak untuk objek yang bisa memvalidasi dirinya sendiri.
 *
 * KONSEP DITERAPKAN:
 *   [5] INTERFACE — kontrak validasi data.
 *   [15] SOLID (Single Responsibility) — validasi dipisah dari persistence.
 */
public interface Validatable {
    /**
     * Validasi seluruh field objek ini.
     * @throws IllegalStateException jika ada field yang tidak valid.
     */
    void validasi() throws IllegalStateException;

    /**
     * Cek apakah objek ini valid tanpa melempar exception.
     */
    default boolean isValid() {
        try {
            validasi();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}