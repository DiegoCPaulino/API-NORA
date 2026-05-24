package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDao {

    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_end),0)+1 FROM endereco";
    private static final String SQL_INSERT =
            "INSERT INTO endereco (id_end, cep, logradouro, numero, complemento, bairro, cidade, uf) VALUES (?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE endereco SET cep=?, logradouro=?, numero=?, complemento=?, bairro=?, cidade=?, uf=? WHERE id_end=?";
    private static final String SQL_DELETE =
            "DELETE FROM endereco WHERE id_end=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM endereco ORDER BY id_end";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM endereco WHERE id_end=?";

    public Connection minhaConexao;

    public EnderecoDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public EnderecoDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public long inserirRetornandoId(Endereco e) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_end.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, e.getCep());
            stmt.setString(3, e.getLogradouro());
            stmt.setString(4, e.getNumero());
            stmt.setString(5, e.getComplemento());
            stmt.setString(6, e.getBairro());
            stmt.setString(7, e.getCidade());
            stmt.setString(8, e.getUf());
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Endereco e) {
        try {
            inserirRetornandoId(e);
            return "Endereco inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir endereco: " + ex.getMessage();
        }
    }

    public String atualizar(Endereco e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, e.getCep());
            stmt.setString(2, e.getLogradouro());
            stmt.setString(3, e.getNumero());
            stmt.setString(4, e.getComplemento());
            stmt.setString(5, e.getBairro());
            stmt.setString(6, e.getCidade());
            stmt.setString(7, e.getUf());
            stmt.setLong(8, e.getIdEnd());
            stmt.executeUpdate();
            return "Endereco atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar endereco: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Endereco removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover endereco: " + ex.getMessage();
        }
    }

    public List<Endereco> selecionar() {
        List<Endereco> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Endereco buscarPorId(long id) {
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

    private Endereco mapear(ResultSet rs) throws SQLException {
        Endereco e = new Endereco();
        e.setIdEnd(rs.getLong("id_end"));
        e.setCep(rs.getString("cep"));
        e.setLogradouro(rs.getString("logradouro"));
        e.setNumero(rs.getString("numero"));
        e.setComplemento(rs.getString("complemento"));
        e.setBairro(rs.getString("bairro"));
        e.setCidade(rs.getString("cidade"));
        e.setUf(rs.getString("uf"));
        return e;
    }
}
