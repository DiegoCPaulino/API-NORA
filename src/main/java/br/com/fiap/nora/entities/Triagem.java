package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class Triagem {

    private long idTriagem;
    private long idPessoa;
    private int idade;
    private String elegTriag;
    private String priorTriag;
    private String sexoPess;
    private String problemaBucal;
    private String rendaFamiliar;
    private Double nivelUrgIa;
    private Double confIa;
    private String sttsTriag;
    private String decisao;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Triagem() {}

    public Triagem(long idTriagem, long idPessoa, int idade, String elegTriag, String priorTriag,
                   String sexoPess, String problemaBucal, String rendaFamiliar, Double nivelUrgIa,
                   Double confIa, String sttsTriag, String decisao, String observacoes,
                   LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.idTriagem = idTriagem;
        this.idPessoa = idPessoa;
        this.idade = idade;
        this.elegTriag = elegTriag;
        this.priorTriag = priorTriag;
        this.sexoPess = sexoPess;
        this.problemaBucal = problemaBucal;
        this.rendaFamiliar = rendaFamiliar;
        this.nivelUrgIa = nivelUrgIa;
        this.confIa = confIa;
        this.sttsTriag = sttsTriag;
        this.decisao = decisao;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public long getIdTriagem() { return idTriagem; }
    public void setIdTriagem(long idTriagem) { this.idTriagem = idTriagem; }

    public long getIdPessoa() { return idPessoa; }
    public void setIdPessoa(long idPessoa) { this.idPessoa = idPessoa; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public String getElegTriag() { return elegTriag; }
    public void setElegTriag(String elegTriag) { this.elegTriag = elegTriag; }

    public String getPriorTriag() { return priorTriag; }
    public void setPriorTriag(String priorTriag) { this.priorTriag = priorTriag; }

    public String getSexoPess() { return sexoPess; }
    public void setSexoPess(String sexoPess) { this.sexoPess = sexoPess; }

    public String getProblemaBucal() { return problemaBucal; }
    public void setProblemaBucal(String problemaBucal) { this.problemaBucal = problemaBucal; }

    public String getRendaFamiliar() { return rendaFamiliar; }
    public void setRendaFamiliar(String rendaFamiliar) { this.rendaFamiliar = rendaFamiliar; }

    public Double getNivelUrgIa() { return nivelUrgIa; }
    public void setNivelUrgIa(Double nivelUrgIa) { this.nivelUrgIa = nivelUrgIa; }

    public Double getConfIa() { return confIa; }
    public void setConfIa(Double confIa) { this.confIa = confIa; }

    public String getSttsTriag() { return sttsTriag; }
    public void setSttsTriag(String sttsTriag) { this.sttsTriag = sttsTriag; }

    public String getDecisao() { return decisao; }
    public void setDecisao(String decisao) { this.decisao = decisao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    // Faixa de elegibilidade do Projeto Nora: adolescentes de 11 a 17 anos.
    public String definirElegibilidade(int idade) {
        if (idade >= 11 && idade <= 17) return "elegivel";
        return "inelegivel";
    }

    // Prioridade calculada a partir do nível de urgência retornado pelo serviço de predição ML.
    public String calcularPrioridade(double nivelIA) {
        if (nivelIA >= 4.0) return "urgente";
        if (nivelIA >= 3.0) return "alta";
        if (nivelIA >= 2.0) return "media";
        return "baixa";
    }

    @Override
    public String toString() {
        return "Triagem{idTriagem=" + idTriagem + "\nidPessoa=" + idPessoa + "\nidade=" + idade
                + "\nelegTriag=" + elegTriag + "\npriorTriag=" + priorTriag
                + "\nsttsTriag=" + sttsTriag + "\ndecisao=" + decisao + "}";
    }
}
