package academic.model;

import academic.enums.StatusAkademik;

/**
 * Mahasiswa — Subclass dari CivitasAkademika.
 * KONSEP: [2] INHERITANCE, [3] POLYMORPHISM, [4] ENCAPSULATION,
 *         [6] ENUMERATION, [9] NESTED CLASS (Builder)
 */
public class Mahasiswa extends CivitasAkademika {

    private StatusAkademik status;

    public Mahasiswa() { this.status = StatusAkademik.AKTIF; }

    public Mahasiswa(String nim, String nama, String prodi) {
        super(nim, nama, prodi);
        this.status = StatusAkademik.AKTIF;
    }

    public Mahasiswa(String nim, String nama, String prodi, StatusAkademik status) {
        super(nim, nama, prodi);
        this.status = status;
    }

    public String getNim()                          { return getId(); }
    public String getProdi()                        { return getUnitKerja(); }
    public StatusAkademik getStatus()               { return status; }
    public void setStatus(StatusAkademik status)    { this.status = status; }

    @Override public String getPeran()              { return "Mahasiswa"; }
    @Override public String getLabelId()            { return "NIM      "; }
    @Override protected String getUnitKerja_Label() { return "Prodi    "; }

    @Override
    public void cetak() {
        System.out.println("  NIM    : " + getNim());
        System.out.println("  Nama   : " + getNama());
        System.out.println("  Prodi  : " + getProdi());
        System.out.println("  Status : " + status.getLabel());
    }

    @Override
    public String getRingkasan() {
        return String.format("  %-12s | %-28s | %-22s | %s",
            getNim(), getNama(), getProdi(), status.getLabel());
    }

    @Override
    public String toString() {
        return "Mahasiswa | NIM: " + getNim() + " | Nama: " + getNama()
            + " | Prodi: " + getProdi() + " | Status: " + status.getLabel();
    }

    // =====================================================
    //  [9] NESTED CLASS — Builder Pattern
    // =====================================================
    public static class Builder {
        private final String nim;
        private final String nama;
        private final String prodi;
        private StatusAkademik status = StatusAkademik.AKTIF;

        public Builder(String nim, String nama, String prodi) {
            this.nim = nim; this.nama = nama; this.prodi = prodi;
        }

        public Builder status(StatusAkademik s)  { this.status = s; return this; }
        public Builder status(String s)           { this.status = StatusAkademik.fromString(s); return this; }

        public Mahasiswa build() {
            Mahasiswa m = new Mahasiswa(nim, nama, prodi, status);
            m.validasi();
            return m;
        }
    }
}