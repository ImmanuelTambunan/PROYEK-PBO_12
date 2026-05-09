package academic.model;

/**
 * Dosen - Kelas turunan dari CivitasAkademika (INHERITANCE).
 * Merepresentasikan data dosen dengan atribut NIDN dan Fakultas.
 */
public class Dosen extends CivitasAkademika {

    public Dosen() {}

    public Dosen(String nidn, String nama, String fakultas) {
        super(nidn, nama, fakultas);
    }

    // Alias getter
    public String getNidn() { return getId(); }
    public String getFakultas() { return getUnitKerja(); }

    @Override
    public String getPeran() {
        return "Dosen";
    }

    @Override
    public String toString() {
        return "Dosen | NIDN: " + getNidn() + " | Nama: " + getNama() + " | Fakultas: " + getFakultas();
    }
}
