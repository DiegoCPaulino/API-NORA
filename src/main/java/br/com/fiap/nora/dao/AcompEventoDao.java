package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.AcompEvento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcompEventoDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_acomp),0)+1 FROM acomp_evento";
    private static final String SQL_INSERT =
            "INSERT INTO acomp_evento (id_acomp, fk_encam_id, tp_evento, ds_evento, origem, resumo_ia, tp_mensagem, dt_evento) VALUES (?,?,?,?,?,?,?,SYSDATE)";
    private static final String SQL_UPDATE =
            "UPDATE acomp_evento SET ds_evento=?, resumo_ia=? WHERE id_acomp=?";
    private static final String SQL_DELETE =
            "DELETE FROM acomp_evento WHERE id_acomp=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM acomp_evento ORDER BY id_acomp";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM acomp_evento WHERE id_acomp=?";
    private static final String SQL_SELECT_BY_ENCAM =
            "SELECT * FROM acomp_evento WHERE fk_encam_id=? ORDER BY dt_evento ASC";

    public Connection minhaConexao;

    public AcompEventoDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public AcompEventoDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public long inserirRetornandoId(AcompEvento a) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_acomp.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            if (a.getFkEncamId() != null) stmt.setLong(2, a.getFkEncamId()); else stmt.setNull(2, Types.NUMERIC);
            stmt.setString(3, a.getTpEvento());
            stmt.setString(4, a.getDsEvento());
            stmt.setString(5, a.getOrigem());
            stmt.setString(6, a.getResumoIa());
            stmt.setString(7, a.getTpMensagem());
            stmt.executeUpdate();
        }
        return novoId;
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
        try {
            inserirRetornandoId(a);
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
            stmt.setLong(3, a.getIdAcomp());
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
        a.setIdAcomp(rs.getLong("id_acomp"));
        a.setTpEvento(rs.getString("tp_evento"));
        a.setDsEvento(rs.getString("ds_evento"));
        a.setOrigem(rs.getString("origem"));
        Date de = rs.getDate("dt_evento"); if (de != null) a.setDtEvento(de.toLocalDate());
        a.setResumoIa(rs.getString("resumo_ia"));
        a.setTpMensagem(rs.getString("tp_mensagem"));
        long fe = rs.getLong("fk_encam_id"); a.setFkEncamId(rs.wasNull() ? null : fe);
        long fu = rs.getLong("fk_user_id"); a.setFkUserId(rs.wasNull() ? null : fu);
        return a;
    }
}
