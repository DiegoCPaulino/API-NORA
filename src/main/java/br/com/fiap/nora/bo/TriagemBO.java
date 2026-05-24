package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dao.TriagemDao;
import br.com.fiap.nora.dto.MLRequest;
import br.com.fiap.nora.dto.MLResponse;
import br.com.fiap.nora.dto.response.TriagemResponseDTO;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.entities.Triagem;
import br.com.fiap.nora.exceptions.PythonApiException;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import br.com.fiap.nora.mapper.TriagemMapper;
import br.com.fiap.nora.services.MLService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TriagemBO {

    private static final Set<String> STTS_TRIAG_VALIDOS = new HashSet<>(
            Arrays.asList("em_analise", "aprovada", "encerrada", "inativa"));
    private static final Set<String> DECISAO_VALIDOS = new HashSet<>(
            Arrays.asList("aprovado", "encerrado", "reanalise"));
    private static final Set<String> SEXO_VALIDOS = new HashSet<>(Arrays.asList("M", "F"));
    private static final Set<String> RENDA_VALIDOS = new HashSet<>(
            Arrays.asList("ate_1sm", "1_3sm", "acima_3sm"));

    public List<TriagemResponseDTO> listar() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            List<Triagem> triagens = new TriagemDao(conn).selecionar();
            List<TriagemResponseDTO> resultado = new ArrayList<>();
            for (Triagem t : triagens) {
                resultado.add(TriagemMapper.toResponse(t));
            }
            return resultado;
        }
    }

    public TriagemResponseDTO buscarPorId(long id) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Triagem triagem = new TriagemDao(conn).buscarPorId(id);
            return TriagemMapper.toResponse(triagem);
        }
    }

    public TriagemResponseDTO criar(Triagem t) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        if (t.getFkPessId() == null || t.getFkPessId() <= 0) {
            throw new IllegalArgumentException("ID da pessoa obrigatorio.");
        }
        if (t.getProblemaBucal() == null || t.getProblemaBucal().isBlank()) {
            throw new IllegalArgumentException("Problema bucal obrigatorio.");
        }
        if (t.getSexoPess() != null && !t.getSexoPess().isBlank()
                && !SEXO_VALIDOS.contains(t.getSexoPess())) {
            throw new IllegalArgumentException("sexo invalido. Use: M, F.");
        }
        if (t.getRendaFamiliar() != null && !t.getRendaFamiliar().isBlank()
                && !RENDA_VALIDOS.contains(t.getRendaFamiliar())) {
            throw new IllegalArgumentException("rendaFamiliar invalida. Use: ate_1sm, 1_3sm, acima_3sm.");
        }

        // Busca pessoa para calcular idade — conexao fechada antes da chamada ML
        int idadeCalculada;
        try (Connection conn = new ConexaoFactory().conexao()) {
            Pessoa pessoa = new PessoaDao(conn).buscarPorId(t.getFkPessId());
            if (pessoa == null) {
                throw new RegraNegocioException("Pessoa nao encontrada para a triagem.");
            }
            idadeCalculada = Period.between(pessoa.getDtNasc(), LocalDate.now()).getYears();
        }

        // Elegibilidade calculada pelo backend — payload ignorado (§8.1)
        t.setElegTriag(t.definirElegibilidade(idadeCalculada));

        // Chamada ML sem conexao Oracle aberta
        t.setNivelUrgIa(null);
        t.setConfIa(null);
        try {
            MLRequest mlReq = new MLRequest(t.getSexoPess(), idadeCalculada, t.getProblemaBucal(), t.getRendaFamiliar());
            MLResponse mlResp = new MLService().predizerUrgencia(mlReq);
            if (mlResp != null && mlResp.getNivel_urgencia() != null) {
                t.setNivelUrgIa(mlResp.getNivel_urgencia());
                t.setConfIa(mlResp.getConfianca());
            }
        } catch (PythonApiException e) {
            System.err.println("[TriagemBO] MLService indisponivel: " + e.getMessage());
        }

        // Prioridade via nivel IA; fallback sempre baixa quando ML indisponivel (§8.2)
        if (t.getNivelUrgIa() != null) {
            t.setPriorTriag(t.calcularPrioridade(t.getNivelUrgIa()));
        } else {
            t.setPriorTriag("baixa");
        }

        t.setSttsTriag("em_analise");
        t.setDecisao(null);

        // Persiste e retorna — nova conexao aberta e fechada
        try (Connection conn = new ConexaoFactory().conexao()) {
            long idTriag = new TriagemDao(conn).inserirRetornandoId(t);
            Triagem criada = new TriagemDao(conn).buscarPorId(idTriag);
            return TriagemMapper.toResponse(criada);
        }
    }

    // PUT /triagens/{id} — atualiza somente stts_triag e decisao; nenhum outro campo tocado
    public TriagemResponseDTO atualizarStatus(long id, String sttsTriag, String decisao)
            throws SQLException, ClassNotFoundException {
        if (sttsTriag == null || sttsTriag.isBlank()) {
            throw new IllegalArgumentException("status obrigatorio.");
        }
        if (!STTS_TRIAG_VALIDOS.contains(sttsTriag)) {
            throw new IllegalArgumentException(
                    "status invalido. Use: em_analise, aprovada, encerrada, inativa.");
        }
        if (decisao != null && !decisao.isBlank() && !DECISAO_VALIDOS.contains(decisao)) {
            throw new IllegalArgumentException(
                    "decisao invalida. Use: aprovado, encerrado, reanalise.");
        }

        try (Connection conn = new ConexaoFactory().conexao()) {
            Triagem triagem = new TriagemDao(conn).buscarPorId(id);
            if (triagem == null) return null;

            String decisaoFinal = (decisao != null && !decisao.isBlank()) ? decisao : null;
            new TriagemDao(conn).atualizarStatus(id, sttsTriag, decisaoFinal);

            Triagem atualizada = new TriagemDao(conn).buscarPorId(id);
            return TriagemMapper.toResponse(atualizada);
        }
    }
}
