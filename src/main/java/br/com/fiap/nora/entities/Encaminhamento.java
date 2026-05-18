package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class Encaminhamento {

    private long idEncaminhamento;
    private long idPaciente;
    private long idDentista;
    private long idTriagem;
    private LocalDateTime dtEncam;
    private LocalDateTime prevFollow;
    private String sttsEncam;
    private String matchAuto;
    private Double distKm;
    private String prioridade;
    private String metodoCalculo;
    private String observacao;

    public Encaminhamento() {}

    public Encaminhamento(long idEncaminhamento, long idPaciente, long idDentista, long idTriagem,
                          LocalDateTime dtEncam, LocalDateTime prevFollow, String sttsEncam,
                          String matchAuto, Double distKm, String prioridade,
                          String metodoCalculo, String observacao) {
        this.idEncaminhamento = idEncaminhamento;
        this.idPaciente = idPaciente;
        this.idDentista = idDentista;
        this.idTriagem = idTriagem;
        this.dtEncam = dtEncam;
        this.prevFollow = prevFollow;
        this.sttsEncam = sttsEncam;
        this.matchAuto = matchAuto;
        this.distKm = distKm;
        this.prioridade = prioridade;
        this.metodoCalculo = metodoCalculo;
        this.observacao = observacao;
    }

    public long getIdEncaminhamento() { return idEncaminhamento; }
    public void setIdEncaminhamento(long idEncaminhamento) { this.idEncaminhamento = idEncaminhamento; }

    public long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(long idPaciente) { this.idPaciente = idPaciente; }

    public long getIdDentista() { return idDentista; }
    public void setIdDentista(long idDentista) { this.idDentista = idDentista; }

    public long getIdTriagem() { return idTriagem; }
    public void setIdTriagem(long idTriagem) { this.idTriagem = idTriagem; }

    public LocalDateTime getDtEncam() { return dtEncam; }
    public void setDtEncam(LocalDateTime dtEncam) { this.dtEncam = dtEncam; }

    public LocalDateTime getPrevFollow() { return prevFollow; }
    public void setPrevFollow(LocalDateTime prevFollow) { this.prevFollow = prevFollow; }

    public String getSttsEncam() { return sttsEncam; }
    public void setSttsEncam(String sttsEncam) { this.sttsEncam = sttsEncam; }

    public String getMatchAuto() { return matchAuto; }
    public void setMatchAuto(String matchAuto) { this.matchAuto = matchAuto; }

    public Double getDistKm() { return distKm; }
    public void setDistKm(Double distKm) { this.distKm = distKm; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getMetodoCalculo() { return metodoCalculo; }
    public void setMetodoCalculo(String metodoCalculo) { this.metodoCalculo = metodoCalculo; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    // Regra obrigatória — CLAUDE.md §9.4 — encerra o encaminhamento; paciente.stts_trat pendente de confirmação
    public void encerrarEncaminhamento() {
        this.sttsEncam = "concluido";
    }

    @Override
    public String toString() {
        return "Encaminhamento{idEncaminhamento=" + idEncaminhamento + "\nidPaciente=" + idPaciente
                + "\nidDentista=" + idDentista + "\nsttsEncam=" + sttsEncam
                + "\nprioridade=" + prioridade + "\nmatchAuto=" + matchAuto + "}";
    }
}
