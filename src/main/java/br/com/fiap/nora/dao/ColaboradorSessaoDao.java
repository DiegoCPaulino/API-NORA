package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.ColaboradorSessao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorSessaoDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_COLABORADOR_SESSAO (ID_COLABORADOR, TOKEN, DATA_EXPIRACAO, STATUS_SESSAO) VALUES (?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_COLABORADOR_SESSAO SET STATUS_SESSAO=? WHERE ID_SESSAO=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_COLABORADOR_SESSAO WHERE ID_SESSAO=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_COLABORADOR_SESSAO ORDER BY ID_SESSAO";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_COLABORADOR_SESSAO WHERE ID_SESSAO=?";
    private static final String SQL_SELECT_BY_TOKEN =
            "SELECT * FROM TB_COLABORADOR_SESSAO WHERE TOKEN=?";

    public Connection minhaConexao;

    public ColaboradorSessaoDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(ColaboradorSessao s) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, s.getIdColaborador());
            stmt.setString(2, s.getToken());
            stmt.setTimestamp(3, Timestamp.valueOf(s.getDataExpiracao()));
            stmt.setString(4, s.getStatusSessao());
            stmt.executeUpdate();
            return "Sessao inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir sessao: " + ex.getMessage();
        }
    }

    public String atualizar(ColaboradorSessao s) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, s.getStatusSessao());
            stmt.setLong(2, s.getIdSessao());
            stmt.executeUpdate();
            return "Sessao atualizada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar sessao: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Sessao removida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover sessao: " + ex.getMessage();
        }
    }

    public List<ColaboradorSessao> selecionar() {
        List<ColaboradorSessao> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public ColaboradorSessao buscarPorId(long id) {
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

    public ColaboradorSessao buscarPorToken(String token) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_TOKEN)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private ColaboradorSessao mapear(ResultSet rs) throws SQLException {
        ColaboradorSessao s = new ColaboradorSessao();
        s.setIdSessao(rs.getLong("ID_SESSAO"));
        s.setIdColaborador(rs.getLong("ID_COLABORADOR"));
        s.setToken(rs.getString("TOKEN"));
        Timestamp tc = rs.getTimestamp("DATA_CRIACAO"); if (tc != null) s.setDataCriacao(tc.toLocalDateTime());
        Timestamp te = rs.getTimestamp("DATA_EXPIRACAO"); if (te != null) s.setDataExpiracao(te.toLocalDateTime());
        s.setStatusSessao(rs.getString("STATUS_SESSAO"));
        return s;
    }
}
