package br.com.fiap.nora.bo;

import br.com.fiap.nora.dao.ConversaDao;
import br.com.fiap.nora.dao.MensagemDao;
import br.com.fiap.nora.entities.Mensagem;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.SQLException;
import java.util.List;

public class MensagemBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";

    public Mensagem criar(Mensagem nova) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        if (nova.getIdConversa() <= 0) {
            throw new RegraNegocioException("REGRA: ID da conversa obrigatorio.");
        }
        if (nova.getConteudo() == null || nova.getConteudo().isBlank()) {
            throw new RegraNegocioException("REGRA: Conteudo da mensagem obrigatorio.");
        }
        if (new ConversaDao().buscarPorId(nova.getIdConversa()) == null) {
            throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Conversa nao encontrada.");
        }
        if (nova.getLida() == null || nova.getLida().isBlank()) nova.setLida("N");

        MensagemDao dao = new MensagemDao();
        long id = dao.inserirRetornandoId(nova);
        nova.setIdMensagem(id);
        return nova;
    }

    public List<Mensagem> listarPorConversa(long idConversa) throws SQLException, ClassNotFoundException {
        return new MensagemDao().listarPorConversa(idConversa);
    }
}
