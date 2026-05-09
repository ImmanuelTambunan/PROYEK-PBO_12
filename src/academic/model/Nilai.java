package academic.model;

/**
 * Nilai - Merepresentasikan nilai mahasiswa untuk satu mata kuliah.
 * Menyimpan NIM, kode MK, dan huruf mutu (grade).
 * Termasuk logika konversi huruf mutu → bobot (grade point).
 */
public class Nilai {

    private String nim;
    private String kodeMK;
    private String hurufMutu;

    // Field transient (diisi dari JOIN query saat cetak transkrip)
    private String namaMK;
    private int sks;

    public Nilai() {}

    public Nilai(String nim, String kodeMK, String hurufMutu) {
        this.nim = nim;
        this.kodeMK = kodeMK;
        this.hurufMutu = hurufMutu;
    }

    // --- Getter & Setter ---

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getKodeMK() { return kodeMK; }
    public void setKodeMK(String kodeMK) { this.kodeMK = kodeMK; }

    public String getHurufMutu() { return hurufMutu; }
    public void setHurufMutu(String hurufMutu) { this.hurufMutu = hurufMutu; }

    public String getNamaMK() { return namaMK; }
    public void setNamaMK(String namaMK) { this.namaMK = namaMK; }

    public int getSks() { return sks; }
    public void setSks(int sks) { this.sks = sks; }

    /**
     * Mengkonversi huruf mutu menjadi bobot (grade point).
     * Skala: A=4.0, AB=3.5, B=3.0, BC=2.5, C=2.0, D=1.0, E=0.0
     */
    public double getBobot() {
        switch (hurufMutu.toUpperCase()) {
            case "A":  return 4.0;
            case "AB": return 3.5;
            case "B":  return 3.0;
            case "BC": return 2.5;
            case "C":  return 2.0;
            case "D":  return 1.0;
            case "E":  return 0.0;
            default:   return 0.0;
        }
    }

    @Override
    public String toString() {
        return kodeMK + " | " + (namaMK != null ? namaMK : "-") + " | "
             + sks + " SKS | " + hurufMutu + " (" + getBobot() + ")";
    }
}
