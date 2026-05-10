package academic.enums;

/**
 * StatusAkademik — Enum status studi mahasiswa.
 *
 * KONSEP DITERAPKAN:
 *   [6] ENUMERATION — konstanta bermakna dengan label tampilan.
 */
public enum StatusAkademik {
    AKTIF    ("Aktif"),
    CUTI     ("Cuti"),
    LULUS    ("Lulus"),
    DO       ("Drop Out");

    private final String label;

    StatusAkademik(String label) { this.label = label; }

    public String getLabel() { return label; }

    public static StatusAkademik fromString(String s) {
        try {
            return StatusAkademik.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status tidak valid: '" + s + "'.");
        }
    }

    @Override
    public String toString() { return label; }
}