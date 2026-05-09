package academic.model;

public abstract class CivitasAkademika {
    protected String id;
    protected String nama;
    protected String unitKerja;

    public CivitasAkademika(String id, String nama, String unitKerja) {
        this.id = id;
        this.nama = nama;
        this.unitKerja = unitKerja;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getUnitKerja() { return unitKerja; }

    public void setNama(String nama) { this.nama = nama; }
    public void setUnitKerja(String unitKerja) { this.unitKerja = unitKerja; }

    public abstract String getPeran();

    @Override
    public String toString() {
        return getPeran() + " | ID: " + id + " | Nama: " + nama + " | Unit: " + unitKerja;
    }
}
