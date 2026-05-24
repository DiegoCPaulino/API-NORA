package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.AcompEventoDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AcompEventoBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";
    public static final String PREFIXO_REGRA = "REGRA:";

    // Dominios definidos por CHK_ACOMP_TIPO e CHK_ACOMP_ORIG no DDL
    private static final List<String> TIPOS_VALIDOS =
            Arrays.asList("primeira_consulta", "atualizacao", "abandono", "conclusao", "reencaminhamento", "followup", "outro");
    private static final List<String> ORIGENS_VALIDAS =
            Arrays.asList("paciente", "dentista", "ia", "atendente", "sistema");

    public AcompEvento criar(AcompEvento novo) throws SQLException, ClassNotFoundException {
        // Campos NOT NULL confirmados no DDL
        if (novo.getFkEncamId() == null || novo.getFkEncamId() <= 0) {
            throw new RegraNegocioException(PREFIXO_REGRA + " ID do encaminhamento obrigatorio.");
        }
        if (novo.getTpEvento() == null || novo.getTpEvento().isBlank()) {
            throw new RegraNegocioException(PREFIXO_REGRA + " tpEvento obrigatorio.");
        }
        if (!TIPOS_VALIDOS.contains(novo.getTpEvento())) {
            throw new RegraNegocioException(PREFIXO_REGRA + " tpEvento invalido. Valores aceitos: " + TIPOS_VALIDOS);
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

        try (Connection conn = new ConexaoFactory().conexao()) {
            if (new EncaminhamentoDao(conn).buscarPorId(novo.getFkEncamId()) == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Encaminhamento nao encontrado.");
            }
            long id = new AcompEventoDao(conn).inserirRetornandoId(novo);
            novo.setIdAcomp(id);
            return novo;
        }
    }

    public List<AcompEvento> listarPorEncaminhamento(long idEncam) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            if (new EncaminhamentoDao(conn).buscarPorId(idEncam) == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Encaminhamento nao encontrado.");
            }
            return new AcompEventoDao(conn).listarPorEncaminhamento(idEncam);
        }
    }
}
