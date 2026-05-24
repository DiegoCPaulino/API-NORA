package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.ConversaDao;
import br.com.fiap.nora.dao.DentistaDao;
import br.com.fiap.nora.dao.MensagemDao;
import br.com.fiap.nora.dao.PacienteDao;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dto.response.ConversaResponseDTO;
import br.com.fiap.nora.dto.response.DadosDentistaDTO;
import br.com.fiap.nora.dto.response.DadosPacienteDTO;
import br.com.fiap.nora.entities.Conversa;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.entities.Mensagem;
import br.com.fiap.nora.entities.Paciente;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import br.com.fiap.nora.mapper.ConversaMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConversaBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";
    public static final String PREFIXO_REGRA = "REGRA:";

    private static final List<String> CANAIS_VALIDOS =
            Arrays.asList("whatsapp", "instagram", "facebook", "telegram");
    private static final List<String> CONTEXTOS_VALIDOS =
            Arrays.asList("acomp_paciente", "acomp_dentista", "cadastro");

    public List<ConversaResponseDTO> listar(String contexto, String stts) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Conversa> todas = new ConversaDao(conn).selecionar();
            List<ConversaResponseDTO> resultado = new ArrayList<>();
            for (Conversa c : todas) {
                if (contexto != null && !contexto.equals(c.getContexto())) continue;
                if (stts != null && !stts.equals(c.getSttsConv())) continue;
                resultado.add(montarDTO(conn, c, false));
            }
            return resultado;
        }
    }

    public ConversaResponseDTO buscarPorId(long id) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Conversa c = new ConversaDao(conn).buscarPorId(id);
            if (c == null) throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Conversa nao encontrada.");
            return montarDTO(conn, c, true);
        }
    }

    public ConversaResponseDTO criar(Conversa nova) throws SQLException, ClassNotFoundException {
        if (nova.getCanalConv() == null || nova.getCanalConv().isBlank()) {
            throw new RegraNegocioException(PREFIXO_REGRA + " canalConv e obrigatorio.");
        }
        if (!CANAIS_VALIDOS.contains(nova.getCanalConv())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " canalConv invalido. Valores aceitos: " + CANAIS_VALIDOS);
        }
        if (nova.getContexto() == null || nova.getContexto().isBlank()) {
            throw new RegraNegocioException(PREFIXO_REGRA + " contexto e obrigatorio.");
        }
        if (!CONTEXTOS_VALIDOS.contains(nova.getContexto())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " contexto invalido. Valores aceitos: " + CONTEXTOS_VALIDOS);
        }

        // Exatamente uma FK contextual deve estar preenchida.
        int fksPreenchidas = 0;
        if (nova.getFkPessId() != null) fksPreenchidas++;
        if (nova.getFkPacId() != null) fksPreenchidas++;
        if (nova.getFkDentId() != null) fksPreenchidas++;
        if (fksPreenchidas != 1) {
            throw new RegraNegocioException(PREFIXO_REGRA
                    + " Conversa deve referenciar exatamente uma entre fkPessId, fkPacId ou fkDentId.");
        }

        // FK deve ser coerente com contexto declarado.
        if (nova.getFkPessId() != null && !"cadastro".equals(nova.getContexto())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " fkPessId requer contexto='cadastro'.");
        }
        if (nova.getFkPacId() != null && !"acomp_paciente".equals(nova.getContexto())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " fkPacId requer contexto='acomp_paciente'.");
        }
        if (nova.getFkDentId() != null && !"acomp_dentista".equals(nova.getContexto())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " fkDentId requer contexto='acomp_dentista'.");
        }

        try (Connection conn = new ConexaoFactory().conexao()) {
            // Upsert: retorna conversa ativa existente para a mesma FK para evitar duplicidade.
            ConversaDao dao = new ConversaDao(conn);
            Conversa existente = buscarAtiva(dao, nova);
            if (existente != null) return montarDTO(conn, existente, false);

            if (nova.getSttsConv() == null || nova.getSttsConv().isBlank()) nova.setSttsConv("aberta");

            long id = dao.inserirRetornandoId(nova);
            nova.setIdConv(id);
            return montarDTO(conn, nova, false);
        }
    }

    private Conversa buscarAtiva(ConversaDao dao, Conversa nova) throws SQLException {
        if (nova.getFkPessId() != null) return dao.buscarAtivaPorPessoa(nova.getFkPessId());
        if (nova.getFkPacId() != null) return dao.buscarAtivaPorPaciente(nova.getFkPacId());
        if (nova.getFkDentId() != null) return dao.buscarAtivaPorDentista(nova.getFkDentId());
        return null;
    }

    // incluirMensagens=true para GET /{id} (historico completo); false para GET lista (sem historico).
    // Todos os DAOs compartilham a mesma conexao — evita N+1 sessoes Oracle por conversa.
    private ConversaResponseDTO montarDTO(Connection conn, Conversa c, boolean incluirMensagens)
            throws SQLException, ClassNotFoundException {

        List<Mensagem> mensagens;
        Mensagem ultima;
        if (incluirMensagens) {
            mensagens = new MensagemDao(conn).listarPorConversa(c.getIdConv());
            ultima = mensagens.isEmpty() ? null : mensagens.get(mensagens.size() - 1);
        } else {
            mensagens = Collections.emptyList();
            ultima = new MensagemDao(conn).buscarUltimaPorConversa(c.getIdConv());
        }

        String conteudoUltima = ultima != null ? ultima.getConteudo() : null;

        DadosPacienteDTO dadosPaciente = null;
        DadosDentistaDTO dadosDentista = null;

        if (c.getFkPacId() != null) {
            Paciente pac = new PacienteDao(conn).buscarPorId(c.getFkPacId());
            if (pac != null && pac.getFkPessId() != null) {
                Pessoa pessoa = new PessoaDao(conn).buscarPorId(pac.getFkPessId());
                if (pessoa != null) {
                    dadosPaciente = new DadosPacienteDTO(pac.getIdPac(), pessoa.getNmPess(), pessoa.getTelPess());
                }
            }
        } else if (c.getFkPessId() != null) {
            Pessoa pessoa = new PessoaDao(conn).buscarPorId(c.getFkPessId());
            if (pessoa != null) {
                dadosPaciente = new DadosPacienteDTO(c.getFkPessId(), pessoa.getNmPess(), pessoa.getTelPess());
            }
        } else if (c.getFkDentId() != null) {
            Dentista dent = new DentistaDao(conn).buscarPorId(c.getFkDentId());
            if (dent != null) {
                dadosDentista = new DadosDentistaDTO(dent.getIdDent(), dent.getNmDent(), dent.getCroDent());
            }
        }

        return ConversaMapper.toResponse(c,
                conteudoUltima,
                ultima != null ? ultima.getDtEnvio() : null,
                mensagens,
                dadosPaciente,
                dadosDentista);
    }
}
