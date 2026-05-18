package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Conversa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// A regra de exclusividade de FKs contextuais é responsabilidade da BO — este DAO apenas persiste.
public class ConversaDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_CONVERSA (CANAL_CONV, CONTEXTO, TG_THREAD_ID, ID_PESSOA, ID_PACIENTE, ID_DENTISTA, STTS_CONV, NAO_LIDAS) VALUES (?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_CONVERSA SET CANAL_CONV=?, CONTEXTO=?, TG_THREAD_ID=?, ID_PESSOA=?, ID_PACIENTE=?, ID_DENTISTA=?, STTS_CONV=?, NAO_LIDAS=?, DATA_ATUALIZACAO=SYSTIMESTAMP WHERE ID_CONVERSA=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_CONVERSA WHERE ID_CONVERSA=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_CONVERSA ORDER BY ID_CONVERSA";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_CONVERSA WHERE ID_CONVERSA=?";
    private static final String SQL_SELECT_BY_PESSOA =
            "SELECT * FROM TB_CONVERSA WHERE ID_PESSOA=? ORDER BY DATA_CRIACAO DESC";
    // Migra conversa de 'cadastro' para 'acomp_paciente': limpa FK de pessoa, preenche FK de paciente.
    private static final String SQL_ATUALIZAR_PARA_PACIENTE =
            "UPDATE TB_CONVERSA SET CONTEXTO='acomp_paciente', ID_PESSOA=NULL, ID_PACIENTE=?, DATA_ATUALIZACAO=SYSTIMESTAMP WHERE ID_CONVERSA=?";
    // Upsert lógico: conversa ativa = STTS_CONV='aberta' (único status não-terminal per DDL CHECK constraint)
    private static final String SQL_ATIVA_POR_PESSOA =
            "SELECT * FROM TB_CONVERSA WHERE ID_PESSOA=? AND STTS_CONV='aberta' ORDER BY DATA_CRIACAO DESC";
    private static final String SQL_ATIVA_POR_PACIENTE =
            "SELECT * FROM TB_CONVERSA WHERE ID_PACIENTE=? AND STTS_CONV='aberta' ORDER BY DATA_CRIACAO DESC";
    private static final String SQL_ATIVA_POR_DENTISTA =
            "SELECT * FROM TB_CONVERSA WHERE ID_DENTISTA=? AND STTS_CONV='aberta' ORDER BY DATA_CRIACAO DESC";

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

    public void atualizarParaPaciente(long idConversa, long idPaciente) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ATUALIZAR_PARA_PACIENTE)) {
            stmt.setLong(1, idPaciente);
            stmt.setLong(2, idConversa);
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
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT, new String[]{"ID_CONVERSA"})) {
            stmt.setString(1, c.getCanalConv());
            stmt.setString(2, c.getContexto());
            stmt.setString(3, c.getTgThreadId());
            if (c.getIdPessoa() != null) stmt.setLong(4, c.getIdPessoa()); else stmt.setNull(4, Types.NUMERIC);
            if (c.getIdPaciente() != null) stmt.setLong(5, c.getIdPaciente()); else stmt.setNull(5, Types.NUMERIC);
            if (c.getIdDentista() != null) stmt.setLong(6, c.getIdDentista()); else stmt.setNull(6, Types.NUMERIC);
            stmt.setString(7, c.getSttsConv());
            stmt.setInt(8, c.getNaoLidas());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("Falha ao obter ID gerado para Conversa.");
        }
    }

    public String inserir(Conversa c) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, c.getCanalConv());
            stmt.setString(2, c.getContexto());
            stmt.setString(3, c.getTgThreadId());
            if (c.getIdPessoa() != null) stmt.setLong(4, c.getIdPessoa()); else stmt.setNull(4, Types.NUMERIC);
            if (c.getIdPaciente() != null) stmt.setLong(5, c.getIdPaciente()); else stmt.setNull(5, Types.NUMERIC);
            if (c.getIdDentista() != null) stmt.setLong(6, c.getIdDentista()); else stmt.setNull(6, Types.NUMERIC);
            stmt.setString(7, c.getSttsConv());
            stmt.setInt(8, c.getNaoLidas());
            stmt.executeUpdate();
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
            if (c.getIdPessoa() != null) stmt.setLong(4, c.getIdPessoa()); else stmt.setNull(4, Types.NUMERIC);
            if (c.getIdPaciente() != null) stmt.setLong(5, c.getIdPaciente()); else stmt.setNull(5, Types.NUMERIC);
            if (c.getIdDentista() != null) stmt.setLong(6, c.getIdDentista()); else stmt.setNull(6, Types.NUMERIC);
            stmt.setString(7, c.getSttsConv());
            stmt.setInt(8, c.getNaoLidas());
            stmt.setLong(9, c.getIdConversa());
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
        c.setIdConversa(rs.getLong("ID_CONVERSA"));
        c.setCanalConv(rs.getString("CANAL_CONV"));
        c.setContexto(rs.getString("CONTEXTO"));
        c.setTgThreadId(rs.getString("TG_THREAD_ID"));
        long ip = rs.getLong("ID_PESSOA"); c.setIdPessoa(rs.wasNull() ? null : ip);
        long ipac = rs.getLong("ID_PACIENTE"); c.setIdPaciente(rs.wasNull() ? null : ipac);
        long id = rs.getLong("ID_DENTISTA"); c.setIdDentista(rs.wasNull() ? null : id);
        c.setSttsConv(rs.getString("STTS_CONV"));
        c.setNaoLidas(rs.getInt("NAO_LIDAS"));
        Timestamp tc = rs.getTimestamp("DATA_CRIACAO"); if (tc != null) c.setDataCriacao(tc.toLocalDateTime());
        Timestamp ta = rs.getTimestamp("DATA_ATUALIZACAO"); if (ta != null) c.setDataAtualizacao(ta.toLocalDateTime());
        return c;
    }
}
