package academic.repository;

import academic.interfaces.Repository;
import academic.model.AkademikORM;
import academic.model.Nilai;
import academic.record.RingkasanTranskrip;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class NilaiRepository implements Repository<Nilai> {
    private static NilaiRepository instance;
    private NilaiRepository() {}
    public static NilaiRepository getInstance() {
        if (instance == null) instance = new NilaiRepository();
        return instance;
    }
    @Override public void save(Nilai e) throws SQLException { AkademikORM.insertNilai(e); }
    @Override public Optional<Nilai> findById(String id) throws SQLException { return Optional.empty(); }
    @Override public List<Nilai> findAll() throws SQLException { throw new UnsupportedOperationException("Gunakan findByNim(nim)."); }
    @Override public void deleteById(String id) throws SQLException { throw new UnsupportedOperationException("Gunakan deleteByNimAndKode."); }
    @Override public boolean existsById(String id) throws SQLException { return false; }
    public List<Nilai> findByNim(String nim) throws SQLException { return AkademikORM.selectNilaiByNim(nim); }
    public void deleteByNimAndKode(String nim, String kodeMK) throws SQLException { AkademikORM.deleteNilai(nim, kodeMK); }
    public Optional<RingkasanTranskrip> getRingkasanTranskrip(String nim) throws SQLException { return AkademikORM.selectRingkasanTranskrip(nim); }
}