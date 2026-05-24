package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Especialidade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_espec),0)+1 FROM especialidade";
    private static final String SQL_INSERT =
            "INSERT INTO especialidade (id_espec, nm_espec, ds_espec) VALUES (?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE especialidade SET nm_espec=?, ds_espec=? WHERE id_espec=?";
    private static final String SQL_DELETE =
            "DELETE FROM especialidade WHERE id_espec=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM especialidade ORDER BY id_espec";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM especialidade WHERE id_espec=?";

    public Connection minhaConexao;

    public EspecialidadeDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public EspecialidadeDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public long inserirRetornandoId(Especialidade e) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_espec.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, e.getNmEspec());
            stmt.setString(3, e.getDsEspec());
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Especialidade e) {
        try {
            inserirRetornandoId(e);
            return "Especialidade inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir especialidade: " + ex.getMessage();
        }
    }

    public String atualizar(Especialidade e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, e.getNmEspec());
            stmt.setString(2, e.getDsEspec());
            stmt.setLong(3, e.getIdEspec());
            stmt.executeUpdate();
            return "Especialidade atualizada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar especialidade: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Especialidade removida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover especialidade: " + ex.getMessage();
        }
    }

    public List<Especialidade> selecionar() {
        List<Especialidade> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Especialidade buscarPorId(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Especialidade mapear(ResultSet rs) throws SQLException {
        Especialidade e = new Especialidade();
        e.setIdEspec(rs.getLong("id_espec"));
        e.setNmEspec(rs.getString("nm_espec"));
        e.setDsEspec(rs.getString("ds_espec"));
        return e;
    }
}
