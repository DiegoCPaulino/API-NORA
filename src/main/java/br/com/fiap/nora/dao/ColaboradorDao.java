package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Colaborador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_COLABORADOR (NOME, EMAIL, SENHA_HASH, PERFIL, STATUS_COLAB) VALUES (?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_COLABORADOR SET NOME=?, EMAIL=?, SENHA_HASH=?, PERFIL=?, STATUS_COLAB=? WHERE ID_COLABORADOR=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_COLABORADOR WHERE ID_COLABORADOR=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_COLABORADOR ORDER BY ID_COLABORADOR";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_COLABORADOR WHERE ID_COLABORADOR=?";
    private static final String SQL_SELECT_BY_EMAIL =
            "SELECT * FROM TB_COLABORADOR WHERE EMAIL=?";

    public Connection minhaConexao;

    public ColaboradorDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(Colaborador c) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getSenhaHash());
            stmt.setString(4, c.getPerfil());
            stmt.setString(5, c.getStatusColab());
            stmt.executeUpdate();
            return "Colaborador inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir colaborador: " + ex.getMessage();
        }
    }

    public String atualizar(Colaborador c) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getSenhaHash());
            stmt.setString(4, c.getPerfil());
            stmt.setString(5, c.getStatusColab());
            stmt.setLong(6, c.getIdColaborador());
            stmt.executeUpdate();
            return "Colaborador atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar colaborador: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Colaborador removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover colaborador: " + ex.getMessage();
        }
    }

    public List<Colaborador> selecionar() {
        List<Colaborador> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Colaborador buscarPorId(long id) {
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

    public Colaborador buscarPorEmail(String email) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_EMAIL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Colaborador mapear(ResultSet rs) throws SQLException {
        Colaborador c = new Colaborador();
        c.setIdColaborador(rs.getLong("ID_COLABORADOR"));
        c.setNome(rs.getString("NOME"));
        c.setEmail(rs.getString("EMAIL"));
        c.setSenhaHash(rs.getString("SENHA_HASH"));
        c.setPerfil(rs.getString("PERFIL"));
        c.setStatusColab(rs.getString("STATUS_COLAB"));
        Timestamp ts = rs.getTimestamp("DATA_CRIACAO"); if (ts != null) c.setDataCriacao(ts.toLocalDateTime());
        return c;
    }
}
