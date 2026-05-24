package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensagemDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_mens),0)+1 FROM mensagem";
    private static final String SQL_INSERT =
            "INSERT INTO mensagem (id_mens, fk_conv_id, enviado_por, direcao, conteudo, tp_mens, dt_envio) VALUES (?,?,?,?,?,?,SYSDATE)";
    private static final String SQL_UPDATE =
            "UPDATE mensagem SET conteudo=? WHERE id_mens=?";
    private static final String SQL_DELETE =
            "DELETE FROM mensagem WHERE id_mens=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM mensagem ORDER BY id_mens";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM mensagem WHERE id_mens=?";
    private static final String SQL_SELECT_BY_CONVERSA =
            "SELECT * FROM mensagem WHERE fk_conv_id=? ORDER BY dt_envio ASC, id_mens ASC";
    private static final String SQL_ULTIMA_POR_CONVERSA =
            "SELECT * FROM mensagem WHERE fk_conv_id=? ORDER BY dt_envio DESC, id_mens DESC";

    public Connection minhaConexao;

    public MensagemDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public MensagemDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public long inserirRetornandoId(Mensagem m) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_mens.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            if (m.getFkConvId() != null) stmt.setLong(2, m.getFkConvId()); else stmt.setNull(2, Types.NUMERIC);
            stmt.setString(3, m.getEnviadoPor());
            stmt.setString(4, m.getDirecao());
            stmt.setString(5, m.getConteudo());
            stmt.setString(6, m.getTpMens());
            stmt.executeUpdate();
        }
        return novoId;
    }

    public Mensagem buscarUltimaPorConversa(long idConversa) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ULTIMA_POR_CONVERSA)) {
            stmt.setLong(1, idConversa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Mensagem> listarPorConversa(long idConversa) {
        List<Mensagem> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_CONVERSA)) {
            stmt.setLong(1, idConversa);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public String inserir(Mensagem m) {
        try {
            inserirRetornandoId(m);
            return "Mensagem inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir mensagem: " + ex.getMessage();
        }
    }

    public String atualizar(Mensagem m) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, m.getConteudo());
            stmt.setLong(2, m.getIdMens());
            stmt.executeUpdate();
            return "Mensagem atualizada com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar mensagem: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Mensagem removida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover mensagem: " + ex.getMessage();
        }
    }

    public List<Mensagem> selecionar() {
        List<Mensagem> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Mensagem buscarPorId(long id) {
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

    private Mensagem mapear(ResultSet rs) throws SQLException {
        Mensagem m = new Mensagem();
        m.setIdMens(rs.getLong("id_mens"));
        m.setEnviadoPor(rs.getString("enviado_por"));
        m.setDirecao(rs.getString("direcao"));
        m.setConteudo(rs.getString("conteudo"));
        m.setTpMens(rs.getString("tp_mens"));
        Date de = rs.getDate("dt_envio"); if (de != null) m.setDtEnvio(de.toLocalDate());
        long fc = rs.getLong("fk_conv_id"); m.setFkConvId(rs.wasNull() ? null : fc);
        return m;
    }
}
