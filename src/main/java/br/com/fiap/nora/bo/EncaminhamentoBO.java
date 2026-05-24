package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.AcompEventoDao;
import br.com.fiap.nora.dao.DentistaDao;
import br.com.fiap.nora.dao.DentistaEspecialidadeDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.dao.EnderecoDao;
import br.com.fiap.nora.dao.PacienteDao;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dao.TriagemDao;
import br.com.fiap.nora.dto.response.DentistaEmbutidoDTO;
import br.com.fiap.nora.dto.response.EncaminhamentoResponseDTO;
import br.com.fiap.nora.dto.response.PacienteEmbutidoDTO;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Endereco;
import br.com.fiap.nora.entities.Paciente;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.entities.Triagem;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import br.com.fiap.nora.mapper.DentistaMapper;
import br.com.fiap.nora.mapper.EncaminhamentoMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncaminhamentoBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";
    public static final String PREFIXO_REGRA = "REGRA:";

    private static final List<String> STATUS_VALIDOS =
            Arrays.asList("ativo", "concluido", "cancelado", "reencaminhado");
    private static final List<String> PRIORIDADES_VALIDAS =
            Arrays.asList("baixa", "media", "alta", "urgente");
    private static final List<String> MATCH_AUTO_VALIDOS =
            Arrays.asList("S", "N");

    public List<EncaminhamentoResponseDTO> listarTodos() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Encaminhamento> lista = new EncaminhamentoDao(conn).selecionar();
            List<EncaminhamentoResponseDTO> resultado = new ArrayList<>();
            for (Encaminhamento enc : lista) {
                resultado.add(montar(conn, enc));
            }
            return resultado;
        }
    }

    public EncaminhamentoResponseDTO buscarPorId(long id) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Encaminhamento enc = new EncaminhamentoDao(conn).buscarPorId(id);
            if (enc == null) return null;
            return montar(conn, enc);
        }
    }

    public EncaminhamentoResponseDTO criar(Encaminhamento novo) throws SQLException, ClassNotFoundException {
        // Validacoes de campos obrigatorios (sem DB)
        if (novo.getFkPacId() == null || novo.getFkPacId() <= 0) {
            throw new IllegalArgumentException("fkPacId e obrigatorio.");
        }
        if (novo.getFkDentId() == null || novo.getFkDentId() <= 0) {
            throw new IllegalArgumentException("fkDentId e obrigatorio.");
        }
        if (novo.getPriorEncam() == null || novo.getPriorEncam().isBlank()) {
            throw new IllegalArgumentException("priorEncam e obrigatorio.");
        }
        if (!PRIORIDADES_VALIDAS.contains(novo.getPriorEncam())) {
            throw new IllegalArgumentException("priorEncam invalido. Valores aceitos: " + PRIORIDADES_VALIDAS);
        }
        if (novo.getMatchAuto() != null && !MATCH_AUTO_VALIDOS.contains(novo.getMatchAuto())) {
            throw new IllegalArgumentException("matchAuto invalido. Valores aceitos: S ou N.");
        }

        if (novo.getMatchAuto() == null) novo.setMatchAuto("N");
        if (novo.getSttsEncam() == null || novo.getSttsEncam().isBlank()) novo.setSttsEncam("ativo");
        if (novo.getPrevFollow() == null) novo.setPrevFollow(LocalDate.now().plusDays(15));

        try (Connection conn = new ConexaoFactory().conexao()) {
            if (new PacienteDao(conn).buscarPorId(novo.getFkPacId()) == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Paciente nao encontrado.");
            }
            if (new DentistaDao(conn).buscarPorId(novo.getFkDentId()) == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Dentista nao encontrado.");
            }

            long id = new EncaminhamentoDao(conn).inserirRetornandoId(novo);
            novo.setIdEncam(id);
            return montar(conn, novo);
        }
    }

    public EncaminhamentoResponseDTO atualizar(long id, String status, String obs)
            throws SQLException, ClassNotFoundException {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status e obrigatorio.");
        }
        if (!STATUS_VALIDOS.contains(status)) {
            throw new IllegalArgumentException("status invalido. Valores aceitos: " + STATUS_VALIDOS);
        }

        try (Connection conn = new ConexaoFactory().conexao()) {
            Encaminhamento enc = new EncaminhamentoDao(conn).buscarPorId(id);
            if (enc == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Encaminhamento nao encontrado.");
            }

            // Aplica regra de dominio para 'concluido'
            if ("concluido".equals(status)) {
                enc.encerrarEncaminhamento();
            } else {
                enc.setSttsEncam(status);
            }
            enc.setObsEncam(obs);

            new EncaminhamentoDao(conn).atualizarStatusObs(id, enc.getSttsEncam(), obs);
            return montar(conn, enc);
        }
    }

    // Todos os DAOs compartilham a mesma conexao — evita N+1 sessoes Oracle por encaminhamento
    private EncaminhamentoResponseDTO montar(Connection conn, Encaminhamento enc) throws SQLException {
        PacienteEmbutidoDTO pacienteEmbutido = null;
        if (enc.getFkPacId() != null) {
            Paciente pac = new PacienteDao(conn).buscarPorId(enc.getFkPacId());
            if (pac != null && pac.getFkPessId() != null) {
                Pessoa pessoa = new PessoaDao(conn).buscarPorId(pac.getFkPessId());
                Endereco endPessoa = (pessoa != null && pessoa.getFkEndId() != null)
                        ? new EnderecoDao(conn).buscarPorId(pessoa.getFkEndId()) : null;
                List<Triagem> triagens = pessoa != null
                        ? new TriagemDao(conn).listarPorPessoa(pac.getFkPessId()) : new ArrayList<>();
                Triagem ultimaTriagem = triagens.isEmpty() ? null : triagens.get(0);
                pacienteEmbutido = EncaminhamentoMapper.toPacienteEmbutido(
                        enc.getFkPacId(), pessoa, endPessoa, ultimaTriagem);
            }
        }

        DentistaEmbutidoDTO dentistaEmbutido = null;
        if (enc.getFkDentId() != null) {
            Dentista dent = new DentistaDao(conn).buscarPorId(enc.getFkDentId());
            if (dent != null) {
                Endereco endDent = dent.getFkEndId() != null
                        ? new EnderecoDao(conn).buscarPorId(dent.getFkEndId()) : null;
                List<String> especialidades = new DentistaEspecialidadeDao(conn)
                        .listarNomesEspecialidadesPorDentista(enc.getFkDentId());
                dentistaEmbutido = DentistaMapper.toEmbutido(dent, endDent, especialidades, enc.getDistKm());
            }
        }

        List<AcompEvento> eventos = new AcompEventoDao(conn).listarPorEncaminhamento(enc.getIdEncam());
        return EncaminhamentoMapper.toResponse(enc, pacienteEmbutido, dentistaEmbutido, eventos);
    }
}
