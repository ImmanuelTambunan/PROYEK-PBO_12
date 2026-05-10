package academic.record;

/**
 * RingkasanTranskrip — Record immutable untuk data hasil kalkulasi transkrip.
 * KONSEP: [7] RECORD, [4] ENCAPSULATION
 *
 * Accessor record Java: nim(), nama(), prodi(), totalSks(), ipk()
 * (bukan getNim(), dll — itu perilaku bawaan Java record)
 */
public record RingkasanTranskrip(
    String nim,
    String nama,
    String prodi,
    int    totalSks,
    double ipk
) {
    /** Compact constructor — validasi saat pembuatan. */
    public RingkasanTranskrip {
        if (nim == null || nim.isBlank()) throw new IllegalArgumentException("NIM kosong.");
        if (totalSks < 0)  throw new IllegalArgumentException("Total SKS negatif.");
        if (ipk < 0.0 || ipk > 4.0) throw new IllegalArgumentException("IPK di luar range.");
    }

    /** Predikat kelulusan berdasarkan IPK. */
    public String getPredikat() {
        if (ipk >= 3.51) return "Dengan Pujian (Cum Laude)";
        if (ipk >= 3.01) return "Sangat Memuaskan";
        if (ipk >= 2.76) return "Memuaskan";
        if (ipk >= 2.00) return "Cukup";
        return "Kurang";
    }

    /** Format IPK dua desimal. */
    public String getIpkFormatted() { return String.format("%.2f", ipk); }
}