package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Triagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TriagemDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_triag),0)+1 FROM triagem";
    private static final String SQL_INSERT =
            "INSERT INTO triagem (id_triag, fk_pess_id, eleg_triag, prior_triag, sexo_pess, problema_bucal, renda_familiar, nivel_urg_ia, conf_ia, sugestao_ia, obs_triag, dt_triag, stts_triag, decisao) VALUES (?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE triagem SET eleg_triag=?, prior_triag=?, sexo_pess=?, nivel_urg_ia=?, conf_ia=?, sugestao_ia=?, obs_triag=?, stts_triag=?, decisao=? WHERE id_triag=?";
    private static final String SQL_UPDATE_STATUS =
            "UPDATE triagem SET stts_triag=?, decisao=? WHERE id_triag=?";
    private static final String SQL_DELETE =
            "DELETE FROM triagem WHERE id_triag=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM triagem ORDER BY id_triag";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM triagem WHERE id_triag=?";
    private static final String SQL_SELECT_BY_PESSOA =
            "SELECT * FROM triagem WHERE fk_pess_id=? ORDER BY dt_triag DESC";

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
            if (decisao != null) stmt.setString(2, decisao); else stmt.setNull(2, java.sql.Types.VARCHAR);
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    // Retorna o ID gerado; usado em TriagemBO.criar para popular o DTO de resposta
    public long inserirRetornandoId(Triagem t) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_triag.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            if (t.getFkPessId() != null) stmt.setLong(2, t.getFkPessId()); else stmt.setNull(2, java.sql.Types.NUMERIC);
            stmt.setString(3, t.getElegTriag());
            stmt.setString(4, t.getPriorTriag());
            stmt.setString(5, t.getSexoPess());
            stmt.setString(6, t.getProblemaBucal());
            stmt.setString(7, t.getRendaFamiliar());
            if (t.getNivelUrgIa() != null) stmt.setDouble(8, t.getNivelUrgIa()); else stmt.setNull(8, java.sql.Types.NUMERIC);
            if (t.getConfIa() != null) stmt.setDouble(9, t.getConfIa()); else stmt.setNull(9, java.sql.Types.NUMERIC);
            stmt.setString(10, t.getSugestaoIa());
            stmt.setString(11, t.getObsTriag());
            stmt.setString(12, t.getSttsTriag());
            if (t.getDecisao() != null) stmt.setString(13, t.getDecisao()); else stmt.setNull(13, java.sql.Types.VARCHAR);
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Triagem t) {
        try {
            inserirRetornandoId(t);
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
            stmt.setString(6, t.getSugestaoIa());
            stmt.setString(7, t.getObsTriag());
            stmt.setString(8, t.getSttsTriag());
            stmt.setString(9, t.getDecisao());
            stmt.setLong(10, t.getIdTriag());
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

    public List<Triagem> listarPorPessoa(long idPess) {
        List<Triagem> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_PESSOA)) {
            stmt.setLong(1, idPess);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
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
        t.setIdTriag(rs.getLong("id_triag"));
        t.setElegTriag(rs.getString("eleg_triag"));
        t.setPriorTriag(rs.getString("prior_triag"));
        t.setSexoPess(rs.getString("sexo_pess"));
        t.setProblemaBucal(rs.getString("problema_bucal"));
        t.setRendaFamiliar(rs.getString("renda_familiar"));
        double nia = rs.getDouble("nivel_urg_ia"); t.setNivelUrgIa(rs.wasNull() ? null : nia);
        double cia = rs.getDouble("conf_ia"); t.setConfIa(rs.wasNull() ? null : cia);
        t.setSugestaoIa(rs.getString("sugestao_ia"));
        t.setObsTriag(rs.getString("obs_triag"));
        Date dt = rs.getDate("dt_triag"); if (dt != null) t.setDtTriag(dt.toLocalDate());
        t.setSttsTriag(rs.getString("stts_triag"));
        t.setDecisao(rs.getString("decisao"));
        long fp = rs.getLong("fk_pess_id"); t.setFkPessId(rs.wasNull() ? null : fp);
        long fu = rs.getLong("fk_user_id"); t.setFkUserId(rs.wasNull() ? null : fu);
        return t;
    }
}
