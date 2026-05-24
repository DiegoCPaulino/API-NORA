package br.com.fiap.nora.services;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.DentistaDao;
import br.com.fiap.nora.entities.Dentista;

import java.sql.Connection;
import java.util.List;

public class MatchService {

    // Match interno: seleciona o primeiro dentista ativo com vagas disponíveis no Oracle.
    // O parâmetro idTriagem é mantido porque o fluxo de aprovação já trabalha a partir da triagem.
    public Dentista sugerirDentistaPorTriagem(long idTriagem) {
        System.out.println("[MatchService] Buscando dentista disponivel no banco para triagem " + idTriagem);

        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Dentista> disponiveis = new DentistaDao(conn).selecionarDisponiveis();

            if (disponiveis.isEmpty()) {
                return null;
            }

            return disponiveis.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar dentista disponivel no banco: " + e.getMessage(), e);
        }
    }
}
