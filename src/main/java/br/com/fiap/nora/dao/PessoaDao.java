package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Pessoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDao {

    private static final String SQL_INSERT =
            "INSERT INTO TB_PESSOA (ID_ENDERECO, NOME_COMPLETO, CPF, DATA_NASCIMENTO, IDADE, SEXO, EMAIL, TELEFONE, TG_CHAT_ID, CANAL_ORIG, STTS_PESS) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE TB_PESSOA SET ID_ENDERECO=?, NOME_COMPLETO=?, CPF=?, DATA_NASCIMENTO=?, IDADE=?, SEXO=?, EMAIL=?, TELEFONE=?, TG_CHAT_ID=?, CANAL_ORIG=?, STTS_PESS=?, DATA_ATUALIZACAO=SYSTIMESTAMP WHERE ID_PESSOA=?";
    private static final String SQL_UPDATE_STATUS =
            "UPDATE TB_PESSOA SET STTS_PESS=?, DATA_ATUALIZACAO=SYSTIMESTAMP WHERE ID_PESSOA=?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_PESSOA WHERE ID_PESSOA=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM TB_PESSOA ORDER BY ID_PESSOA";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM TB_PESSOA WHERE ID_PESSOA=?";

    public Connection minhaConexao;

    public PessoaDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public PessoaDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public void atualizarStatus(long id, String sttsPess) throws SQLException {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE_STATUS)) {
            stmt.setString(1, sttsPess);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public String inserir(Pessoa p) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, p.getIdEndereco());
            stmt.setString(2, p.getNomeCompleto());
            stmt.setString(3, p.getCpf());
            stmt.setDate(4, Date.valueOf(p.getDataNascimento()));
            stmt.setInt(5, p.getIdade());
            stmt.setString(6, p.getSexo());
            stmt.setString(7, p.getEmail());
            stmt.setString(8, p.getTelefone());
            stmt.setString(9, p.getTgChatId());
            stmt.setString(10, p.getCanalOrig());
            stmt.setString(11, p.getSttsPess());
            stmt.executeUpdate();
            return "Pessoa inserida com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir pessoa: " + ex.getMessage();
        }
    }

    public String atualizar(Pessoa p) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setLong(1, p.getIdEndereco());
            stmt.setString(2, p.getNomeCompleto());
            stmt.setString(3, p.getCpf());
            stmt.setDate(4, Date.valueOf(p.getDataNascimento()));
            stmt.setInt(5, p.getIdade());
            stmt.setString(6, p.getSexo());
            stmt.setString(7, p.getEmail());
            stmt.setString(8, p.getTelefone());
            stmt.setString(9, p.getTgChatId());
            stmt.setString(10, p.getCanalOrig());
            stmt.setString(11, p.getSttsPess());
            stmt.setLong(12, p.getIdPessoa());
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
        p.setIdPessoa(rs.getLong("ID_PESSOA"));
        p.setIdEndereco(rs.getLong("ID_ENDERECO"));
        p.setNomeCompleto(rs.getString("NOME_COMPLETO"));
        p.setCpf(rs.getString("CPF"));
        Date dn = rs.getDate("DATA_NASCIMENTO"); if (dn != null) p.setDataNascimento(dn.toLocalDate());
        p.setIdade(rs.getInt("IDADE"));
        p.setSexo(rs.getString("SEXO"));
        p.setEmail(rs.getString("EMAIL"));
        p.setTelefone(rs.getString("TELEFONE"));
        p.setTgChatId(rs.getString("TG_CHAT_ID"));
        p.setCanalOrig(rs.getString("CANAL_ORIG"));
        p.setSttsPess(rs.getString("STTS_PESS"));
        Timestamp tc = rs.getTimestamp("DATA_CRIACAO"); if (tc != null) p.setDataCriacao(tc.toLocalDateTime());
        Timestamp ta = rs.getTimestamp("DATA_ATUALIZACAO"); if (ta != null) p.setDataAtualizacao(ta.toLocalDateTime());
        return p;
    }
}
