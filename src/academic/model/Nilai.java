package academic.model;

import academic.enums.HurufMutu;
import academic.interfaces.Printable;
import academic.interfaces.Validatable;

/**
 * Nilai — Model data nilai mahasiswa per mata kuliah.
 * KONSEP: [4] ENCAPSULATION, [5] INTERFACE, [6] ENUMERATION, [3] POLYMORPHISM
 */
public class Nilai implements Printable, Validatable {

    private String    nim;
    private String    kodeMK;
    private HurufMutu hurufMutu;
    private String    namaMK;  // diisi dari JOIN query
    private int       sks;     // diisi dari JOIN query

    public Nilai() {}

    public Nilai(String nim, String kodeMK, HurufMutu hurufMutu) {
        this.nim = nim; this.kodeMK = kodeMK; this.hurufMutu = hurufMutu;
    }

    public Nilai(String nim, String kodeMK, String hurufMutu) {
        this(nim, kodeMK, HurufMutu.fromString(hurufMutu));
    }

    public String    getNim()                    { return nim; }
    public void      setNim(String nim)          { this.nim = nim; }
    public String    getKodeMK()                 { return kodeMK; }
    public void      setKodeMK(String k)         { this.kodeMK = k; }
    public HurufMutu getHurufMutu()              { return hurufMutu; }
    public void      setHurufMutu(HurufMutu hm)  { this.hurufMutu = hm; }
    public void      setHurufMutu(String hm)     { this.hurufMutu = HurufMutu.fromString(hm); }
    public String    getNamaMK()                 { return namaMK; }
    public void      setNamaMK(String n)         { this.namaMK = n; }
    public int       getSks()                    { return sks; }
    public void      setSks(int s)               { this.sks = s; }
    public double    getBobot()                  { return hurufMutu.getBobot(); }
    public String    getHurufMutuStr()           { return hurufMutu.name(); }

    @Override
    public void cetak() {
        System.out.printf("  %-8s | %-32s | %2d SKS | %-3s | %.1f%n",
            kodeMK, namaMK != null ? namaMK : "-", sks,
            hurufMutu.name(), getBobot());
    }

    @Override
    public String getRingkasan() {
        return String.format("%-8s | %-32s | %2d | %-3s | %.1f",
            kodeMK, namaMK != null ? namaMK : "-", sks, hurufMutu.name(), getBobot());
    }

    @Override
    public void validasi() throws IllegalStateException {
        if (nim == null || nim.isBlank())    throw new IllegalStateException("NIM kosong.");
        if (kodeMK == null || kodeMK.isBlank()) throw new IllegalStateException("Kode MK kosong.");
        if (hurufMutu == null)               throw new IllegalStateException("Huruf mutu kosong.");
    }

    @Override
    public String toString() {
        return kodeMK + " | " + (namaMK != null ? namaMK : "-")
            + " | " + sks + " SKS | " + hurufMutu.name() + " (" + getBobot() + ")";
    }
}