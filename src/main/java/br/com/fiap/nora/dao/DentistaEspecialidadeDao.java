package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.DentistaEspecialidade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistaEspecialidadeDao {

    private static final String SQL_INSERT =
            "INSERT INTO dentista_especialidade (fk_dent_id, fk_espec_id) VALUES (?,?)";
    private static final String SQL_DELETE =
            "DELETE FROM dentista_especialidade WHERE fk_dent_id=? AND fk_espec_id=?";
    private static final String SQL_DELETE_BY_DENTISTA =
            "DELETE FROM dentista_especialidade WHERE fk_dent_id=?";
    private static final String SQL_SELECT_BY_DENTISTA =
            "SELECT * FROM dentista_especialidade WHERE fk_dent_id=? ORDER BY fk_espec_id";
    private static final String SQL_SELECT_NOMES_BY_DENTISTA =
            "SELECT e.nm_espec FROM especialidade e " +
            "JOIN dentista_especialidade de ON e.id_espec = de.fk_espec_id " +
            "WHERE de.fk_dent_id=? ORDER BY e.nm_espec";

    public Connection minhaConexao;

    public DentistaEspecialidadeDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public DentistaEspecialidadeDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public String inserir(DentistaEspecialidade de) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, de.getFkDentId());
            stmt.setLong(2, de.getFkEspecId());
            stmt.executeUpdate();
            return "Especialidade vinculada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao vincular especialidade: " + ex.getMessage();
        }
    }

    public String deletar(long idDentista, long idEspecialidade) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, idDentista);
            stmt.setLong(2, idEspecialidade);
            stmt.executeUpdate();
            return "Vinculo removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover vinculo: " + ex.getMessage();
        }
    }

    // Usado dentro de transacao — deixa SQLException propagar para o rollback funcionar
    public void removerPorDentista(long idDentista) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE_BY_DENTISTA)) {
            stmt.setLong(1, idDentista);
            stmt.executeUpdate();
        }
    }

    // Usado dentro de transacao — deixa SQLException propagar para o rollback funcionar
    public void inserirTransacional(DentistaEspecialidade de) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, de.getFkDentId());
            stmt.setLong(2, de.getFkEspecId());
            stmt.executeUpdate();
        }
    }

    public List<String> listarNomesEspecialidadesPorDentista(long idDentista) {
        List<String> nomes = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_NOMES_BY_DENTISTA)) {
            stmt.setLong(1, idDentista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) nomes.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return nomes;
    }

    public List<DentistaEspecialidade> buscarPorDentista(long idDentista) {
        List<DentistaEspecialidade> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_DENTISTA)) {
            stmt.setLong(1, idDentista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    private DentistaEspecialidade mapear(ResultSet rs) throws SQLException {
        DentistaEspecialidade de = new DentistaEspecialidade();
        de.setFkDentId(rs.getLong("fk_dent_id"));
        de.setFkEspecId(rs.getLong("fk_espec_id"));
        return de;
    }
}
