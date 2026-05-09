package academic.model;

public class Mahasiswa extends CivitasAkademika {

    public Mahasiswa(String nim, String nama, String prodi) {
        super(nim, nama, prodi);
    }

    public String getNim() { return id; }
    public String getProdi() { return unitKerja; }

    @Override
    public String getPeran() { return "Mahasiswa"; }

    @Override
    public String toString() {
        return "Mahasiswa | NIM: " + id + " | Nama: " + nama + " | Prodi: " + unitKerja;
    }
}
