package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.AcompEvento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcompEventoDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_ACOMP_EVENTO (ID_ENCAMINHAMENTO, TIPO_EVENTO, DS_EVENTO, ORIGEM, RESUMO_IA, TIPO_MENSAGEM) VALUES (?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_ACOMP_EVENTO SET DS_EVENTO=?, RESUMO_IA=? WHERE ID_ACOMP_EVENTO=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_ACOMP_EVENTO WHERE ID_ACOMP_EVENTO=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_ACOMP_EVENTO ORDER BY ID_ACOMP_EVENTO";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_ACOMP_EVENTO WHERE ID_ACOMP_EVENTO=?";
    private static final String SQL_SELECT_BY_ENCAM =
            "SELECT * FROM TB_ACOMP_EVENTO WHERE ID_ENCAMINHAMENTO=? ORDER BY DATA_EVENTO ASC";

    public Connection minhaConexao;

    public AcompEventoDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public long inserirRetornandoId(AcompEvento a) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT, new String[]{"ID_ACOMP_EVENTO"})) {
            stmt.setLong(1, a.getIdEncaminhamento());
            stmt.setString(2, a.getTipoEvento());
            stmt.setString(3, a.getDsEvento());
            stmt.setString(4, a.getOrigem());
            stmt.setString(5, a.getResumoIa());
            stmt.setString(6, a.getTipoMensagem());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("Falha ao obter ID gerado para AcompEvento.");
        }
    }

    public List<AcompEvento> listarPorEncaminhamento(long idEncaminhamento) {
        List<AcompEvento> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_ENCAM)) {
            stmt.setLong(1, idEncaminhamento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public String inserir(AcompEvento a) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, a.getIdEncaminhamento());
            stmt.setString(2, a.getTipoEvento());
            stmt.setString(3, a.getDsEvento());
            stmt.setString(4, a.getOrigem());
            stmt.setString(5, a.getResumoIa());
            stmt.setString(6, a.getTipoMensagem());
            stmt.executeUpdate();
            return "Evento inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir evento: " + ex.getMessage();
        }
    }

    public String atualizar(AcompEvento a) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, a.getDsEvento());
            stmt.setString(2, a.getResumoIa());
            stmt.setLong(3, a.getIdAcompEvento());
            stmt.executeUpdate();
            return "Evento atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar evento: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Evento removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover evento: " + ex.getMessage();
        }
    }

    public List<AcompEvento> selecionar() {
        List<AcompEvento> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public AcompEvento buscarPorId(long id) {
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

    private AcompEvento mapear(ResultSet rs) throws SQLException {
        AcompEvento a = new AcompEvento();
        a.setIdAcompEvento(rs.getLong("ID_ACOMP_EVENTO"));
        a.setIdEncaminhamento(rs.getLong("ID_ENCAMINHAMENTO"));
        a.setTipoEvento(rs.getString("TIPO_EVENTO"));
        a.setDsEvento(rs.getString("DS_EVENTO"));
        a.setOrigem(rs.getString("ORIGEM"));
        a.setResumoIa(rs.getString("RESUMO_IA"));
        a.setTipoMensagem(rs.getString("TIPO_MENSAGEM"));
        Timestamp td = rs.getTimestamp("DATA_EVENTO"); if (td != null) a.setDataEvento(td.toLocalDateTime());
        return a;
    }
}
