package br.com.fiap.nora.services;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

// Sessão em memória — substitui TB_COLABORADOR_SESSAO que não existe no DDL Sprint 4.
// Limitação acadêmica consciente: sessões não persistem ao reiniciar a aplicação.
public class SessaoMemoria {

    public static class DadosSessao {
        public final Long idUser;
        public final String nome;
        public final String perfil;
        public final LocalDateTime expiraEm;

        public DadosSessao(Long idUser, String nome, String perfil, LocalDateTime expiraEm) {
            this.idUser = idUser;
            this.nome = nome;
            this.perfil = perfil;
            this.expiraEm = expiraEm;
        }
    }

    private static final ConcurrentHashMap<String, DadosSessao> sessoes = new ConcurrentHashMap<>();

    public static void registrar(String token, DadosSessao dados) {
        sessoes.put(token, dados);
    }

    public static DadosSessao validar(String token) {
        DadosSessao dados = sessoes.get(token);
        if (dados == null) return null;
        if (dados.expiraEm.isBefore(LocalDateTime.now())) {
            sessoes.remove(token);
            return null;
        }
        return dados;
    }

    public static void remover(String token) {
        sessoes.remove(token);
    }
}
