package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Encaminhamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncaminhamentoDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_ENCAMINHAMENTO (ID_PACIENTE, ID_DENTISTA, ID_TRIAGEM, PREV_FOLLOW, STTS_ENCAM, MATCH_AUTO, DIST_KM, PRIORIDADE, METODO_CALCULO, OBSERVACAO) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_ENCAMINHAMENTO SET PREV_FOLLOW=?, STTS_ENCAM=?, DIST_KM=?, OBSERVACAO=? WHERE ID_ENCAMINHAMENTO=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_ENCAMINHAMENTO WHERE ID_ENCAMINHAMENTO=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_ENCAMINHAMENTO ORDER BY ID_ENCAMINHAMENTO";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_ENCAMINHAMENTO WHERE ID_ENCAMINHAMENTO=?";
    private static final String SQL_FOLLOWUP =
            "SELECT * FROM TB_ENCAMINHAMENTO WHERE TRUNC(PREV_FOLLOW) = ? AND STTS_ENCAM = 'ativo'";

    public Connection minhaConexao;

    public EncaminhamentoDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public EncaminhamentoDao(Connection conn) {
        this.minhaConexao = conn;
    }

    // Retorna o ID gerado; usado na transacao de aprovacao
    public long inserirRetornandoId(Encaminhamento e) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT, new String[]{"ID_ENCAMINHAMENTO"})) {
            stmt.setLong(1, e.getIdPaciente());
            stmt.setLong(2, e.getIdDentista());
            stmt.setLong(3, e.getIdTriagem());
            stmt.setTimestamp(4, Timestamp.valueOf(e.getPrevFollow()));
            stmt.setString(5, e.getSttsEncam());
            stmt.setString(6, e.getMatchAuto());
            if (e.getDistKm() != null) stmt.setDouble(7, e.getDistKm()); else stmt.setNull(7, Types.NUMERIC);
            stmt.setString(8, e.getPrioridade());
            stmt.setString(9, e.getMetodoCalculo());
            stmt.setString(10, e.getObservacao());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Nenhum ID gerado ao inserir encaminhamento.");
    }

    public String inserir(Encaminhamento e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, e.getIdPaciente());
            stmt.setLong(2, e.getIdDentista());
            stmt.setLong(3, e.getIdTriagem());
            stmt.setTimestamp(4, Timestamp.valueOf(e.getPrevFollow()));
            stmt.setString(5, e.getSttsEncam());
            stmt.setString(6, e.getMatchAuto());
            if (e.getDistKm() != null) stmt.setDouble(7, e.getDistKm()); else stmt.setNull(7, Types.NUMERIC);
            stmt.setString(8, e.getPrioridade());
            stmt.setString(9, e.getMetodoCalculo());
            stmt.setString(10, e.getObservacao());
            stmt.executeUpdate();
            return "Encaminhamento inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir encaminhamento: " + ex.getMessage();
        }
    }

    public String atualizar(Encaminhamento e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setTimestamp(1, Timestamp.valueOf(e.getPrevFollow()));
            stmt.setString(2, e.getSttsEncam());
            if (e.getDistKm() != null) stmt.setDouble(3, e.getDistKm()); else stmt.setNull(3, Types.NUMERIC);
            stmt.setString(4, e.getObservacao());
            stmt.setLong(5, e.getIdEncaminhamento());
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

    // Usado pelo FollowUpService — retorna encaminhamentos ativos com prev_follow na data alvo
    public List<Encaminhamento> listarParaFollowUp(LocalDate dataAlvo) throws SQLException {
        List<Encaminhamento> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_FOLLOWUP)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataAlvo));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Encaminhamento mapear(ResultSet rs) throws SQLException {
        Encaminhamento e = new Encaminhamento();
        e.setIdEncaminhamento(rs.getLong("ID_ENCAMINHAMENTO"));
        e.setIdPaciente(rs.getLong("ID_PACIENTE"));
        e.setIdDentista(rs.getLong("ID_DENTISTA"));
        e.setIdTriagem(rs.getLong("ID_TRIAGEM"));
        Timestamp te = rs.getTimestamp("DT_ENCAM"); if (te != null) e.setDtEncam(te.toLocalDateTime());
        Timestamp pf = rs.getTimestamp("PREV_FOLLOW"); if (pf != null) e.setPrevFollow(pf.toLocalDateTime());
        e.setSttsEncam(rs.getString("STTS_ENCAM"));
        e.setMatchAuto(rs.getString("MATCH_AUTO"));
        double dk = rs.getDouble("DIST_KM"); e.setDistKm(rs.wasNull() ? null : dk);
        e.setPrioridade(rs.getString("PRIORIDADE"));
        e.setMetodoCalculo(rs.getString("METODO_CALCULO"));
        e.setObservacao(rs.getString("OBSERVACAO"));
        return e;
    }
}
