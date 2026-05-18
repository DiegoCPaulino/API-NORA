package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_PACIENTE (ID_PESSOA, ID_TRIAGEM, IDADE, STTS_TRAT, OBSERVACOES) VALUES (?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_PACIENTE SET STTS_TRAT=?, OBSERVACOES=? WHERE ID_PACIENTE=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_PACIENTE WHERE ID_PACIENTE=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_PACIENTE ORDER BY ID_PACIENTE";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_PACIENTE WHERE ID_PACIENTE=?";

    public Connection minhaConexao;

    public PacienteDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public PacienteDao(Connection conn) {
        this.minhaConexao = conn;
    }

    // Retorna o ID gerado; usado na transacao de aprovacao onde o ID e necessario imediatamente
    public long inserirRetornandoId(Paciente p) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT, new String[]{"ID_PACIENTE"})) {
            stmt.setLong(1, p.getIdPessoa());
            stmt.setLong(2, p.getIdTriagem());
            stmt.setInt(3, p.getIdade());
            stmt.setString(4, p.getSttsTrat());
            stmt.setString(5, p.getObservacoes());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Nenhum ID gerado ao inserir paciente.");
    }

    public String inserir(Paciente p) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, p.getIdPessoa());
            stmt.setLong(2, p.getIdTriagem());
            stmt.setInt(3, p.getIdade());
            stmt.setString(4, p.getSttsTrat());
            stmt.setString(5, p.getObservacoes());
            stmt.executeUpdate();
            return "Paciente inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir paciente: " + ex.getMessage();
        }
    }

    public String atualizar(Paciente p) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, p.getSttsTrat());
            stmt.setString(2, p.getObservacoes());
            stmt.setLong(3, p.getIdPaciente());
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
        p.setIdPaciente(rs.getLong("ID_PACIENTE"));
        p.setIdPessoa(rs.getLong("ID_PESSOA"));
        p.setIdTriagem(rs.getLong("ID_TRIAGEM"));
        p.setIdade(rs.getInt("IDADE"));
        Timestamp ta = rs.getTimestamp("DT_APROV"); if (ta != null) p.setDtAprov(ta.toLocalDateTime());
        p.setSttsTrat(rs.getString("STTS_TRAT"));
        p.setObservacoes(rs.getString("OBSERVACOES"));
        return p;
    }
}
