package br.com.fiap.nora.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDate;

public class Encaminhamento {

    private Long idEncam;
    private LocalDate dtEncam;
    @JsonAlias({"prioridade", "prior_encam"})
    private String priorEncam;
    private String obsEncam;
    @JsonAlias({"previsaoFollowUp", "prev_follow"})
    private LocalDate prevFollow;
    @JsonAlias({"status", "stts_encam"})
    private String sttsEncam;
    @JsonAlias("match_auto")
    private String matchAuto;
    @JsonAlias("dist_km")
    private Double distKm;
    @JsonAlias({"idPaciente", "pacienteId", "fk_pac_id"})
    private Long fkPacId;
    @JsonAlias({"idDentista", "dentistaId", "fk_dent_id"})
    private Long fkDentId;
    @JsonAlias({"idUsuario", "usuarioId", "fk_user_id"})
    private Long fkUserId;

    public Encaminhamento() {}

    public Encaminhamento(Long idEncam, LocalDate dtEncam, String priorEncam, String obsEncam,
                          LocalDate prevFollow, String sttsEncam, String matchAuto, Double distKm,
                          Long fkPacId, Long fkDentId, Long fkUserId) {
        this.idEncam = idEncam;
        this.dtEncam = dtEncam;
        this.priorEncam = priorEncam;
        this.obsEncam = obsEncam;
        this.prevFollow = prevFollow;
        this.sttsEncam = sttsEncam;
        this.matchAuto = matchAuto;
        this.distKm = distKm;
        this.fkPacId = fkPacId;
        this.fkDentId = fkDentId;
        this.fkUserId = fkUserId;
    }

    public Long getIdEncam() { return idEncam; }
    public void setIdEncam(Long idEncam) { this.idEncam = idEncam; }

    public LocalDate getDtEncam() { return dtEncam; }
    public void setDtEncam(LocalDate dtEncam) { this.dtEncam = dtEncam; }

    public String getPriorEncam() { return priorEncam; }
    public void setPriorEncam(String priorEncam) { this.priorEncam = priorEncam; }

    public String getObsEncam() { return obsEncam; }
    public void setObsEncam(String obsEncam) { this.obsEncam = obsEncam; }

    public LocalDate getPrevFollow() { return prevFollow; }
    public void setPrevFollow(LocalDate prevFollow) { this.prevFollow = prevFollow; }

    public String getSttsEncam() { return sttsEncam; }
    public void setSttsEncam(String sttsEncam) { this.sttsEncam = sttsEncam; }

    public String getMatchAuto() { return matchAuto; }
    public void setMatchAuto(String matchAuto) { this.matchAuto = matchAuto; }

    public Double getDistKm() { return distKm; }
    public void setDistKm(Double distKm) { this.distKm = distKm; }

    public Long getFkPacId() { return fkPacId; }
    public void setFkPacId(Long fkPacId) { this.fkPacId = fkPacId; }

    public Long getFkDentId() { return fkDentId; }
    public void setFkDentId(Long fkDentId) { this.fkDentId = fkDentId; }

    public Long getFkUserId() { return fkUserId; }
    public void setFkUserId(Long fkUserId) { this.fkUserId = fkUserId; }

    // Encerra o encaminhamento. Atualizacao de stts_trat do paciente fica a cargo do fluxo de acompanhamento.
    public void encerrarEncaminhamento() {
        this.sttsEncam = "concluido";
    }

    @Override
    public String toString() {
        return "Encaminhamento{idEncam=" + idEncam + "\nfkPacId=" + fkPacId
                + "\nfkDentId=" + fkDentId + "\nsttsEncam=" + sttsEncam
                + "\npriorEncam=" + priorEncam + "\nmatchAuto=" + matchAuto + "}";
    }
}
