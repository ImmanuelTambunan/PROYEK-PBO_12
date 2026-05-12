package academic.repository;

import academic.interfaces.Repository;
import academic.model.AkademikORM;
import academic.model.MataKuliah;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MataKuliahRepository implements Repository<MataKuliah> {
    private static MataKuliahRepository instance;
    private MataKuliahRepository() {}
    public static MataKuliahRepository getInstance() {
        if (instance == null) instance = new MataKuliahRepository();
        return instance;
    }
    @Override public void save(MataKuliah e) throws SQLException {
        if (!existsById(e.getKode())) AkademikORM.insertMataKuliah(e);
    }
    @Override public Optional<MataKuliah> findById(String kode) throws SQLException { return AkademikORM.selectMataKuliahByKode(kode); }
    @Override public List<MataKuliah> findAll() throws SQLException { return AkademikORM.selectAllMataKuliah(); }
    @Override public void deleteById(String kode) throws SQLException { AkademikORM.deleteMataKuliah(kode); }
    @Override public boolean existsById(String kode) throws SQLException { return AkademikORM.existsMataKuliah(kode); }
}