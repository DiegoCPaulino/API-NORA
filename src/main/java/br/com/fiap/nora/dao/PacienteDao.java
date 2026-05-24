package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_pac),0)+1 FROM paciente";
    private static final String SQL_INSERT =
            "INSERT INTO paciente (id_pac, fk_pess_id, id_triag_ref, dt_aprov, stts_trat) VALUES (?,?,?,SYSDATE,?)";
    private static final String SQL_UPDATE =
            "UPDATE paciente SET stts_trat=? WHERE id_pac=?";
    private static final String SQL_DELETE =
            "DELETE FROM paciente WHERE id_pac=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM paciente ORDER BY id_pac";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM paciente WHERE id_pac=?";
    private static final String SQL_SELECT_BY_PESSOA =
            "SELECT * FROM paciente WHERE fk_pess_id=?";
    private static final String SQL_EXISTS_BY_PESSOA =
            "SELECT COUNT(*) FROM paciente WHERE fk_pess_id=?";
    private static final String SQL_EXISTS_BY_TRIAGEM =
            "SELECT COUNT(*) FROM paciente WHERE id_triag_ref=?";

    public Connection minhaConexao;

    public PacienteDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public PacienteDao(Connection conn) {
        this.minhaConexao = conn;
    }

    // Retorna o ID gerado; usado na transacao de aprovacao onde o ID e necessario imediatamente
    public long inserirRetornandoId(Paciente p) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_pac.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            if (p.getFkPessId() != null) stmt.setLong(2, p.getFkPessId()); else stmt.setNull(2, Types.NUMERIC);
            if (p.getIdTriagRef() != null) stmt.setLong(3, p.getIdTriagRef()); else stmt.setNull(3, Types.NUMERIC);
            // dt_aprov = SYSDATE no SQL
            stmt.setString(4, p.getSttsTrat());
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Paciente p) {
        try {
            inserirRetornandoId(p);
            return "Paciente inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir paciente: " + ex.getMessage();
        }
    }

    public String atualizar(Paciente p) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, p.getSttsTrat());
            stmt.setLong(2, p.getIdPac());
            stmt.executeUpdate();
            return "Paciente atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar paciente: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Paciente removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover paciente: " + ex.getMessage();
        }
    }

    public List<Paciente> selecionar() {
        List<Paciente> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public boolean existePorPessoa(long idPess) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_EXISTS_BY_PESSOA)) {
            stmt.setLong(1, idPess);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean existePorTriagem(long idTriagem) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_EXISTS_BY_TRIAGEM)) {
            stmt.setLong(1, idTriagem);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Paciente buscarPorPessoa(long idPess) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_PESSOA)) {
            stmt.setLong(1, idPess);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Paciente buscarPorId(long id) {
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

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setIdPac(rs.getLong("id_pac"));
        Date da = rs.getDate("dt_aprov"); if (da != null) p.setDtAprov(da.toLocalDate());
        p.setSttsTrat(rs.getString("stts_trat"));
        long itr = rs.getLong("id_triag_ref"); p.setIdTriagRef(rs.wasNull() ? null : itr);
        long fp = rs.getLong("fk_pess_id"); p.setFkPessId(rs.wasNull() ? null : fp);
        return p;
    }
}
