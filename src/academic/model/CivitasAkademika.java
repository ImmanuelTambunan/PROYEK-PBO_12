package academic.model;

import academic.interfaces.Printable;
import academic.interfaces.Validatable;

/**
 * CivitasAkademika — Abstract parent class.
 * KONSEP: [1] ABSTRACTION, [2] INHERITANCE, [3] POLYMORPHISM,
 *         [4] ENCAPSULATION, [5] INTERFACE, [15] SOLID LSP
 */
public abstract class CivitasAkademika implements Printable, Validatable {

    private String id;
    private String nama;
    private String unitKerja;

    protected CivitasAkademika() {}
    protected CivitasAkademika(String id, String nama, String unitKerja) {
        this.id = id; this.nama = nama; this.unitKerja = unitKerja;
    }

    public String getId()                      { return id; }
    public void   setId(String id)             { this.id = id; }
    public String getNama()                    { return nama; }
    public void   setNama(String nama)         { this.nama = nama; }
    public String getUnitKerja()               { return unitKerja; }
    public void   setUnitKerja(String uk)      { this.unitKerja = uk; }

    // Abstract methods — wajib di-override subclass [1] ABSTRACTION
    public abstract String getPeran();
    public abstract String getLabelId();
    protected abstract String getUnitKerja_Label();

    // Implementasi Printable [5] INTERFACE + [3] POLYMORPHISM
    @Override
    public String getRingkasan() {
        return String.format("  %-12s | %-28s | %-22s | %s",
            getId(), getNama(), getUnitKerja(), getPeran());
    }

    @Override
    public void cetak() {
        System.out.println("  " + getLabelId() + " : " + getId());
        System.out.println("  Nama         : " + getNama());
        System.out.println("  " + getUnitKerja_Label() + " : " + getUnitKerja());
        System.out.println("  Peran        : " + getPeran());
    }

    // Implementasi Validatable [5] INTERFACE
    @Override
    public void validasi() throws IllegalStateException {
        if (id == null || id.isBlank())
            throw new IllegalStateException(getLabelId() + " tidak boleh kosong.");
        if (nama == null || nama.isBlank())
            throw new IllegalStateException("Nama tidak boleh kosong.");
        if (unitKerja == null || unitKerja.isBlank())
            throw new IllegalStateException(getUnitKerja_Label() + " tidak boleh kosong.");
    }

    @Override
    public String toString() { return getPeran() + " | " + id + " | " + nama + " | " + unitKerja; }
}