package academic.enums;

/**
 * HurufMutu — Enumeration untuk representasi nilai akademik.
 *
 * KONSEP DITERAPKAN:
 *   [6] ENUMERATION — tipe data terbatas, type-safe, membawa data & behavior.
 *   [4] ENCAPSULATION — field private final, akses via getter.
 */
public enum HurufMutu {
    A   (4.0, "Sangat Baik"),
    AB  (3.5, "Antara Sangat Baik dan Baik"),
    B   (3.0, "Baik"),
    BC  (2.5, "Antara Baik dan Cukup"),
    C   (2.0, "Cukup"),
    D   (1.0, "Kurang"),
    E   (0.0, "Tidak Lulus");

    private final double bobot;
    private final String keterangan;

    HurufMutu(double bobot, String keterangan) {
        this.bobot      = bobot;
        this.keterangan = keterangan;
    }

    public double getBobot()      { return bobot; }
    public String getKeterangan() { return keterangan; }

    /** Parse String ke HurufMutu secara aman. */
    public static HurufMutu fromString(String nilai) {
        try {
            return HurufMutu.valueOf(nilai.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Huruf mutu tidak valid: '" + nilai + "'. Gunakan: A, AB, B, BC, C, D, atau E."
            );
        }
    }

    public static boolean isValid(String nilai) {
        try   { fromString(nilai); return true;  }
        catch (IllegalArgumentException e) { return false; }
    }

    @Override
    public String toString() { return name() + " (" + bobot + ")"; }
}