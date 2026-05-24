package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.ConversaDao;
import br.com.fiap.nora.dao.MensagemDao;
import br.com.fiap.nora.dto.response.MensagemDTO;
import br.com.fiap.nora.entities.Conversa;
import br.com.fiap.nora.entities.Mensagem;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import br.com.fiap.nora.mapper.ConversaMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MensagemBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";

    private static final List<String> ENVIADO_POR_VALIDOS =
            Arrays.asList("usuario", "nora_ia", "externo");
    private static final List<String> DIRECAO_VALIDA =
            Arrays.asList("entrada", "saida");
    private static final List<String> TP_MENS_VALIDOS =
            Arrays.asList("texto", "audio", "imagem", "documento");

    public MensagemDTO criar(Mensagem nova) throws SQLException, ClassNotFoundException {
        if (nova.getFkConvId() == null || nova.getFkConvId() <= 0) {
            throw new RegraNegocioException("REGRA: fkConvId e obrigatorio.");
        }
        if (nova.getConteudo() == null || nova.getConteudo().isBlank()) {
            throw new RegraNegocioException("REGRA: conteudo da mensagem e obrigatorio.");
        }
        if (nova.getEnviadoPor() == null || nova.getEnviadoPor().isBlank()) {
            throw new RegraNegocioException("REGRA: enviadoPor e obrigatorio. Valores: " + ENVIADO_POR_VALIDOS);
        }
        if (!ENVIADO_POR_VALIDOS.contains(nova.getEnviadoPor())) {
            throw new RegraNegocioException("REGRA: enviadoPor invalido. Valores aceitos: " + ENVIADO_POR_VALIDOS);
        }
        if (nova.getDirecao() == null || nova.getDirecao().isBlank()) {
            throw new RegraNegocioException("REGRA: direcao e obrigatoria. Valores: " + DIRECAO_VALIDA);
        }
        if (!DIRECAO_VALIDA.contains(nova.getDirecao())) {
            throw new RegraNegocioException("REGRA: direcao invalida. Valores aceitos: " + DIRECAO_VALIDA);
        }
        if (nova.getTpMens() != null && !nova.getTpMens().isBlank()
                && !TP_MENS_VALIDOS.contains(nova.getTpMens())) {
            throw new RegraNegocioException("REGRA: tpMens invalido. Valores aceitos: " + TP_MENS_VALIDOS);
        }
        if (nova.getTpMens() == null || nova.getTpMens().isBlank()) nova.setTpMens("texto");

        try (Connection conn = new ConexaoFactory().conexao()) {
            Conversa conversa = new ConversaDao(conn).buscarPorId(nova.getFkConvId());
            if (conversa == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Conversa nao encontrada.");
            }
            long id = new MensagemDao(conn).inserirRetornandoId(nova);
            nova.setIdMens(id);
            return ConversaMapper.toMensagemDTO(nova);
        }
    }

    public List<MensagemDTO> listarPorConversa(long idConversa) throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Conversa conversa = new ConversaDao(conn).buscarPorId(idConversa);
            if (conversa == null) {
                throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Conversa nao encontrada.");
            }
            List<Mensagem> mensagens = new MensagemDao(conn).listarPorConversa(idConversa);
            List<MensagemDTO> dtos = new ArrayList<>();
            for (Mensagem m : mensagens) {
                dtos.add(ConversaMapper.toMensagemDTO(m));
            }
            return dtos;
        }
    }
}
