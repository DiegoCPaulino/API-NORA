package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Dentista;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistaDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_DENTISTA (ID_ENDERECO, NOME, CRO, EMAIL, TELEFONE, STTS_DENT, CAP_MENSAL, ATIVOS, OBSERVACOES) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_DENTISTA SET NOME=?, EMAIL=?, TELEFONE=?, STTS_DENT=?, CAP_MENSAL=?, ATIVOS=?, OBSERVACOES=? WHERE ID_DENTISTA=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_DENTISTA WHERE ID_DENTISTA=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_DENTISTA ORDER BY ID_DENTISTA";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_DENTISTA WHERE ID_DENTISTA=?";
    private static final String SQL_SELECT_DISPONIVEIS =
            "SELECT * FROM TB_DENTISTA WHERE STTS_DENT='ativo' AND ATIVOS < CAP_MENSAL ORDER BY ID_DENTISTA";

    public Connection minhaConexao;

    public DentistaDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(Dentista d) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, d.getIdEndereco());
            stmt.setString(2, d.getNome());
            stmt.setString(3, d.getCro());
            stmt.setString(4, d.getEmail());
            stmt.setString(5, d.getTelefone());
            stmt.setString(6, d.getSttsDent());
            stmt.setInt(7, d.getCapMensal());
            stmt.setInt(8, d.getAtivos());
            stmt.setString(9, d.getObservacoes());
            stmt.executeUpdate();
            return "Dentista inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir dentista: " + ex.getMessage();
        }
    }

    public String atualizar(Dentista d) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, d.getNome());
            stmt.setString(2, d.getEmail());
            stmt.setString(3, d.getTelefone());
            stmt.setString(4, d.getSttsDent());
            stmt.setInt(5, d.getCapMensal());
            stmt.setInt(6, d.getAtivos());
            stmt.setString(7, d.getObservacoes());
            stmt.setLong(8, d.getIdDentista());
            stmt.executeUpdate();
            return "Dentista atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar dentista: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Dentista removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover dentista: " + ex.getMessage();
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
        d.setIdDentista(rs.getLong("ID_DENTISTA"));
        d.setIdEndereco(rs.getLong("ID_ENDERECO"));
        d.setNome(rs.getString("NOME"));
        d.setCro(rs.getString("CRO"));
        d.setEmail(rs.getString("EMAIL"));
        d.setTelefone(rs.getString("TELEFONE"));
        d.setSttsDent(rs.getString("STTS_DENT"));
        d.setCapMensal(rs.getInt("CAP_MENSAL"));
        d.setAtivos(rs.getInt("ATIVOS"));
        Timestamp tc = rs.getTimestamp("DATA_CADASTRO"); if (tc != null) d.setDataCadastro(tc.toLocalDateTime());
        d.setObservacoes(rs.getString("OBSERVACOES"));
        return d;
    }
}
