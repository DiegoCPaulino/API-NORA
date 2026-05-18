package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Especialidade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_ESPECIALIDADE (NOME, DESCRICAO, STATUS_ESP) VALUES (?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_ESPECIALIDADE SET NOME=?, DESCRICAO=?, STATUS_ESP=? WHERE ID_ESPECIALIDADE=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_ESPECIALIDADE WHERE ID_ESPECIALIDADE=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_ESPECIALIDADE ORDER BY ID_ESPECIALIDADE";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_ESPECIALIDADE WHERE ID_ESPECIALIDADE=?";

    public Connection minhaConexao;

    public EspecialidadeDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(Especialidade e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, e.getNome());
            stmt.setString(2, e.getDescricao());
            stmt.setString(3, e.getStatusEsp());
            stmt.executeUpdate();
            return "Especialidade inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir especialidade: " + ex.getMessage();
        }
    }

    public String atualizar(Especialidade e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, e.getNome());
            stmt.setString(2, e.getDescricao());
            stmt.setString(3, e.getStatusEsp());
            stmt.setLong(4, e.getIdEspecialidade());
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
        e.setIdEspecialidade(rs.getLong("ID_ESPECIALIDADE"));
        e.setNome(rs.getString("NOME"));
        e.setDescricao(rs.getString("DESCRICAO"));
        e.setStatusEsp(rs.getString("STATUS_ESP"));
        return e;
    }
}
