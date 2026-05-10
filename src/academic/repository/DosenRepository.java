package academic.repository;

import academic.interfaces.Repository;
import academic.model.AkademikORM;
import academic.model.Dosen;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DosenRepository implements Repository<Dosen> {
    private static DosenRepository instance;
    private DosenRepository() {}
    public static DosenRepository getInstance() {
        if (instance == null) instance = new DosenRepository();
        return instance;
    }
    @Override public void save(Dosen e) throws SQLException {
        if (!existsById(e.getNidn())) AkademikORM.insertDosen(e);
    }
    @Override public Optional<Dosen> findById(String nidn) throws SQLException { return AkademikORM.selectDosenByNidn(nidn); }
    @Override public List<Dosen> findAll() throws SQLException { return AkademikORM.selectAllDosen(); }
    @Override public void deleteById(String nidn) throws SQLException { AkademikORM.deleteDosen(nidn); }
    @Override public boolean existsById(String nidn) throws SQLException { return AkademikORM.existsDosen(nidn); }
}