package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Encaminhamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncaminhamentoDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_encam),0)+1 FROM encaminhamento";
    private static final String SQL_INSERT =
            "INSERT INTO encaminhamento (id_encam, dt_encam, prior_encam, obs_encam, prev_follow, stts_encam, match_auto, dist_km, fk_pac_id, fk_dent_id) VALUES (?,SYSDATE,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE encaminhamento SET prior_encam=?, obs_encam=?, prev_follow=?, stts_encam=? WHERE id_encam=?";
    private static final String SQL_DELETE =
            "DELETE FROM encaminhamento WHERE id_encam=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM encaminhamento ORDER BY id_encam";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM encaminhamento WHERE id_encam=?";
    private static final String SQL_SELECT_BY_PACIENTE =
            "SELECT * FROM encaminhamento WHERE fk_pac_id=? ORDER BY dt_encam DESC";
    private static final String SQL_SELECT_BY_DENTISTA =
            "SELECT * FROM encaminhamento WHERE fk_dent_id=? ORDER BY dt_encam DESC";
    private static final String SQL_COUNT_ATIVOS_BY_DENTISTA =
            "SELECT COUNT(*) FROM encaminhamento WHERE fk_dent_id=? AND stts_encam='ativo'";
    // <= captura follow-ups atrasados além dos de hoje — mais robusto que igualdade exata
    private static final String SQL_FOLLOWUP =
            "SELECT * FROM encaminhamento WHERE TRUNC(prev_follow) <= TRUNC(SYSDATE) AND stts_encam = 'ativo'";
    private static final String SQL_UPDATE_STATUS_OBS =
            "UPDATE encaminhamento SET stts_encam=?, obs_encam=? WHERE id_encam=?";

    public Connection minhaConexao;

    public EncaminhamentoDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public EncaminhamentoDao(Connection conn) {
        this.minhaConexao = conn;
    }

    // Retorna o ID gerado; usado na transacao de aprovacao
    public long inserirRetornandoId(Encaminhamento e) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_encam.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, e.getPriorEncam());
            stmt.setString(3, e.getObsEncam());
            if (e.getPrevFollow() != null) stmt.setDate(4, Date.valueOf(e.getPrevFollow())); else stmt.setNull(4, Types.DATE);
            stmt.setString(5, e.getSttsEncam());
            stmt.setString(6, e.getMatchAuto());
            if (e.getDistKm() != null) stmt.setDouble(7, e.getDistKm()); else stmt.setNull(7, Types.NUMERIC);
            if (e.getFkPacId() != null) stmt.setLong(8, e.getFkPacId()); else stmt.setNull(8, Types.NUMERIC);
            if (e.getFkDentId() != null) stmt.setLong(9, e.getFkDentId()); else stmt.setNull(9, Types.NUMERIC);
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Encaminhamento e) {
        try {
            inserirRetornandoId(e);
            return "Encaminhamento inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir encaminhamento: " + ex.getMessage();
        }
    }

    public void atualizarStatusObs(long id, String status, String obs) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE_STATUS_OBS)) {
            stmt.setString(1, status);
            if (obs != null) stmt.setString(2, obs); else stmt.setNull(2, Types.VARCHAR);
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    public String atualizar(Encaminhamento e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, e.getPriorEncam());
            stmt.setString(2, e.getObsEncam());
            if (e.getPrevFollow() != null) stmt.setDate(3, Date.valueOf(e.getPrevFollow())); else stmt.setNull(3, Types.DATE);
            stmt.setString(4, e.getSttsEncam());
            stmt.setLong(5, e.getIdEncam());
            stmt.executeUpdate();
            return "Encaminhamento atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar encaminhamento: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Encaminhamento removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover encaminhamento: " + ex.getMessage();
        }
    }

    public List<Encaminhamento> selecionar() {
        List<Encaminhamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Encaminhamento buscarPorId(long id) {
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

    public List<Encaminhamento> listarPorPaciente(long idPac) {
        List<Encaminhamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_PACIENTE)) {
            stmt.setLong(1, idPac);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public int contarAtivosPorDentista(long idDent) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_COUNT_ATIVOS_BY_DENTISTA)) {
            stmt.setLong(1, idDent);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public List<Encaminhamento> listarPorDentista(long idDent) {
        List<Encaminhamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_DENTISTA)) {
            stmt.setLong(1, idDent);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Usado pelo FollowUpService — retorna encaminhamentos ativos com prev_follow <= hoje (inclui atrasados)
    public List<Encaminhamento> listarParaFollowUp() throws SQLException {
        List<Encaminhamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_FOLLOWUP);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Encaminhamento mapear(ResultSet rs) throws SQLException {
        Encaminhamento e = new Encaminhamento();
        e.setIdEncam(rs.getLong("id_encam"));
        Date de = rs.getDate("dt_encam"); if (de != null) e.setDtEncam(de.toLocalDate());
        e.setPriorEncam(rs.getString("prior_encam"));
        e.setObsEncam(rs.getString("obs_encam"));
        Date pf = rs.getDate("prev_follow"); if (pf != null) e.setPrevFollow(pf.toLocalDate());
        e.setSttsEncam(rs.getString("stts_encam"));
        e.setMatchAuto(rs.getString("match_auto"));
        double dk = rs.getDouble("dist_km"); e.setDistKm(rs.wasNull() ? null : dk);
        long fp = rs.getLong("fk_pac_id"); e.setFkPacId(rs.wasNull() ? null : fp);
        long fd = rs.getLong("fk_dent_id"); e.setFkDentId(rs.wasNull() ? null : fd);
        long fu = rs.getLong("fk_user_id"); e.setFkUserId(rs.wasNull() ? null : fu);
        return e;
    }
}
