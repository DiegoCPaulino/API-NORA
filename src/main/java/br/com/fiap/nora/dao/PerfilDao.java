package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Perfil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_perfil),0)+1 FROM perfil";
    private static final String SQL_INSERT =
            "INSERT INTO perfil (id_perfil, nm_perfil, ds_perfil) VALUES (?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE perfil SET nm_perfil=?, ds_perfil=? WHERE id_perfil=?";
    private static final String SQL_DELETE =
            "DELETE FROM perfil WHERE id_perfil=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM perfil ORDER BY id_perfil";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM perfil WHERE id_perfil=?";

    public Connection minhaConexao;

    public PerfilDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public PerfilDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public long inserirRetornandoId(Perfil p) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_perfil.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, p.getNmPerfil());
            stmt.setString(3, p.getDsPerfil());
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Perfil p) {
        try {
            inserirRetornandoId(p);
            return "Perfil inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir perfil: " + ex.getMessage();
        }
    }

    public String atualizar(Perfil p) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, p.getNmPerfil());
            stmt.setString(2, p.getDsPerfil());
            stmt.setLong(3, p.getIdPerfil());
            stmt.executeUpdate();
            return "Perfil atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar perfil: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Perfil removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover perfil: " + ex.getMessage();
        }
    }

    public List<Perfil> selecionar() {
        List<Perfil> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Perfil buscarPorId(long id) {
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

    private Perfil mapear(ResultSet rs) throws SQLException {
        Perfil p = new Perfil();
        p.setIdPerfil(rs.getLong("id_perfil"));
        p.setNmPerfil(rs.getString("nm_perfil"));
        p.setDsPerfil(rs.getString("ds_perfil"));
        return p;
    }
}
