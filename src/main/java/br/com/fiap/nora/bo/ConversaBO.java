package br.com.fiap.nora.bo;

import br.com.fiap.nora.dao.ConversaDao;
import br.com.fiap.nora.entities.Conversa;
import br.com.fiap.nora.exceptions.RegraNegocioException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConversaBO {

    public static final String PREFIXO_NAO_ENCONTRADA = "NAO_ENCONTRADA:";
    public static final String PREFIXO_REGRA = "REGRA:";

    public List<Conversa> listar(String contexto, String stts) throws SQLException, ClassNotFoundException {
        List<Conversa> todas = new ConversaDao().selecionar();
        if (contexto == null && stts == null) return todas;
        List<Conversa> filtradas = new ArrayList<>();
        for (Conversa c : todas) {
            if (contexto != null && !contexto.equals(c.getContexto())) continue;
            if (stts != null && !stts.equals(c.getSttsConv())) continue;
            filtradas.add(c);
        }
        return filtradas;
    }

    public Conversa buscarPorId(long id) throws SQLException, ClassNotFoundException {
        Conversa c = new ConversaDao().buscarPorId(id);
        if (c == null) throw new RegraNegocioException(PREFIXO_NAO_ENCONTRADA + " Conversa nao encontrada.");
        return c;
    }

    public Conversa criar(Conversa nova) throws SQLException, ClassNotFoundException {
        // Regra de exclusividade (CLAUDE.md §10): exatamente uma FK deve estar preenchida
        int fksPreenchidas = 0;
        if (nova.getIdPessoa() != null) fksPreenchidas++;
        if (nova.getIdPaciente() != null) fksPreenchidas++;
        if (nova.getIdDentista() != null) fksPreenchidas++;
        if (fksPreenchidas != 1) {
            throw new RegraNegocioException(PREFIXO_REGRA
                    + " Conversa deve referenciar exatamente uma entre pessoa, paciente ou dentista.");
        }

        // Upsert lógico: evita duplicidade conforme CLAUDE.md §16.12
        ConversaDao dao = new ConversaDao();
        Conversa existente = buscarAtiva(dao, nova);
        if (existente != null) return existente;

        if (nova.getSttsConv() == null || nova.getSttsConv().isBlank()) nova.setSttsConv("aberta");

        long id = dao.inserirRetornandoId(nova);
        nova.setIdConversa(id);
        return nova;
    }

    private Conversa buscarAtiva(ConversaDao dao, Conversa nova) throws SQLException {
        if (nova.getIdPessoa() != null) return dao.buscarAtivaPorPessoa(nova.getIdPessoa());
        if (nova.getIdPaciente() != null) return dao.buscarAtivaPorPaciente(nova.getIdPaciente());
        if (nova.getIdDentista() != null) return dao.buscarAtivaPorDentista(nova.getIdDentista());
        return null;
    }
}
