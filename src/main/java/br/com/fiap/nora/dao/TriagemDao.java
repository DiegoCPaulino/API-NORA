package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Triagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TriagemDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_TRIAGEM (ID_PESSOA, IDADE, ELEG_TRIAG, PRIOR_TRIAG, SEXO_PESS, PROBLEMA_BUCAL, RENDA_FAMILIAR, NIVEL_URG_IA, CONF_IA, STTS_TRIAG, DECISAO, OBSERVACOES) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_TRIAGEM SET ELEG_TRIAG=?, PRIOR_TRIAG=?, SEXO_PESS=?, NIVEL_URG_IA=?, CONF_IA=?, STTS_TRIAG=?, DECISAO=?, OBSERVACOES=?, DATA_ATUALIZACAO=SYSTIMESTAMP WHERE ID_TRIAGEM=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_TRIAGEM WHERE ID_TRIAGEM=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_TRIAGEM ORDER BY ID_TRIAGEM";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_TRIAGEM WHERE ID_TRIAGEM=?";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE TB_TRIAGEM SET STTS_TRIAG=?, DECISAO=?, DATA_ATUALIZACAO=SYSTIMESTAMP WHERE ID_TRIAGEM=?";

    public Connection minhaConexao;

    public TriagemDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public TriagemDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public void atualizarStatus(long id, String sttsTriag, String decisao) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE_STATUS)) {
            stmt.setString(1, sttsTriag);
            stmt.setString(2, decisao);
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    public String inserir(Triagem t) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, t.getIdPessoa());
            stmt.setInt(2, t.getIdade());
            stmt.setString(3, t.getElegTriag());
            stmt.setString(4, t.getPriorTriag());
            stmt.setString(5, t.getSexoPess());
            stmt.setString(6, t.getProblemaBucal());
            stmt.setString(7, t.getRendaFamiliar());
            if (t.getNivelUrgIa() != null) stmt.setDouble(8, t.getNivelUrgIa()); else stmt.setNull(8, Types.NUMERIC);
            if (t.getConfIa() != null) stmt.setDouble(9, t.getConfIa()); else stmt.setNull(9, Types.NUMERIC);
            stmt.setString(10, t.getSttsTriag());
            stmt.setString(11, t.getDecisao());
            stmt.setString(12, t.getObservacoes());
            stmt.executeUpdate();
            return "Triagem inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir triagem: " + ex.getMessage();
        }
    }

    public String atualizar(Triagem t) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, t.getElegTriag());
            stmt.setString(2, t.getPriorTriag());
            stmt.setString(3, t.getSexoPess());
            if (t.getNivelUrgIa() != null) stmt.setDouble(4, t.getNivelUrgIa()); else stmt.setNull(4, Types.NUMERIC);
            if (t.getConfIa() != null) stmt.setDouble(5, t.getConfIa()); else stmt.setNull(5, Types.NUMERIC);
            stmt.setString(6, t.getSttsTriag());
            stmt.setString(7, t.getDecisao());
            stmt.setString(8, t.getObservacoes());
            stmt.setLong(9, t.getIdTriagem());
            stmt.executeUpdate();
            return "Triagem atualizada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar triagem: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Triagem removida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover triagem: " + ex.getMessage();
        }
    }

    public List<Triagem> selecionar() {
        List<Triagem> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Triagem buscarPorId(long id) {
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

    private Triagem mapear(ResultSet rs) throws SQLException {
        Triagem t = new Triagem();
        t.setIdTriagem(rs.getLong("ID_TRIAGEM"));
        t.setIdPessoa(rs.getLong("ID_PESSOA"));
        t.setIdade(rs.getInt("IDADE"));
        t.setElegTriag(rs.getString("ELEG_TRIAG"));
        t.setPriorTriag(rs.getString("PRIOR_TRIAG"));
        t.setSexoPess(rs.getString("SEXO_PESS"));
        t.setProblemaBucal(rs.getString("PROBLEMA_BUCAL"));
        t.setRendaFamiliar(rs.getString("RENDA_FAMILIAR"));
        double nia = rs.getDouble("NIVEL_URG_IA"); t.setNivelUrgIa(rs.wasNull() ? null : nia);
        double cia = rs.getDouble("CONF_IA"); t.setConfIa(rs.wasNull() ? null : cia);
        t.setSttsTriag(rs.getString("STTS_TRIAG"));
        t.setDecisao(rs.getString("DECISAO"));
        t.setObservacoes(rs.getString("OBSERVACOES"));
        Timestamp tc = rs.getTimestamp("DATA_CRIACAO"); if (tc != null) t.setDataCriacao(tc.toLocalDateTime());
        Timestamp ta = rs.getTimestamp("DATA_ATUALIZACAO"); if (ta != null) t.setDataAtualizacao(ta.toLocalDateTime());
        return t;
    }
}
