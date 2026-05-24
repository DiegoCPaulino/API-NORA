package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.DentistaDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.dao.EnderecoDao;
import br.com.fiap.nora.dao.PacienteDao;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dao.TriagemDao;
import br.com.fiap.nora.dto.request.PessoaRequest;
import br.com.fiap.nora.dto.response.PacienteResponseDTO;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Endereco;
import br.com.fiap.nora.entities.Paciente;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.entities.Triagem;
import br.com.fiap.nora.mapper.PacienteMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PessoaBO {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Todos os DAOs compartilham a mesma conexao — evita N+1 sessoes Oracle por pessoa
    private PacienteResponseDTO compor(Connection conn, Pessoa pessoa) throws SQLException {
        Endereco endereco = null;
        if (pessoa.getFkEndId() != null) {
            endereco = new EnderecoDao(conn).buscarPorId(pessoa.getFkEndId());
        }

        List<Triagem> triagens = new TriagemDao(conn).listarPorPessoa(pessoa.getIdPess());

        List<Encaminhamento> encaminhamentos = new ArrayList<>();
        Map<Long, String> nomesDentistas = new HashMap<>();

        Paciente paciente = new PacienteDao(conn).buscarPorPessoa(pessoa.getIdPess());
        if (paciente != null) {
            encaminhamentos = new EncaminhamentoDao(conn).listarPorPaciente(paciente.getIdPac());
            for (Encaminhamento e : encaminhamentos) {
                if (e.getFkDentId() != null && !nomesDentistas.containsKey(e.getFkDentId())) {
                    Dentista d = new DentistaDao(conn).buscarPorId(e.getFkDentId());
                    nomesDentistas.put(e.getFkDentId(), d != null ? d.getNmDent() : null);
                }
            }
        }

        return PacienteMapper.toResponse(pessoa, endereco, triagens, encaminhamentos, nomesDentistas);
    }

    public List<PacienteResponseDTO> listarPessoas() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Pessoa> pessoas = new PessoaDao(conn).selecionar();
            List<PacienteResponseDTO> resultado = new ArrayList<>();
            for (Pessoa p : pessoas) {
                resultado.add(compor(conn, p));
            }
            return resultado;
        }
    }

    public PacienteResponseDTO buscarPessoa(long idPess) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Pessoa pessoa = new PessoaDao(conn).buscarPorId(idPess);
            if (pessoa == null) return null;
            return compor(conn, pessoa);
        }
    }

    public PacienteResponseDTO criarPessoa(PessoaRequest req) throws SQLException, ClassNotFoundException {
        validarObrigatorios(req);
        LocalDate dtNasc = parseDateOrThrow(req.getDataNascimento());

        Pessoa pessoa = new Pessoa();
        pessoa.setNmPess(req.getNome());
        pessoa.setCpfPess(req.getCpf());
        pessoa.setRgPess(req.getRg());
        pessoa.setTelPess(req.getTelefone());
        pessoa.setEmailPess(req.getEmail());
        pessoa.setDtNasc(dtNasc);
        pessoa.setTgChatId(req.getTgChatId());
        pessoa.setCanalOrig(req.getCanalOrigem());
        pessoa.setSttsPess("em_triagem");

        long idPess;
        try (Connection conn = new ConexaoFactory().conexao()) {
            conn.setAutoCommit(false);
            try {
                if (req.getCpf() != null && !req.getCpf().isBlank()
                        && new PessoaDao(conn).existePorCpf(req.getCpf())) {
                    throw new IllegalArgumentException("CPF ja cadastrado.");
                }
                if (req.getIdEndereco() != null) {
                    // Usa endereco ja existente — valida existencia antes de criar a pessoa
                    Endereco existente = new EnderecoDao(conn).buscarPorId(req.getIdEndereco());
                    if (existente == null) {
                        throw new IllegalArgumentException("Endereco id=" + req.getIdEndereco() + " nao encontrado.");
                    }
                    pessoa.setFkEndId(req.getIdEndereco());
                } else {
                    // Cria novo endereco com os campos enviados
                    Endereco endereco = new Endereco();
                    endereco.setCep(req.getCep());
                    endereco.setLogradouro(req.getLogradouro());
                    endereco.setNumero(req.getNumero());
                    endereco.setComplemento(req.getComplemento());
                    endereco.setBairro(req.getBairro());
                    endereco.setCidade(req.getCidade());
                    endereco.setUf(req.getUf());
                    long idEnd = new EnderecoDao(conn).inserirRetornandoId(endereco);
                    pessoa.setFkEndId(idEnd);
                }
                idPess = new PessoaDao(conn).inserirRetornandoId(pessoa);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException("Erro ao criar pessoa: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        }

        return buscarPessoa(idPess);
    }

    public PacienteResponseDTO atualizarPessoa(long idPess, PessoaRequest req) throws SQLException, ClassNotFoundException {
        if (req.getNome() == null || req.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatorio.");
        }
        if (req.getDataNascimento() == null || req.getDataNascimento().isBlank()) {
            throw new IllegalArgumentException("Data de nascimento obrigatoria.");
        }
        LocalDate dtNasc = parseDateOrThrow(req.getDataNascimento());

        try (Connection conn = new ConexaoFactory().conexao()) {
            Pessoa pessoa = new PessoaDao(conn).buscarPorId(idPess);
            if (pessoa == null) return null;

            pessoa.setNmPess(req.getNome());
            pessoa.setCpfPess(req.getCpf());
            pessoa.setRgPess(req.getRg());
            pessoa.setTelPess(req.getTelefone());
            pessoa.setEmailPess(req.getEmail());
            pessoa.setDtNasc(dtNasc);
            pessoa.setTgChatId(req.getTgChatId());
            if (req.getCanalOrigem() != null && !req.getCanalOrigem().isBlank()) {
                pessoa.setCanalOrig(req.getCanalOrigem());
            }

            new PessoaDao(conn).atualizar(pessoa);

            if (pessoa.getFkEndId() != null && req.getCep() != null && !req.getCep().isBlank()) {
                Endereco endereco = new EnderecoDao(conn).buscarPorId(pessoa.getFkEndId());
                if (endereco != null) {
                    endereco.setCep(req.getCep());
                    endereco.setLogradouro(req.getLogradouro());
                    endereco.setNumero(req.getNumero());
                    endereco.setComplemento(req.getComplemento());
                    endereco.setBairro(req.getBairro());
                    endereco.setCidade(req.getCidade());
                    endereco.setUf(req.getUf());
                    new EnderecoDao(conn).atualizar(endereco);
                }
            }

            return compor(conn, pessoa);
        }
    }

    public List<PacienteResponseDTO> listarPacientes() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Paciente> pacientes = new PacienteDao(conn).selecionar();
            List<PacienteResponseDTO> resultado = new ArrayList<>();
            for (Paciente pac : pacientes) {
                if (pac.getFkPessId() != null) {
                    Pessoa pessoa = new PessoaDao(conn).buscarPorId(pac.getFkPessId());
                    if (pessoa != null) {
                        resultado.add(compor(conn, pessoa));
                    }
                }
            }
            return resultado;
        }
    }

    // id aqui e id_pess; retorna null se a pessoa nao for paciente
    public PacienteResponseDTO buscarPaciente(long idPess) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Paciente pac = new PacienteDao(conn).buscarPorPessoa(idPess);
            if (pac == null) return null;
            Pessoa pessoa = new PessoaDao(conn).buscarPorId(idPess);
            if (pessoa == null) return null;
            return compor(conn, pessoa);
        }
    }

    private void validarObrigatorios(PessoaRequest req) {
        if (req.getNome() == null || req.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatorio.");
        }
        if (req.getDataNascimento() == null || req.getDataNascimento().isBlank()) {
            throw new IllegalArgumentException("Data de nascimento obrigatoria.");
        }
        if (req.getCanalOrigem() == null || req.getCanalOrigem().isBlank()) {
            throw new IllegalArgumentException("Canal de origem obrigatorio.");
        }
    }

    private LocalDate parseDateOrThrow(String dataNascimento) {
        try {
            return LocalDate.parse(dataNascimento, FMT_DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data de nascimento invalida. Use DD/MM/AAAA.");
        }
    }
}
