package academic.model;

import academic.interfaces.Printable;
import academic.interfaces.Validatable;

/**
 * MataKuliah — Model data mata kuliah.
 * KONSEP: [4] ENCAPSULATION, [5] INTERFACE, [3] POLYMORPHISM, [9] NESTED CLASS
 */
public class MataKuliah implements Printable, Validatable {

    private String kode;
    private String nama;
    private int    sks;

    public MataKuliah() {}
    public MataKuliah(String kode, String nama, int sks) {
        this.kode = kode; this.nama = nama; this.sks = sks;
    }

    public String getKode()         { return kode; }
    public void   setKode(String k) { this.kode = k; }
    public String getNama()         { return nama; }
    public void   setNama(String n) { this.nama = n; }
    public int    getSks()          { return sks; }
    public void   setSks(int s)     { this.sks = s; }

    @Override
    public void cetak() {
        System.out.println("  Kode : " + kode);
        System.out.println("  Nama : " + nama);
        System.out.println("  SKS  : " + sks);
    }

    @Override
    public String getRingkasan() {
        return String.format("  %-8s | %-35s | %d SKS", kode, nama, sks);
    }

    @Override
    public void validasi() throws IllegalStateException {
        if (kode == null || kode.isBlank())  throw new IllegalStateException("Kode MK kosong.");
        if (nama == null || nama.isBlank())  throw new IllegalStateException("Nama MK kosong.");
        if (sks <= 0 || sks > 6)            throw new IllegalStateException("SKS harus 1-6.");
    }

    @Override
    public String toString() { return kode + " | " + nama + " | " + sks + " SKS"; }

    // =====================================================
    //  [9] NESTED CLASS — Builder Pattern
    // =====================================================
    public static class Builder {
        private final String kode, nama;
        private final int sks;
        public Builder(String kode, String nama, int sks) {
            this.kode = kode; this.nama = nama; this.sks = sks;
        }
        public MataKuliah build() {
            MataKuliah mk = new MataKuliah(kode, nama, sks);
            mk.validasi();
            return mk;
        }
    }
}