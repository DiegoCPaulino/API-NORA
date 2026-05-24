package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Dentista;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistaDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_dent),0)+1 FROM dentista";
    private static final String SQL_INSERT =
            "INSERT INTO dentista (id_dent, nm_dent, cro_dent, tel_dent, email_dent, tg_chat_id, cap_mensal, stts_dent, dt_cred, fk_end_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE dentista SET nm_dent=?, tel_dent=?, email_dent=?, stts_dent=?, cap_mensal=? WHERE id_dent=?";
    private static final String SQL_DELETE =
            "DELETE FROM dentista WHERE id_dent=?";
    private static final String SQL_EXISTS_BY_CRO =
            "SELECT COUNT(*) FROM dentista WHERE cro_dent=?";
    private static final String SQL_EXISTS_BY_EMAIL =
            "SELECT COUNT(*) FROM dentista WHERE email_dent=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM dentista ORDER BY id_dent";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM dentista WHERE id_dent=?";
    // Disponiveis: status ativo + encaminhamentos ativos abaixo da capacidade mensal
    private static final String SQL_SELECT_DISPONIVEIS =
            "SELECT d.* FROM dentista d WHERE d.stts_dent='ativo' " +
            "AND (SELECT COUNT(*) FROM encaminhamento e WHERE e.fk_dent_id=d.id_dent AND e.stts_encam='ativo') < d.cap_mensal " +
            "ORDER BY d.id_dent";

    public Connection minhaConexao;

    public DentistaDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public DentistaDao(Connection conn) {
        this.minhaConexao = conn;
    }

    // Retorna o ID gerado; usado na transacao de criacao de dentista
    public long inserirRetornandoId(Dentista d) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_dent.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, d.getNmDent());
            stmt.setString(3, d.getCroDent());
            stmt.setString(4, d.getTelDent());
            stmt.setString(5, d.getEmailDent());
            stmt.setString(6, d.getTgChatId());
            stmt.setInt(7, d.getCapMensal());
            stmt.setString(8, d.getSttsDent());
            if (d.getDtCred() != null) stmt.setDate(9, Date.valueOf(d.getDtCred())); else stmt.setNull(9, Types.DATE);
            if (d.getFkEndId() != null) stmt.setLong(10, d.getFkEndId()); else stmt.setNull(10, Types.NUMERIC);
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Dentista d) {
        try {
            inserirRetornandoId(d);
            return "Dentista inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir dentista: " + ex.getMessage();
        }
    }

    public String atualizar(Dentista d) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, d.getNmDent());
            stmt.setString(2, d.getTelDent());
            stmt.setString(3, d.getEmailDent());
            stmt.setString(4, d.getSttsDent());
            stmt.setInt(5, d.getCapMensal());
            stmt.setLong(6, d.getIdDent());
            stmt.executeUpdate();
            return "Dentista atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar dentista: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try {
            deletarTransacional(id);
            return "Dentista removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover dentista: " + ex.getMessage();
        }
    }

    // Usado pela BO para permitir tratamento correto de 404/409 no endpoint DELETE
    public int deletarTransacional(long id) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate();
        }
    }

    public List<Dentista> selecionar() {
        List<Dentista> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public boolean existePorCro(String cro) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_EXISTS_BY_CRO)) {
            stmt.setString(1, cro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean existePorEmail(String email) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_EXISTS_BY_EMAIL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Dentista buscarPorId(long id) {
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

    public List<Dentista> selecionarDisponiveis() {
        List<Dentista> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_DISPONIVEIS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    private Dentista mapear(ResultSet rs) throws SQLException {
        Dentista d = new Dentista();
        d.setIdDent(rs.getLong("id_dent"));
        d.setNmDent(rs.getString("nm_dent"));
        d.setCroDent(rs.getString("cro_dent"));
        d.setTelDent(rs.getString("tel_dent"));
        d.setEmailDent(rs.getString("email_dent"));
        d.setTgChatId(rs.getString("tg_chat_id"));
        d.setCapMensal(rs.getInt("cap_mensal"));
        d.setSttsDent(rs.getString("stts_dent"));
        Date dc = rs.getDate("dt_cred"); if (dc != null) d.setDtCred(dc.toLocalDate());
        long fe = rs.getLong("fk_end_id"); d.setFkEndId(rs.wasNull() ? null : fe);
        return d;
    }
}
