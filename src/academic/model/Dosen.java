package academic.model;

/**
 * Dosen — Subclass dari CivitasAkademika.
 * KONSEP: [2] INHERITANCE, [3] POLYMORPHISM, [4] ENCAPSULATION, [9] NESTED CLASS
 */
public class Dosen extends CivitasAkademika {

    public Dosen() {}
    public Dosen(String nidn, String nama, String fakultas) { super(nidn, nama, fakultas); }

    public String getNidn()     { return getId(); }
    public String getFakultas() { return getUnitKerja(); }

    @Override public String getPeran()              { return "Dosen"; }
    @Override public String getLabelId()            { return "NIDN     "; }
    @Override protected String getUnitKerja_Label() { return "Fakultas "; }

    @Override
    public void cetak() {
        System.out.println("  NIDN     : " + getNidn());
        System.out.println("  Nama     : " + getNama());
        System.out.println("  Fakultas : " + getFakultas());
    }

    @Override
    public String getRingkasan() {
        return String.format("  %-12s | %-28s | %s", getNidn(), getNama(), getFakultas());
    }

    @Override
    public String toString() {
        return "Dosen | NIDN: " + getNidn() + " | Nama: " + getNama() + " | Fakultas: " + getFakultas();
    }

    // =====================================================
    //  [9] NESTED CLASS — Builder Pattern
    // =====================================================
    public static class Builder {
        private final String nidn, nama, fakultas;
        public Builder(String nidn, String nama, String fakultas) {
            this.nidn = nidn; this.nama = nama; this.fakultas = fakultas;
        }
        public Dosen build() {
            Dosen d = new Dosen(nidn, nama, fakultas);
            d.validasi();
            return d;
        }
    }
}