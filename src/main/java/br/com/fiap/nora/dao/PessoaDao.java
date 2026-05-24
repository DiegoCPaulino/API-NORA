package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Pessoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_pess),0)+1 FROM pessoa";
    private static final String SQL_INSERT =
            "INSERT INTO pessoa (id_pess, nm_pess, cpf_pess, rg_pess, tel_pess, email_pess, dt_nasc, tg_chat_id, canal_orig, dt_cad, stts_pess, fk_end_id) VALUES (?,?,?,?,?,?,?,?,?,SYSDATE,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE pessoa SET nm_pess=?, cpf_pess=?, rg_pess=?, tel_pess=?, email_pess=?, dt_nasc=?, tg_chat_id=?, canal_orig=?, stts_pess=?, fk_end_id=? WHERE id_pess=?";
    private static final String SQL_UPDATE_STATUS =
            "UPDATE pessoa SET stts_pess=? WHERE id_pess=?";
    private static final String SQL_DELETE =
            "DELETE FROM pessoa WHERE id_pess=?";
    private static final String SQL_EXISTS_BY_CPF =
            "SELECT COUNT(*) FROM pessoa WHERE cpf_pess=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM pessoa ORDER BY id_pess";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM pessoa WHERE id_pess=?";

    public Connection minhaConexao;

    public PessoaDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public PessoaDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public long inserirRetornandoId(Pessoa p) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_pess.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, p.getNmPess());
            stmt.setString(3, p.getCpfPess());
            stmt.setString(4, p.getRgPess());
            stmt.setString(5, p.getTelPess());
            stmt.setString(6, p.getEmailPess());
            stmt.setDate(7, Date.valueOf(p.getDtNasc()));
            stmt.setString(8, p.getTgChatId());
            stmt.setString(9, p.getCanalOrig());
            stmt.setString(10, p.getSttsPess());
            if (p.getFkEndId() != null) stmt.setLong(11, p.getFkEndId()); else stmt.setNull(11, Types.NUMERIC);
            stmt.executeUpdate();
        }
        return novoId;
    }

    public void atualizarStatus(long id, String sttsPess) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE_STATUS)) {
            stmt.setString(1, sttsPess);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public String inserir(Pessoa p) {
        try {
            inserirRetornandoId(p);
            return "Pessoa inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir pessoa: " + ex.getMessage();
        }
    }

    public String atualizar(Pessoa p) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, p.getNmPess());
            stmt.setString(2, p.getCpfPess());
            stmt.setString(3, p.getRgPess());
            stmt.setString(4, p.getTelPess());
            stmt.setString(5, p.getEmailPess());
            stmt.setDate(6, Date.valueOf(p.getDtNasc()));
            stmt.setString(7, p.getTgChatId());
            stmt.setString(8, p.getCanalOrig());
            stmt.setString(9, p.getSttsPess());
            if (p.getFkEndId() != null) stmt.setLong(10, p.getFkEndId()); else stmt.setNull(10, Types.NUMERIC);
            stmt.setLong(11, p.getIdPess());
            stmt.executeUpdate();
            return "Pessoa atualizada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar pessoa: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Pessoa removida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover pessoa: " + ex.getMessage();
        }
    }

    public List<Pessoa> selecionar() {
        List<Pessoa> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public boolean existePorCpf(String cpf) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_EXISTS_BY_CPF)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Pessoa buscarPorId(long id) {
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

    private Pessoa mapear(ResultSet rs) throws SQLException {
        Pessoa p = new Pessoa();
        p.setIdPess(rs.getLong("id_pess"));
        p.setNmPess(rs.getString("nm_pess"));
        p.setCpfPess(rs.getString("cpf_pess"));
        p.setRgPess(rs.getString("rg_pess"));
        p.setTelPess(rs.getString("tel_pess"));
        p.setEmailPess(rs.getString("email_pess"));
        Date dn = rs.getDate("dt_nasc"); if (dn != null) p.setDtNasc(dn.toLocalDate());
        p.setTgChatId(rs.getString("tg_chat_id"));
        p.setCanalOrig(rs.getString("canal_orig"));
        Date dc = rs.getDate("dt_cad"); if (dc != null) p.setDtCad(dc.toLocalDate());
        p.setSttsPess(rs.getString("stts_pess"));
        long fe = rs.getLong("fk_end_id"); p.setFkEndId(rs.wasNull() ? null : fe);
        return p;
    }
}
