package br.com.fiap.nora.bo;

import br.com.fiap.nora.dao.ColaboradorDao;
import br.com.fiap.nora.dao.ColaboradorSessaoDao;
import br.com.fiap.nora.dto.LoginResponse;
import br.com.fiap.nora.entities.Colaborador;
import br.com.fiap.nora.entities.ColaboradorSessao;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuthBO {

    public LoginResponse login(String email, String senha) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email obrigatorio.");
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("Senha obrigatoria.");

        Colaborador colaborador = new ColaboradorDao().buscarPorEmail(email);
        if (colaborador == null || !senha.equals(colaborador.getSenhaHash())) {
            return null;
        }
        if (!"ativo".equals(colaborador.getStatusColab())) {
            throw new RegraNegocioException("Colaborador inativo.");
        }

        String token = UUID.randomUUID().toString();

        ColaboradorSessao sessao = new ColaboradorSessao();
        sessao.setIdColaborador(colaborador.getIdColaborador());
        sessao.setToken(token);
        sessao.setDataExpiracao(LocalDateTime.now().plusHours(8));
        sessao.setStatusSessao("ativo");

        new ColaboradorSessaoDao().inserir(sessao);

        return new LoginResponse(token, colaborador.getNome(), colaborador.getPerfil());
    }

    public ColaboradorSessao validarToken(String token) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        ColaboradorSessao sessao = new ColaboradorSessaoDao().buscarPorToken(token);
        if (sessao == null) return null;
        if (!"ativo".equals(sessao.getStatusSessao())) return null;
        if (sessao.getDataExpiracao() != null && sessao.getDataExpiracao().isBefore(LocalDateTime.now())) return null;
        return sessao;
    }
}
