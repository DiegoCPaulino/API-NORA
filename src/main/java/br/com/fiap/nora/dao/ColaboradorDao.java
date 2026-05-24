package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Colaborador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_colab),0)+1 FROM colaborador";
    private static final String SQL_INSERT =
            "INSERT INTO colaborador (id_colab, nm_colab, cpf_colab, email_colab, cargo_colab, dt_entrada, stts_colab, fk_end_id) VALUES (?,?,?,?,?,SYSDATE,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE colaborador SET nm_colab=?, email_colab=?, cargo_colab=?, stts_colab=? WHERE id_colab=?";
    private static final String SQL_DELETE =
            "DELETE FROM colaborador WHERE id_colab=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM colaborador ORDER BY id_colab";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM colaborador WHERE id_colab=?";
    private static final String SQL_SELECT_BY_EMAIL =
            "SELECT * FROM colaborador WHERE email_colab=?";

    public Connection minhaConexao;

    public ColaboradorDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public ColaboradorDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public long inserirRetornandoId(Colaborador c) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_colab.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, c.getNmColab());
            stmt.setString(3, c.getCpfColab());
            stmt.setString(4, c.getEmailColab());
            stmt.setString(5, c.getCargoColab());
            // dt_entrada = SYSDATE no SQL
            stmt.setString(6, c.getSttsColab());
            if (c.getFkEndId() != null) stmt.setLong(7, c.getFkEndId()); else stmt.setNull(7, Types.NUMERIC);
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Colaborador c) {
        try {
            inserirRetornandoId(c);
            return "Colaborador inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir colaborador: " + ex.getMessage();
        }
    }

    public String atualizar(Colaborador c) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, c.getNmColab());
            stmt.setString(2, c.getEmailColab());
            stmt.setString(3, c.getCargoColab());
            stmt.setString(4, c.getSttsColab());
            stmt.setLong(5, c.getIdColab());
            stmt.executeUpdate();
            return "Colaborador atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar colaborador: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Colaborador removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover colaborador: " + ex.getMessage();
        }
    }

    public List<Colaborador> selecionar() {
        List<Colaborador> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Colaborador buscarPorId(long id) {
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

    public Colaborador buscarPorEmail(String email) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_EMAIL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Colaborador mapear(ResultSet rs) throws SQLException {
        Colaborador c = new Colaborador();
        c.setIdColab(rs.getLong("id_colab"));
        c.setNmColab(rs.getString("nm_colab"));
        c.setCpfColab(rs.getString("cpf_colab"));
        c.setEmailColab(rs.getString("email_colab"));
        c.setCargoColab(rs.getString("cargo_colab"));
        Date de = rs.getDate("dt_entrada"); if (de != null) c.setDtEntrada(de.toLocalDate());
        c.setSttsColab(rs.getString("stts_colab"));
        long fe = rs.getLong("fk_end_id"); c.setFkEndId(rs.wasNull() ? null : fe);
        return c;
    }
}
