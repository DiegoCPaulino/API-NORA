package br.com.fiap.nora.bo;

import br.com.fiap.nora.dao.AcompEventoDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AcompEventoBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";
    public static final String PREFIXO_REGRA = "REGRA:";

    // Domínios definidos por DDL CHK_ACOMP_TIPO e CHK_ACOMP_ORIG
    private static final List<String> TIPOS_VALIDOS =
            Arrays.asList("follow_up", "retorno_dentista", "observacao", "mudanca_status", "sistema");
    private static final List<String> ORIGENS_VALIDAS =
            Arrays.asList("n8n", "bot", "atendente", "sistema", "dentista");

    public AcompEvento criar(AcompEvento novo) throws SQLException, ClassNotFoundException {
        // Campos NOT NULL confirmados no DDL
        if (novo.getIdEncaminhamento() <= 0) {
            throw new RegraNegocioException(PREFIXO_REGRA + " ID do encaminhamento obrigatorio.");
        }
        if (novo.getTipoEvento() == null || novo.getTipoEvento().isBlank()) {
            throw new RegraNegocioException(PREFIXO_REGRA + " tipoEvento obrigatorio.");
        }
        if (!TIPOS_VALIDOS.contains(novo.getTipoEvento())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " tipoEvento invalido. Valores aceitos: " + TIPOS_VALIDOS);
        }
        if (novo.getDsEvento() == null || novo.getDsEvento().isBlank()) {
            throw new RegraNegocioException(PREFIXO_REGRA + " dsEvento obrigatorio.");
        }
        if (novo.getOrigem() == null || novo.getOrigem().isBlank()) {
            throw new RegraNegocioException(PREFIXO_REGRA + " origem obrigatoria.");
        }
        if (!ORIGENS_VALIDAS.contains(novo.getOrigem())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " origem invalida. Valores aceitos: " + ORIGENS_VALIDAS);
        }

        // Valida existência do encaminhamento
        if (new EncaminhamentoDao().buscarPorId(novo.getIdEncaminhamento()) == null) {
            throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Encaminhamento nao encontrado.");
        }

        AcompEventoDao dao = new AcompEventoDao();
        long id = dao.inserirRetornandoId(novo);
        novo.setIdAcompEvento(id);
        return novo;
    }

    public List<AcompEvento> listarPorEncaminhamento(long idEncam) throws SQLException, ClassNotFoundException {
        // Valida existência do encaminhamento antes de listar
        if (new EncaminhamentoDao().buscarPorId(idEncam) == null) {
            throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Encaminhamento nao encontrado.");
        }
        return new AcompEventoDao().listarPorEncaminhamento(idEncam);
    }
}
