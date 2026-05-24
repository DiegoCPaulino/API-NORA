package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Conversa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// A regra de exclusividade de FKs contextuais e responsabilidade da BO — este DAO apenas persiste.
public class ConversaDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_conv),0)+1 FROM conversa";
    private static final String SQL_INSERT =
            "INSERT INTO conversa (id_conv, canal_conv, contexto, iniciado_por, dt_inicio, stts_conv, tg_thread_id, fk_pess_id, fk_pac_id, fk_dent_id) VALUES (?,?,?,?,SYSDATE,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE conversa SET canal_conv=?, contexto=?, tg_thread_id=?, fk_pess_id=?, fk_pac_id=?, fk_dent_id=?, stts_conv=? WHERE id_conv=?";
    private static final String SQL_DELETE =
            "DELETE FROM conversa WHERE id_conv=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM conversa ORDER BY id_conv";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM conversa WHERE id_conv=?";
    private static final String SQL_SELECT_BY_PESSOA =
            "SELECT * FROM conversa WHERE fk_pess_id=? ORDER BY dt_inicio DESC";
    // Migra conversa de 'cadastro' para 'acomp_paciente': limpa FK de pessoa, preenche FK de paciente.
    private static final String SQL_ATUALIZAR_PARA_PACIENTE =
            "UPDATE conversa SET contexto='acomp_paciente', fk_pess_id=NULL, fk_pac_id=? WHERE id_conv=?";
    private static final String SQL_ATIVA_POR_PESSOA =
            "SELECT * FROM conversa WHERE fk_pess_id=? AND stts_conv='aberta' ORDER BY dt_inicio DESC";
    private static final String SQL_ATIVA_POR_PACIENTE =
            "SELECT * FROM conversa WHERE fk_pac_id=? AND stts_conv='aberta' ORDER BY dt_inicio DESC";
    private static final String SQL_ATIVA_POR_DENTISTA =
            "SELECT * FROM conversa WHERE fk_dent_id=? AND stts_conv='aberta' ORDER BY dt_inicio DESC";

    public Connection minhaConexao;

    public ConversaDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public ConversaDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public Conversa buscarPorPessoa(long idPessoa) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_PESSOA)) {
            stmt.setLong(1, idPessoa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void atualizarParaPaciente(long idConv, long idPaciente) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ATUALIZAR_PARA_PACIENTE)) {
            stmt.setLong(1, idPaciente);
            stmt.setLong(2, idConv);
            stmt.executeUpdate();
        }
    }

    public Conversa buscarAtivaPorPessoa(long idPessoa) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ATIVA_POR_PESSOA)) {
            stmt.setLong(1, idPessoa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Conversa buscarAtivaPorPaciente(long idPaciente) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ATIVA_POR_PACIENTE)) {
            stmt.setLong(1, idPaciente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Conversa buscarAtivaPorDentista(long idDentista) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ATIVA_POR_DENTISTA)) {
            stmt.setLong(1, idDentista);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public long inserirRetornandoId(Conversa c) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_conv.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, c.getCanalConv());
            stmt.setString(3, c.getContexto());
            String iniciadoPor = c.getIniciadoPor() != null ? c.getIniciadoPor() : "nora_ia";
            stmt.setString(4, iniciadoPor);
            stmt.setString(5, c.getSttsConv());
            stmt.setString(6, c.getTgThreadId());
            if (c.getFkPessId() != null) stmt.setLong(7, c.getFkPessId()); else stmt.setNull(7, Types.NUMERIC);
            if (c.getFkPacId() != null) stmt.setLong(8, c.getFkPacId()); else stmt.setNull(8, Types.NUMERIC);
            if (c.getFkDentId() != null) stmt.setLong(9, c.getFkDentId()); else stmt.setNull(9, Types.NUMERIC);
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Conversa c) {
        try {
            inserirRetornandoId(c);
            return "Conversa inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir conversa: " + ex.getMessage();
        }
    }

    public String atualizar(Conversa c) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, c.getCanalConv());
            stmt.setString(2, c.getContexto());
            stmt.setString(3, c.getTgThreadId());
            if (c.getFkPessId() != null) stmt.setLong(4, c.getFkPessId()); else stmt.setNull(4, Types.NUMERIC);
            if (c.getFkPacId() != null) stmt.setLong(5, c.getFkPacId()); else stmt.setNull(5, Types.NUMERIC);
            if (c.getFkDentId() != null) stmt.setLong(6, c.getFkDentId()); else stmt.setNull(6, Types.NUMERIC);
            stmt.setString(7, c.getSttsConv());
            stmt.setLong(8, c.getIdConv());
            stmt.executeUpdate();
            return "Conversa atualizada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar conversa: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Conversa removida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover conversa: " + ex.getMessage();
        }
    }

    public List<Conversa> selecionar() {
        List<Conversa> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Conversa buscarPorId(long id) {
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

    private Conversa mapear(ResultSet rs) throws SQLException {
        Conversa c = new Conversa();
        c.setIdConv(rs.getLong("id_conv"));
        c.setCanalConv(rs.getString("canal_conv"));
        c.setContexto(rs.getString("contexto"));
        c.setIniciadoPor(rs.getString("iniciado_por"));
        Date di = rs.getDate("dt_inicio"); if (di != null) c.setDtInicio(di.toLocalDate());
        c.setSttsConv(rs.getString("stts_conv"));
        c.setTgThreadId(rs.getString("tg_thread_id"));
        long fp = rs.getLong("fk_pess_id"); c.setFkPessId(rs.wasNull() ? null : fp);
        long fpac = rs.getLong("fk_pac_id"); c.setFkPacId(rs.wasNull() ? null : fpac);
        long fd = rs.getLong("fk_dent_id"); c.setFkDentId(rs.wasNull() ? null : fd);
        long fu = rs.getLong("fk_user_id"); c.setFkUserId(rs.wasNull() ? null : fu);
        return c;
    }
}
