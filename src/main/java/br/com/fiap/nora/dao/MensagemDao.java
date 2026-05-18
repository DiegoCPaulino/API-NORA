package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensagemDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_MENSAGEM (ID_CONVERSA, ENVIADO_POR, DIRECAO, CONTEUDO, TIPO_MENSAGEM, LIDA) VALUES (?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_MENSAGEM SET LIDA=? WHERE ID_MENSAGEM=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_MENSAGEM WHERE ID_MENSAGEM=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_MENSAGEM ORDER BY ID_MENSAGEM";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_MENSAGEM WHERE ID_MENSAGEM=?";
    private static final String SQL_SELECT_BY_CONVERSA =
            "SELECT * FROM TB_MENSAGEM WHERE ID_CONVERSA=? ORDER BY DATA_ENVIO";

    public Connection minhaConexao;

    public MensagemDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public long inserirRetornandoId(Mensagem m) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT, new String[]{"ID_MENSAGEM"})) {
            stmt.setLong(1, m.getIdConversa());
            stmt.setString(2, m.getEnviadoPor());
            stmt.setString(3, m.getDirecao());
            stmt.setString(4, m.getConteudo());
            stmt.setString(5, m.getTipoMensagem());
            stmt.setString(6, m.getLida());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("Falha ao obter ID gerado para Mensagem.");
        }
    }

    public List<Mensagem> listarPorConversa(long idConversa) {
        List<Mensagem> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_CONVERSA)) {
            stmt.setLong(1, idConversa);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public String inserir(Mensagem m) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, m.getIdConversa());
            stmt.setString(2, m.getEnviadoPor());
            stmt.setString(3, m.getDirecao());
            stmt.setString(4, m.getConteudo());
            stmt.setString(5, m.getTipoMensagem());
            stmt.setString(6, m.getLida());
            stmt.executeUpdate();
            return "Mensagem inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir mensagem: " + ex.getMessage();
        }
    }

    public String atualizar(Mensagem m) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, m.getLida());
            stmt.setLong(2, m.getIdMensagem());
            stmt.executeUpdate();
            return "Mensagem atualizada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar mensagem: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Mensagem removida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover mensagem: " + ex.getMessage();
        }
    }

    public List<Mensagem> selecionar() {
        List<Mensagem> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Mensagem buscarPorId(long id) {
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

    private Mensagem mapear(ResultSet rs) throws SQLException {
        Mensagem m = new Mensagem();
        m.setIdMensagem(rs.getLong("ID_MENSAGEM"));
        m.setIdConversa(rs.getLong("ID_CONVERSA"));
        m.setEnviadoPor(rs.getString("ENVIADO_POR"));
        m.setDirecao(rs.getString("DIRECAO"));
        m.setConteudo(rs.getString("CONTEUDO"));
        m.setTipoMensagem(rs.getString("TIPO_MENSAGEM"));
        Timestamp te = rs.getTimestamp("DATA_ENVIO"); if (te != null) m.setDataEnvio(te.toLocalDateTime());
        m.setLida(rs.getString("LIDA"));
        return m;
    }
}
