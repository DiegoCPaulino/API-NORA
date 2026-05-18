package br.com.fiap.nora.bo;

import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.dao.TriagemDao;
import br.com.fiap.nora.dto.MLRequest;
import br.com.fiap.nora.dto.MLResponse;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.entities.Triagem;
import br.com.fiap.nora.exceptions.PythonApiException;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import br.com.fiap.nora.services.MLService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class TriagemBO {

    public List<Triagem> listar() throws SQLException, ClassNotFoundException {
        return new TriagemDao().selecionar();
    }

    public Triagem buscarPorId(long id) throws SQLException, ClassNotFoundException {
        return new TriagemDao().buscarPorId(id);
    }

    public String criar(Triagem t) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        if (t.getIdPessoa() <= 0) {
            throw new IllegalArgumentException("ID da pessoa obrigatorio.");
        }
        if (t.getProblemaBucal() == null || t.getProblemaBucal().isBlank()) {
            throw new IllegalArgumentException("Problema bucal obrigatorio.");
        }

        Pessoa pessoa = new PessoaDao().buscarPorId(t.getIdPessoa());
        if (pessoa == null) {
            throw new RegraNegocioException("Pessoa nao encontrada para a triagem.");
        }
        // Idade e calculada do backend; payload e ignorado
        int idadeCalculada = Period.between(pessoa.getDataNascimento(), LocalDate.now()).getYears();
        t.setIdade(idadeCalculada);

        // Regra obrigatoria §9.1 — elegibilidade calculada pelo backend
        t.setElegTriag(t.definirElegibilidade(idadeCalculada));

        // Integracao ML — endpoint nao disponivel na API Python atual (pendencia CLAUDE.md §11.1)
        // Fallback: mantem nivelUrgIa do payload quando MLService indisponivel
        try {
            MLRequest mlReq = new MLRequest(t.getSexoPess(), t.getIdade(), t.getProblemaBucal(), t.getRendaFamiliar());
            MLResponse mlResp = new MLService().predizerUrgencia(mlReq);
            if (mlResp != null && mlResp.getNivel_urgencia() != null) {
                t.setNivelUrgIa(mlResp.getNivel_urgencia());
                t.setConfIa(mlResp.getConfianca());
            }
        } catch (PythonApiException e) {
            System.err.println("[TriagemBO] MLService indisponivel: " + e.getMessage());
        }

        // Regra obrigatoria §9.2 — prioridade via nivel IA quando disponivel; padrao baixa
        if (t.getNivelUrgIa() != null) {
            t.setPriorTriag(t.calcularPrioridade(t.getNivelUrgIa()));
        } else if (t.getPriorTriag() == null || t.getPriorTriag().isBlank()) {
            t.setPriorTriag("baixa");
        }

        if (t.getSttsTriag() == null || t.getSttsTriag().isBlank()) t.setSttsTriag("em_analise");
        if (t.getDecisao() == null || t.getDecisao().isBlank()) t.setDecisao("pendente");

        return new TriagemDao().inserir(t);
    }

    public String atualizar(Triagem t) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        // Recalcula prioridade se nivelUrgIa foi atualizado
        if (t.getNivelUrgIa() != null) {
            t.setPriorTriag(t.calcularPrioridade(t.getNivelUrgIa()));
        }
        return new TriagemDao().atualizar(t);
    }
}
