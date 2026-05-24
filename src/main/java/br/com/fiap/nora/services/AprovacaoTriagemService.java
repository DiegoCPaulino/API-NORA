package br.com.fiap.nora.services;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.AcompEventoDao;
import br.com.fiap.nora.dao.ConversaDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.dao.PacienteDao;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dao.TriagemDao;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.entities.Conversa;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.entities.Paciente;
import br.com.fiap.nora.entities.Triagem;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

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
            // DDL stts_triag: 'em_analise','aprovada','encerrada','inativa'
            if ("aprovada".equals(triagem.getSttsTriag()) || "encerrada".equals(triagem.getSttsTriag())) {
                throw new RegraNegocioException(PREFIXO_JA_PROCESSADA + " Triagem ja foi aprovada ou encerrada.");
            }

            // 3b. Validar duplicidade de paciente (evita ORA-00001 PAC_PESS_UK)
            PacienteDao pacienteDao = new PacienteDao(conn);
            if (pacienteDao.existePorPessoa(triagem.getFkPessId())) {
                throw new RegraNegocioException(PREFIXO_JA_PROCESSADA + " Pessoa ja possui paciente cadastrado.");
            }
            if (pacienteDao.existePorTriagem(triagemId)) {
                throw new RegraNegocioException(PREFIXO_JA_PROCESSADA + " Triagem ja vinculada a paciente.");
            }

            // 4. Buscar dentista disponível internamente no Oracle
            System.out.println("[AprovacaoTriagemService] Buscando dentista disponivel para triagem " + triagemId);
            Dentista dentistaSugerido = new MatchService().sugerirDentistaPorTriagem(triagemId);

            // 5. Validar resposta do match interno
            if (dentistaSugerido == null) {
                throw new RegraNegocioException(PREFIXO_SEM_DENTISTA + " Nenhum dentista disponivel para encaminhamento.");
            }

            // 6. Atualizar triagem: stts_triag = aprovada, decisao = aprovado
            triagemDao.atualizarStatus(triagemId, "aprovada", "aprovado");

            // 7. Atualizar pessoa: stts_pess = aprovada
            PessoaDao pessoaDao = new PessoaDao(conn);
            pessoaDao.atualizarStatus(triagem.getFkPessId(), "aprovada");

            // 8. Inserir paciente e capturar ID gerado
            Paciente paciente = new Paciente();
            paciente.setFkPessId(triagem.getFkPessId());
            paciente.setIdTriagRef(triagemId);
            paciente.setSttsTrat("aguardando");
            long idPaciente = pacienteDao.inserirRetornandoId(paciente);
            paciente.setIdPac(idPaciente);

            // 9. Atualizar conversa: contexto = acomp_paciente, limpar fk_pess_id, preencher fk_pac_id
            ConversaDao conversaDao = new ConversaDao(conn);
            Conversa conversa = conversaDao.buscarPorPessoa(triagem.getFkPessId());
            if (conversa != null) {
                conversaDao.atualizarParaPaciente(conversa.getIdConv(), idPaciente);
            } else {
                System.err.println("[AprovacaoTriagemService] Aviso: nenhuma conversa encontrada para idPessoa=" + triagem.getFkPessId());
            }

            // 10. Inserir encaminhamento com dentista escolhido pelo match interno
            Encaminhamento encam = new Encaminhamento();
            encam.setFkPacId(idPaciente);
            encam.setFkDentId(dentistaSugerido.getIdDent());
            encam.setMatchAuto("S");
            // Sem cálculo geográfico externo: distância fica nula no fluxo automático
            encam.setDistKm(null);
            encam.setPriorEncam(triagem.getPriorTriag());
            encam.setSttsEncam("ativo");
            // prev_follow = 15 dias a partir de hoje
            encam.setPrevFollow(LocalDate.now().plusDays(15));
            encam.setObsEncam("Match interno — dentista selecionado do banco Oracle");

            EncaminhamentoDao encamDao = new EncaminhamentoDao(conn);
            long idEncam = encamDao.inserirRetornandoId(encam);
            encam.setIdEncam(idEncam);

            // 11. Criar evento inicial de acompanhamento
            AcompEvento evento = new AcompEvento();
            evento.setFkEncamId(idEncam);
            evento.setTpEvento("primeira_consulta");
            evento.setDsEvento("Encaminhamento criado automaticamente via match geografico");
            evento.setOrigem("sistema");
            evento.setTpMensagem("texto");
            new AcompEventoDao(conn).inserirRetornandoId(evento);

            // 12. Confirmar transacao
            conn.commit();

            // 12. Retornar encaminhamento criado
            return encam;

        } catch (RegraNegocioException e) {
            // Regras de negocio (404/409/422): rollback e repropagar para o Resource mapear o status HTTP
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("[AprovacaoTriagemService] Erro inesperado: " + e.getMessage());
            throw new RuntimeException("Falha na aprovacao da triagem: " + e.getMessage(), e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
