package academic.repository;

import academic.interfaces.Repository;
import academic.model.AkademikORM;
import academic.model.Mahasiswa;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MahasiswaRepository implements Repository<Mahasiswa> {
    private static MahasiswaRepository instance;
    private MahasiswaRepository() {}
    public static MahasiswaRepository getInstance() {
        if (instance == null) instance = new MahasiswaRepository();
        return instance;
    }
    @Override public void save(Mahasiswa e) throws SQLException {
        if (existsById(e.getNim())) AkademikORM.updateStatusMahasiswa(e.getNim(), e.getStatus());
        else AkademikORM.insertMahasiswa(e);
    }
    @Override public Optional<Mahasiswa> findById(String nim) throws SQLException { return AkademikORM.selectMahasiswaByNim(nim); }
    @Override public List<Mahasiswa> findAll() throws SQLException { return AkademikORM.selectAllMahasiswa(); }
    @Override public void deleteById(String nim) throws SQLException { AkademikORM.deleteMahasiswa(nim); }
    @Override public boolean existsById(String nim) throws SQLException { return AkademikORM.existsMahasiswa(nim); }
}