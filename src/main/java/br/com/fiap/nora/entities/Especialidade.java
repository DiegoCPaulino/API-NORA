package br.com.fiap.nora.entities;

public class Especialidade {

    private long idEspecialidade;
    private String nome;
    private String descricao;
    private String statusEsp;

    public Especialidade() {}

    public Especialidade(long idEspecialidade, String nome, String descricao, String statusEsp) {
        this.idEspecialidade = idEspecialidade;
        this.nome = nome;
        this.descricao = descricao;
        this.statusEsp = statusEsp;
    }

    public long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatusEsp() { return statusEsp; }
    public void setStatusEsp(String statusEsp) { this.statusEsp = statusEsp; }

    @Override
    public String toString() {
        return "Especialidade{idEspecialidade=" + idEspecialidade + "\nnome=" + nome
                + "\nstatusEsp=" + statusEsp + "}";
    }
}
