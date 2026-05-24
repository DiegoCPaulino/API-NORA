package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.ColaboradorDao;
import br.com.fiap.nora.dao.PerfilDao;
import br.com.fiap.nora.dao.UsuarioDao;
import br.com.fiap.nora.dto.LoginResponse;
import br.com.fiap.nora.entities.Colaborador;
import br.com.fiap.nora.entities.Perfil;
import br.com.fiap.nora.entities.Usuario;
import br.com.fiap.nora.services.SessaoMemoria;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuthBO {

    public LoginResponse login(String identificador, String senha) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        if (identificador == null || identificador.isBlank()) throw new IllegalArgumentException("Login ou email obrigatorio.");
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("Senha obrigatoria.");

        try (Connection conn = new ConexaoFactory().conexao()) {
            UsuarioDao usuarioDao = new UsuarioDao(conn);
            Usuario usuario;
            // Identificador com '@' e tratado como email do colaborador; sem '@' e nm_login
            if (identificador.contains("@")) {
                usuario = usuarioDao.buscarPorEmailColaborador(identificador);
            } else {
                usuario = usuarioDao.buscarPorLogin(identificador);
            }

            // Resposta generica para nao revelar se foi login ou senha
            if (usuario == null) return null;

            // Comparacao em texto puro — limitacao academica consciente (sem BCrypt)
            if (!senha.equals(usuario.getSenhaHash())) return null;

            Colaborador colab = new ColaboradorDao(conn).buscarPorId(usuario.getFkColabId());
            Perfil perf = usuario.getFkPerfId() != null ? new PerfilDao(conn).buscarPorId(usuario.getFkPerfId()) : null;

            String nmColab = colab != null ? colab.getNmColab() : identificador;
            String nmPerfil = perf != null ? perf.getNmPerfil() : "atendente";

            String token = UUID.randomUUID().toString();
            SessaoMemoria.registrar(token, new SessaoMemoria.DadosSessao(
                    usuario.getIdUser(), nmColab, nmPerfil, LocalDateTime.now().plusHours(8)
            ));

            usuarioDao.atualizarUltimoAcesso(usuario.getIdUser());

            return new LoginResponse(token, nmColab, nmPerfil);
        }
    }

    public SessaoMemoria.DadosSessao validarToken(String token) {
        return SessaoMemoria.validar(token);
    }
}
