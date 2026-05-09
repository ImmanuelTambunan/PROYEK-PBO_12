package academic.model;

public class MataKuliah {
    private String kode;
    private String nama;
    private int sks;

    public MataKuliah(String kode, String nama, int sks) {
        this.kode = kode;
        this.nama = nama;
        this.sks = sks;
    }

    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public int getSks() { return sks; }

    public void setNama(String nama) { this.nama = nama; }
    public void setSks(int sks) { this.sks = sks; }

    @Override
    public String toString() {
        return kode + " | " + nama + " | " + sks + " SKS";
    }
}
