package academic.model;

public class Nilai {
    private String nim;
    private String kodeMk;
    private String namaMk;
    private int sks;
    private String hurufMutu;
    private int semester;

    public Nilai(String nim, String kodeMk, String namaMk, int sks, String hurufMutu, int semester) {
        this.nim = nim;
        this.kodeMk = kodeMk;
        this.namaMk = namaMk;
        this.sks = sks;
        this.hurufMutu = hurufMutu;
        this.semester = semester;
    }

    // Constructor backward-compatible tanpa semester
    public Nilai(String nim, String kodeMk, String namaMk, int sks, String hurufMutu) {
        this(nim, kodeMk, namaMk, sks, hurufMutu, 1);
    }

    public String getNim() { return nim; }
    public String getKodeMk() { return kodeMk; }
    public String getNamaMk() { return namaMk; }
    public int getSks() { return sks; }
    public String getHurufMutu() { return hurufMutu; }
    public int getSemester() { return semester; }

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
        return String.format("%-10s %-35s %3d  %-5s %.1f  (Sem %d)",
            kodeMk, namaMk, sks, hurufMutu, getBobot(), semester);
    }
}
