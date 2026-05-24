package br.com.fiap.nora.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class Triagem {

    private Long idTriag;
    private String elegTriag;
    private String priorTriag;
    @JsonAlias("sexo")
    private String sexoPess;
    private String problemaBucal;
    private String rendaFamiliar;
    private Double nivelUrgIa;
    private Double confIa;
    private String sugestaoIa;
    @JsonProperty("observacoes")
    @JsonAlias({"obsTriag", "obs_triag"})
    private String obsTriag;
    private LocalDate dtTriag;
    @JsonProperty("status")
    @JsonAlias({"sttsTriag", "stts_triag"})
    private String sttsTriag;
    private String decisao;
    @JsonProperty("pessoaId")
    @JsonAlias("fkPessId")
    private Long fkPessId;
    private Long fkUserId;

    public Triagem() {}

    public Triagem(Long idTriag, String elegTriag, String priorTriag, String sexoPess,
                   String problemaBucal, String rendaFamiliar, Double nivelUrgIa, Double confIa,
                   String sugestaoIa, String obsTriag, LocalDate dtTriag, String sttsTriag,
                   String decisao, Long fkPessId, Long fkUserId) {
        this.idTriag = idTriag;
        this.elegTriag = elegTriag;
        this.priorTriag = priorTriag;
        this.sexoPess = sexoPess;
        this.problemaBucal = problemaBucal;
        this.rendaFamiliar = rendaFamiliar;
        this.nivelUrgIa = nivelUrgIa;
        this.confIa = confIa;
        this.sugestaoIa = sugestaoIa;
        this.obsTriag = obsTriag;
        this.dtTriag = dtTriag;
        this.sttsTriag = sttsTriag;
        this.decisao = decisao;
        this.fkPessId = fkPessId;
        this.fkUserId = fkUserId;
    }

    public Long getIdTriag() { return idTriag; }
    public void setIdTriag(Long idTriag) { this.idTriag = idTriag; }

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

    public String getSugestaoIa() { return sugestaoIa; }
    public void setSugestaoIa(String sugestaoIa) { this.sugestaoIa = sugestaoIa; }

    public String getObsTriag() { return obsTriag; }
    public void setObsTriag(String obsTriag) { this.obsTriag = obsTriag; }

    public LocalDate getDtTriag() { return dtTriag; }
    public void setDtTriag(LocalDate dtTriag) { this.dtTriag = dtTriag; }

    public String getSttsTriag() { return sttsTriag; }
    public void setSttsTriag(String sttsTriag) { this.sttsTriag = sttsTriag; }

    public String getDecisao() { return decisao; }
    public void setDecisao(String decisao) { this.decisao = decisao; }

    public Long getFkPessId() { return fkPessId; }
    public void setFkPessId(Long fkPessId) { this.fkPessId = fkPessId; }

    public Long getFkUserId() { return fkUserId; }
    public void setFkUserId(Long fkUserId) { this.fkUserId = fkUserId; }

    // Faixa de elegibilidade do Projeto Nora: adolescentes de 11 a 17 anos.
    public String definirElegibilidade(int idade) {
        if (idade >= 11 && idade <= 17) return "elegivel";
        return "inelegivel";
    }

    // Prioridade calculada a partir do nivel de urgencia retornado pelo servico de predicao ML.
    public String calcularPrioridade(double nivelIA) {
        if (nivelIA >= 4.0) return "urgente";
        if (nivelIA >= 3.0) return "alta";
        if (nivelIA >= 2.0) return "media";
        return "baixa";
    }

    @Override
    public String toString() {
        return "Triagem{idTriag=" + idTriag + "\nfkPessId=" + fkPessId
                + "\nelegTriag=" + elegTriag + "\npriorTriag=" + priorTriag
                + "\nsttsTriag=" + sttsTriag + "\ndecisao=" + decisao + "}";
    }
}
