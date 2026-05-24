package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.DentistaDao;
import br.com.fiap.nora.dao.DentistaEspecialidadeDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.dao.EnderecoDao;
import br.com.fiap.nora.dao.EspecialidadeDao;
import br.com.fiap.nora.dao.PacienteDao;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dto.request.DentistaRequest;
import br.com.fiap.nora.dto.response.DentistaResponseDTO;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.entities.DentistaEspecialidade;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Endereco;
import br.com.fiap.nora.entities.Especialidade;
import br.com.fiap.nora.entities.Paciente;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.mapper.DentistaMapper;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DentistaBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";
    public static final String PREFIXO_REGRA = "REGRA:";

    private static final Set<String> STATUS_VALIDOS = new HashSet<>(
            Arrays.asList("ativo", "inativo", "suspenso"));

    private static final Set<String> UFS_VALIDAS = new HashSet<>(Arrays.asList(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
            "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
            "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"));

    // Todos os DAOs compartilham a mesma conexao — evita N+1 sessoes Oracle por dentista
    private DentistaResponseDTO compor(Connection conn, Dentista dentista) throws SQLException {
        Endereco endereco = null;
        if (dentista.getFkEndId() != null) {
            endereco = new EnderecoDao(conn).buscarPorId(dentista.getFkEndId());
        }

        List<String> especialidades = new DentistaEspecialidadeDao(conn)
                .listarNomesEspecialidadesPorDentista(dentista.getIdDent());

        List<Encaminhamento> encaminhamentos = new EncaminhamentoDao(conn)
                .listarPorDentista(dentista.getIdDent());

        int ativos = 0;
        Map<Long, String> nomesPacientes = new HashMap<>();
        for (Encaminhamento e : encaminhamentos) {
            if ("ativo".equals(e.getSttsEncam())) ativos++;
            if (e.getFkPacId() != null && !nomesPacientes.containsKey(e.getFkPacId())) {
                Paciente pac = new PacienteDao(conn).buscarPorId(e.getFkPacId());
                if (pac != null && pac.getFkPessId() != null) {
                    Pessoa pessoa = new PessoaDao(conn).buscarPorId(pac.getFkPessId());
                    nomesPacientes.put(e.getFkPacId(), pessoa != null ? pessoa.getNmPess() : null);
                }
            }
        }

        return DentistaMapper.toResponse(dentista, endereco, especialidades, ativos, encaminhamentos, nomesPacientes);
    }

    public List<DentistaResponseDTO> listarDentistas() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Dentista> dentistas = new DentistaDao(conn).selecionar();
            List<DentistaResponseDTO> resultado = new ArrayList<>();
            for (Dentista d : dentistas) {
                resultado.add(compor(conn, d));
            }
            return resultado;
        }
    }

    public DentistaResponseDTO buscarDentista(long idDent) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Dentista dentista = new DentistaDao(conn).buscarPorId(idDent);
            if (dentista == null) return null;
            return compor(conn, dentista);
        }
    }

    // Usa verificarDisponibilidade da entity conforme regra de negocio da Etapa 2
    public List<DentistaResponseDTO> listarDisponiveis() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Dentista> todos = new DentistaDao(conn).selecionar();
            List<DentistaResponseDTO> resultado = new ArrayList<>();
            for (Dentista d : todos) {
                int ativos = new EncaminhamentoDao(conn).contarAtivosPorDentista(d.getIdDent());
                if (d.verificarDisponibilidade(ativos)) {
                    resultado.add(compor(conn, d));
                }
            }
            return resultado;
        }
    }

    public DentistaResponseDTO criarDentista(DentistaRequest req) throws SQLException, ClassNotFoundException {
        validarObrigatorios(req);

        Endereco endereco = new Endereco();
        endereco.setCep(req.getCep());
        endereco.setLogradouro(req.getLogradouro());
        endereco.setNumero(req.getNumero());
        endereco.setComplemento(req.getComplemento());
        endereco.setBairro(req.getBairro());
        endereco.setCidade(req.getCidade());
        endereco.setUf(req.getUf());

        Dentista dentista = new Dentista();
        dentista.setNmDent(req.getNome());
        dentista.setCroDent(req.getCro());
        dentista.setTelDent(req.getTelefone());
        dentista.setEmailDent(req.getEmail());
        dentista.setTgChatId(req.getTgChatId());
        dentista.setCapMensal(req.getCapMensal());
        dentista.setSttsDent(req.getStatus() != null && !req.getStatus().isBlank() ? req.getStatus() : "ativo");
        dentista.setDtCred(LocalDate.now());

        // Transacao: endereco + dentista + especialidades atomicos
        long idDent;
        try (Connection conn = new ConexaoFactory().conexao()) {
            validarEnums(req, conn);
            DentistaDao dentistaCheck = new DentistaDao(conn);
            if (req.getCro() != null && !req.getCro().isBlank() && dentistaCheck.existePorCro(req.getCro())) {
                throw new IllegalArgumentException("CRO ja cadastrado.");
            }
            if (req.getEmail() != null && !req.getEmail().isBlank() && dentistaCheck.existePorEmail(req.getEmail())) {
                throw new IllegalArgumentException("E-mail ja cadastrado para outro dentista.");
            }
            conn.setAutoCommit(false);
            try {
                long idEnd = new EnderecoDao(conn).inserirRetornandoId(endereco);
                dentista.setFkEndId(idEnd);
                idDent = new DentistaDao(conn).inserirRetornandoId(dentista);
                if (req.getEspecialidadeIds() != null && !req.getEspecialidadeIds().isEmpty()) {
                    DentistaEspecialidadeDao deDao = new DentistaEspecialidadeDao(conn);
                    for (Long idEspec : req.getEspecialidadeIds()) {
                        DentistaEspecialidade de = new DentistaEspecialidade();
                        de.setFkDentId(idDent);
                        de.setFkEspecId(idEspec);
                        deDao.inserirTransacional(de);
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException("Erro ao criar dentista: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        }

        return buscarDentista(idDent);
    }

    public DentistaResponseDTO atualizarDentista(long idDent, DentistaRequest req)
            throws SQLException, ClassNotFoundException {
        if (req.getNome() == null || req.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatorio.");
        }
        if (req.getCro() == null || req.getCro().isBlank()) {
            throw new IllegalArgumentException("CRO obrigatorio.");
        }

        try (Connection conn = new ConexaoFactory().conexao()) {
            Dentista dentista = new DentistaDao(conn).buscarPorId(idDent);
            if (dentista == null) return null;

            validarEnums(req, conn);

            dentista.setNmDent(req.getNome());
            dentista.setCroDent(req.getCro());
            dentista.setTelDent(req.getTelefone());
            dentista.setEmailDent(req.getEmail());
            if (req.getCapMensal() > 0) dentista.setCapMensal(req.getCapMensal());
            if (req.getStatus() != null && !req.getStatus().isBlank()) {
                dentista.setSttsDent(req.getStatus());
            }

            // Transacao: dentista + endereco + especialidades atomicos
            conn.setAutoCommit(false);
            try {
                new DentistaDao(conn).atualizar(dentista);

                if (dentista.getFkEndId() != null && req.getCep() != null && !req.getCep().isBlank()) {
                    Endereco endereco = new EnderecoDao(conn).buscarPorId(dentista.getFkEndId());
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

                // Especialidades: delete-all + insert se vier no body
                if (req.getEspecialidadeIds() != null) {
                    DentistaEspecialidadeDao deDao = new DentistaEspecialidadeDao(conn);
                    deDao.removerPorDentista(idDent);
                    for (Long idEspec : req.getEspecialidadeIds()) {
                        DentistaEspecialidade de = new DentistaEspecialidade();
                        de.setFkDentId(idDent);
                        de.setFkEspecId(idEspec);
                        deDao.inserirTransacional(de);
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException("Erro ao atualizar dentista: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }

            return compor(conn, dentista);
        }
    }

    public void deletarDentista(long idDent) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            DentistaDao dentistaDao = new DentistaDao(conn);
            Dentista dentista = dentistaDao.buscarPorId(idDent);

            if (dentista == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Dentista nao encontrado.");
            }

            try {
                int linhasAfetadas = dentistaDao.deletarTransacional(idDent);

                if (linhasAfetadas == 0) {
                    throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Dentista nao encontrado.");
                }
            } catch (SQLException ex) {
                if (isViolacaoIntegridadeReferencial(ex)) {
                    throw new RegraNegocioException(PREFIXO_REGRA
                            + " Nao e possivel excluir dentista com vinculos cadastrados.");
                }

                throw ex;
            }
        }
    }

    private boolean isViolacaoIntegridadeReferencial(SQLException ex) {
        SQLException atual = ex;

        while (atual != null) {
            if (atual.getErrorCode() == 2292) {
                return true;
            }

            String msg = atual.getMessage();
            if (msg != null && msg.contains("ORA-02292")) {
                return true;
            }

            atual = atual.getNextException();
        }

        return false;
    }

    private void validarObrigatorios(DentistaRequest req) {
        if (req.getNome() == null || req.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatorio.");
        }
        if (req.getCro() == null || req.getCro().isBlank()) {
            throw new IllegalArgumentException("CRO obrigatorio.");
        }
        if (req.getCapMensal() <= 0) {
            throw new IllegalArgumentException("capMensal deve ser maior que zero.");
        }
    }

    private void validarEnums(DentistaRequest req, Connection conn) throws SQLException {
        if (req.getStatus() != null && !req.getStatus().isBlank()
                && !STATUS_VALIDOS.contains(req.getStatus())) {
            throw new IllegalArgumentException(
                    "Status invalido. Use: ativo, inativo, suspenso.");
        }
        if (req.getUf() != null && !req.getUf().isBlank()
                && !UFS_VALIDAS.contains(req.getUf().toUpperCase())) {
            throw new IllegalArgumentException(
                    "UF invalida: " + req.getUf());
        }
        if (req.getEspecialidadeIds() != null && !req.getEspecialidadeIds().isEmpty()) {
            EspecialidadeDao especDao = new EspecialidadeDao(conn);
            for (Long idEspec : req.getEspecialidadeIds()) {
                Especialidade espec = especDao.buscarPorId(idEspec);
                if (espec == null) {
                    throw new IllegalArgumentException(
                            "Especialidade nao encontrada: id=" + idEspec);
                }
            }
        }
    }
}
