package br.com.fiap.nora.bo;

import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.entities.Pessoa;

import java.sql.SQLException;
import java.util.List;

public class PessoaBO {

    public List<Pessoa> listar() throws SQLException, ClassNotFoundException {
        return new PessoaDao().selecionar();
    }

    public Pessoa buscarPorId(long id) throws SQLException, ClassNotFoundException {
        return new PessoaDao().buscarPorId(id);
    }

    public String criar(Pessoa p) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        if (p.getNomeCompleto() == null || p.getNomeCompleto().isBlank()) {
            throw new IllegalArgumentException("Nome completo obrigatorio.");
        }
        if (p.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento obrigatoria.");
        }
        if (p.getCanalOrig() == null || p.getCanalOrig().isBlank()) {
            throw new IllegalArgumentException("Canal de origem obrigatorio.");
        }
        if (p.getSttsPess() == null || p.getSttsPess().isBlank()) {
            p.setSttsPess("lead");
        }
        return new PessoaDao().inserir(p);
    }

    public String atualizar(Pessoa p) throws SQLException, ClassNotFoundException {
        // Regra de negocios
        if (p.getNomeCompleto() == null || p.getNomeCompleto().isBlank()) {
            throw new IllegalArgumentException("Nome completo obrigatorio.");
        }
        if (p.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento obrigatoria.");
        }
        return new PessoaDao().atualizar(p);
    }
}
