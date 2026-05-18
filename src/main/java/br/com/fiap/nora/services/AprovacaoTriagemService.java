package br.com.fiap.nora.services;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.ConversaDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.dao.PacienteDao;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dao.TriagemDao;
import br.com.fiap.nora.dto.MatchResponse;
import br.com.fiap.nora.entities.Conversa;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Paciente;
import br.com.fiap.nora.entities.Triagem;
import br.com.fiap.nora.exceptions.PythonApiException;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AprovacaoTriagemService {

    // Prefixos usados pelo TriagemResource para mapear status HTTP sem subclasses adicionais
    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";
    public static final String PREFIXO_JA_PROCESSADA  = "JA_PROCESSADA:";
    public static final String PREFIXO_SEM_DENTISTA   = "SEM_DENTISTA:";

    public Encaminhamento aprovar(long triagemId) throws RegraNegocioException {
        // Transação atômica: triagem, pessoa, paciente, conversa (se existir) e encaminhamento processados juntos.
        // Falha em qualquer passo dispara rollback total. A conversa é opcional — cadastros presenciais da ONG podem não ter conversa vinculada.
        Connection conn = null;
        try {
            conn = new ConexaoFactory().conexao();
            conn.setAutoCommit(false);

            // 1. Buscar triagem por ID
            TriagemDao triagemDao = new TriagemDao(conn);
            Triagem triagem = triagemDao.buscarPorId(triagemId);

            // 2. Validar existencia (404)
            if (triagem == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Triagem nao encontrada.");
            }

            // 3. Validar se ainda pode ser aprovada (409)
            if ("aprovada".equals(triagem.getSttsTriag()) || "reprovada".equals(triagem.getSttsTriag())) {
                throw new RegraNegocioException(PREFIXO_JA_PROCESSADA + " Triagem ja foi aprovada ou reprovada.");
            }

            // 4. Chamar MatchService usando ID_TRIAGEM — ANTES de qualquer escrita no banco
            // Motivo: paciente criado durante aprovacao ainda nao recebeu commit; Python nao o enxergaria
            // A triagem ja existe no Oracle antes desta chamada — sem problema de visibilidade
            System.out.println("[AprovacaoTriagemService] Buscando sugestao de dentista para triagem " + triagemId);
            MatchResponse matchResp = new MatchService().sugerirDentistaPorTriagem(triagemId);

            // 5. Validar resposta do match
            if ("sem_dentista_disponivel".equals(matchResp.getMetodo_calculo())) {
                throw new RegraNegocioException(PREFIXO_SEM_DENTISTA + " Nenhum dentista disponivel para encaminhamento.");
            }
            if (matchResp.getDentista_sugerido() == null || matchResp.getDentista_sugerido().getId_dentista() == null) {
                throw new RegraNegocioException(PREFIXO_SEM_DENTISTA + " Resposta de match sem dentista valido.");
            }

            // 6. Atualizar triagem: stts_triag = aprovada, decisao = aprovado
            triagemDao.atualizarStatus(triagemId, "aprovada", "aprovado");

            // 7. Atualizar pessoa: stts_pess = aprovada
            PessoaDao pessoaDao = new PessoaDao(conn);
            pessoaDao.atualizarStatus(triagem.getIdPessoa(), "aprovada");

            // 8. Inserir paciente e capturar ID gerado
            Paciente paciente = new Paciente();
            paciente.setIdPessoa(triagem.getIdPessoa());
            paciente.setIdTriagem(triagemId);
            paciente.setIdade(triagem.getIdade());
            paciente.setSttsTrat("aguardando");
            PacienteDao pacienteDao = new PacienteDao(conn);
            long idPaciente = pacienteDao.inserirRetornandoId(paciente);
            paciente.setIdPaciente(idPaciente);

            // 9. Atualizar conversa: contexto = acomp_paciente, limpar fk_pess_id, preencher fk_pac_id
            ConversaDao conversaDao = new ConversaDao(conn);
            Conversa conversa = conversaDao.buscarPorPessoa(triagem.getIdPessoa());
            if (conversa != null) {
                conversaDao.atualizarParaPaciente(conversa.getIdConversa(), idPaciente);
            } else {
                System.err.println("[AprovacaoTriagemService] Aviso: nenhuma conversa encontrada para idPessoa=" + triagem.getIdPessoa());
            }

            // 10. Inserir encaminhamento com dados retornados pelo match
            Encaminhamento encam = new Encaminhamento();
            encam.setIdPaciente(idPaciente);
            encam.setIdDentista(matchResp.getDentista_sugerido().getId_dentista());
            encam.setIdTriagem(triagemId);
            encam.setMatchAuto("S");
            // cep_fallback: distancia_km = null; nominatim_haversine: valor real — coluna DIST_KM aceita null
            encam.setDistKm(matchResp.getDistancia_km());
            encam.setPrioridade(triagem.getPriorTriag());
            encam.setMetodoCalculo(matchResp.getMetodo_calculo());
            encam.setSttsEncam("ativo");
            // prev_follow = 15 dias a partir de hoje.
            encam.setPrevFollow(LocalDateTime.now().plusDays(15));
            encam.setObservacao(matchResp.getCriterio_fallback() != null ? matchResp.getCriterio_fallback() : matchResp.getObservacao());

            EncaminhamentoDao encamDao = new EncaminhamentoDao(conn);
            long idEncam = encamDao.inserirRetornandoId(encam);
            encam.setIdEncaminhamento(idEncam);

            // 11. Confirmar transacao
            conn.commit();

            // 12. Retornar encaminhamento criado
            return encam;

        } catch (RegraNegocioException e) {
            // Regras de negocio (404/409/422): rollback e repropagar para o Resource mapear o status HTTP
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } catch (PythonApiException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("[AprovacaoTriagemService] Falha no MatchService: " + e.getMessage());
            throw new RuntimeException("Servico de match geografico indisponivel: " + e.getMessage(), e);
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("[AprovacaoTriagemService] Erro inesperado: " + e.getMessage());
            throw new RuntimeException("Falha na aprovacao da triagem: " + e.getMessage(), e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
