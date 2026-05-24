package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.entities.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    private static final String SQL_EXISTS_BY_LOGIN =
            "SELECT COUNT(*) FROM usuario WHERE nm_login=?";
    private static final String SQL_NEXT_ID =
            "SELECT NVL(MAX(id_user),0)+1 FROM usuario";
    private static final String SQL_INSERT =
            "INSERT INTO usuario (id_user, nm_login, senha_hash, stts_user, dt_criacao, fk_colab_id, fk_perf_id) VALUES (?,?,?,?,SYSDATE,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE usuario SET senha_hash=?, stts_user=?, fk_perf_id=? WHERE id_user=?";
    private static final String SQL_UPDATE_ACESSO =
            "UPDATE usuario SET dt_acesso=SYSDATE WHERE id_user=?";
    private static final String SQL_DELETE =
            "DELETE FROM usuario WHERE id_user=?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM usuario ORDER BY id_user";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM usuario WHERE id_user=?";
    // Filtra apenas usuarios ativos — inativos nao podem logar
    private static final String SQL_SELECT_BY_LOGIN =
            "SELECT * FROM usuario WHERE nm_login=? AND stts_user='ativo'";
    // Resolve usuario via email do colaborador vinculado — suporte ao campo email do LoginRequest
    private static final String SQL_SELECT_BY_EMAIL_COLAB =
            "SELECT u.* FROM usuario u JOIN colaborador c ON c.id_colab = u.fk_colab_id " +
            "WHERE c.email_colab=? AND u.stts_user='ativo'";

    public Connection minhaConexao;

    public UsuarioDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public UsuarioDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public boolean existePorLogin(String login) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_EXISTS_BY_LOGIN)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public long inserirRetornandoId(Usuario u) throws SQLException {
        long novoId;
        try (PreparedStatement stmtId = minhaConexao.prepareStatement(SQL_NEXT_ID);
             ResultSet rs = stmtId.executeQuery()) {
            if (!rs.next()) throw new SQLException("Falha ao calcular proximo id_user.");
            novoId = rs.getLong(1);
        }
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_INSERT)) {
            stmt.setLong(1, novoId);
            stmt.setString(2, u.getNmLogin());
            stmt.setString(3, u.getSenhaHash());
            stmt.setString(4, u.getSttsUser());
            if (u.getFkColabId() != null) stmt.setLong(5, u.getFkColabId()); else stmt.setNull(5, Types.NUMERIC);
            if (u.getFkPerfId() != null) stmt.setLong(6, u.getFkPerfId()); else stmt.setNull(6, Types.NUMERIC);
            stmt.executeUpdate();
        }
        return novoId;
    }

    public String inserir(Usuario u) {
        try {
            inserirRetornandoId(u);
            return "Usuario inserido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao inserir usuario: " + ex.getMessage();
        }
    }

    public String atualizar(Usuario u) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, u.getSenhaHash());
            stmt.setString(2, u.getSttsUser());
            if (u.getFkPerfId() != null) stmt.setLong(3, u.getFkPerfId()); else stmt.setNull(3, Types.NUMERIC);
            stmt.setLong(4, u.getIdUser());
            stmt.executeUpdate();
            return "Usuario atualizado com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar usuario: " + ex.getMessage();
        }
    }

    public String atualizarUltimoAcesso(long idUser) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_UPDATE_ACESSO)) {
            stmt.setLong(1, idUser);
            stmt.executeUpdate();
            return "Ultimo acesso atualizado.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao atualizar acesso: " + ex.getMessage();
        }
    }

    public String deletar(long id) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return "Usuario removido com sucesso.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Erro ao remover usuario: " + ex.getMessage();
        }
    }

    public List<Usuario> selecionar() {
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Usuario buscarPorId(long id) {
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

    public Usuario buscarPorLogin(String login) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_LOGIN)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Usuario buscarPorEmailColaborador(String email) {
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_SELECT_BY_EMAIL_COLAB)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUser(rs.getLong("id_user"));
        u.setNmLogin(rs.getString("nm_login"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setSttsUser(rs.getString("stts_user"));
        Date dc = rs.getDate("dt_criacao"); if (dc != null) u.setDtCriacao(dc.toLocalDate());
        Date da = rs.getDate("dt_acesso"); if (da != null) u.setDtAcesso(da.toLocalDate());
        long fc = rs.getLong("fk_colab_id"); u.setFkColabId(rs.wasNull() ? null : fc);
        long fp = rs.getLong("fk_perf_id"); u.setFkPerfId(rs.wasNull() ? null : fp);
        return u;
    }
}
