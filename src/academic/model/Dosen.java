package academic.model;

public class Dosen extends CivitasAkademika {

    public Dosen(String nidn, String nama, String fakultas) {
        super(nidn, nama, fakultas);
    }

    public String getNidn() { return id; }
    public String getFakultas() { return unitKerja; }

    @Override
    public String getPeran() { return "Dosen"; }

    @Override
    public String toString() {
        return "Dosen | NIDN: " + id + " | Nama: " + nama + " | Fakultas: " + unitKerja;
    }
}
