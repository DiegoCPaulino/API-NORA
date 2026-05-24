package br.com.fiap.nora.services;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.DentistaDao;
import br.com.fiap.nora.dto.MatchResponse;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.exceptions.PythonApiException;

import java.sql.Connection;
import java.util.List;

public class MatchService {

    // Match interno: seleciona dentista disponivel do banco Oracle.
    // Parametro idTriagem mantido para nao quebrar assinatura usada em AprovacaoTriagemService.
    public MatchResponse sugerirDentistaPorTriagem(long idTriagem) throws PythonApiException {
        System.out.println("[MatchService] Buscando dentista disponivel no banco (match interno) para triagem " + idTriagem);
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Dentista> disponiveis = new DentistaDao(conn).selecionarDisponiveis();
            if (disponiveis.isEmpty()) {
                MatchResponse sem = new MatchResponse();
                sem.setMetodo_calculo("sem_dentista_disponivel");
                return sem;
            }
            Dentista dentista = disponiveis.get(0);
            MatchResponse.DentistaSugerido sugerido = new MatchResponse.DentistaSugerido();
            sugerido.setId_dentista(Long.valueOf(dentista.getIdDent()).intValue());
            sugerido.setNome(dentista.getNmDent());
            MatchResponse resp = new MatchResponse();
            resp.setDentista_sugerido(sugerido);
            resp.setMetodo_calculo("match_interno");
            resp.setDistancia_km(null);
            resp.setObservacao("Match interno — dentista selecionado do banco Oracle");
            return resp;
        } catch (Exception e) {
            throw new PythonApiException("Falha ao buscar dentista disponivel no banco: " + e.getMessage());
        }
    }

    // Nao usado no fluxo principal — mantido por compatibilidade de assinatura
    public MatchResponse sugerirDentista(long idPaciente) throws PythonApiException {
        throw new PythonApiException("Match por paciente nao disponivel — usar sugerirDentistaPorTriagem.");
    }
}
