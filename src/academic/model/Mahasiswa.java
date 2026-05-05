package academic.model;

/**
 * Mahasiswa - Kelas turunan dari CivitasAkademika (INHERITANCE).
 * Merepresentasikan data mahasiswa dengan atribut NIM dan Program Studi.
 */
public class Mahasiswa extends CivitasAkademika {

    public Mahasiswa() {}

    public Mahasiswa(String nim, String nama, String prodi) {
        super(nim, nama, prodi);
    }

    // Alias getter agar lebih intuitif
    public String getNim() { return getId(); }
    public String getProdi() { return getUnitKerja(); }

    @Override
    public String getPeran() {
        return "Mahasiswa";
    }

    @Override
    public String toString() {
        return "Mahasiswa | NIM: " + getNim() + " | Nama: " + getNama() + " | Prodi: " + getProdi();
    }
}
