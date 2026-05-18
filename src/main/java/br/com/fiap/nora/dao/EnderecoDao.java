package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_ENDERECO (CEP, LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, CIDADE, UF, LATITUDE, LONGITUDE) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_ENDERECO SET CEP=?, LOGRADOURO=?, NUMERO=?, COMPLEMENTO=?, BAIRRO=?, CIDADE=?, UF=?, LATITUDE=?, LONGITUDE=? WHERE ID_ENDERECO=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_ENDERECO WHERE ID_ENDERECO=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_ENDERECO ORDER BY ID_ENDERECO";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_ENDERECO WHERE ID_ENDERECO=?";

    public Connection minhaConexao;

    public EnderecoDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(Endereco e) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, e.getCep());
            stmt.setString(2, e.getLogradouro());
            stmt.setString(3, e.getNumero());
            stmt.setString(4, e.getComplemento());
            stmt.setString(5, e.getBairro());
            stmt.setString(6, e.getCidade());
            stmt.setString(7, e.getUf());
            if (e.getLatitude() != null) stmt.setDouble(8, e.getLatitude()); else stmt.setNull(8, Types.NUMERIC);
            if (e.getLongitude() != null) stmt.setDouble(9, e.getLongitude()); else stmt.setNull(9, Types.NUMERIC);
            stmt.executeUpdate();
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
            if (e.getLatitude() != null) stmt.setDouble(8, e.getLatitude()); else stmt.setNull(8, Types.NUMERIC);
            if (e.getLongitude() != null) stmt.setDouble(9, e.getLongitude()); else stmt.setNull(9, Types.NUMERIC);
            stmt.setLong(10, e.getIdEndereco());
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
        e.setIdEndereco(rs.getLong("ID_ENDERECO"));
        e.setCep(rs.getString("CEP"));
        e.setLogradouro(rs.getString("LOGRADOURO"));
        e.setNumero(rs.getString("NUMERO"));
        e.setComplemento(rs.getString("COMPLEMENTO"));
        e.setBairro(rs.getString("BAIRRO"));
        e.setCidade(rs.getString("CIDADE"));
        e.setUf(rs.getString("UF"));
        double lat = rs.getDouble("LATITUDE"); e.setLatitude(rs.wasNull() ? null : lat);
        double lon = rs.getDouble("LONGITUDE"); e.setLongitude(rs.wasNull() ? null : lon);
        Timestamp ts = rs.getTimestamp("DATA_CRIACAO"); if (ts != null) e.setDataCriacao(ts.toLocalDateTime());
        return e;
    }
}
