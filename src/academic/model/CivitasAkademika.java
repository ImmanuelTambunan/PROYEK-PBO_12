package academic.model;

/**
 * CivitasAkademika - Kelas abstrak induk (parent class).
 * Merepresentasikan anggota civitas akademika (mahasiswa, dosen, dll).
 *
 * Konsep: INHERITANCE — kelas ini diturunkan ke Mahasiswa dan Dosen.
 *         ENCAPSULATION — field private, akses via getter/setter.
 */
public abstract class CivitasAkademika {

    private String id;
    private String nama;
    private String unitKerja; // Prodi untuk mahasiswa, Fakultas untuk dosen

    public CivitasAkademika() {}

    public CivitasAkademika(String id, String nama, String unitKerja) {
        this.id = id;
        this.nama = nama;
        this.unitKerja = unitKerja;
    }

    // --- Getter & Setter ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getUnitKerja() { return unitKerja; }
    public void setUnitKerja(String unitKerja) { this.unitKerja = unitKerja; }

    /**
     * Metode abstrak — setiap subclass wajib mendefinisikan perannya sendiri.
     */
    public abstract String getPeran();

    @Override
    public String toString() {
        return getPeran() + " | " + id + " | " + nama + " | " + unitKerja;
    }
}
