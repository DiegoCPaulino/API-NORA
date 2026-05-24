package br.com.fiap.nora.entities;

import java.time.LocalDate;

public class Paciente {

    private Long idPac;
    private LocalDate dtAprov;
    private String sttsTrat;
    private Long idTriagRef;
    private Long fkPessId;

    public Paciente() {}

    public Paciente(Long idPac, LocalDate dtAprov, String sttsTrat, Long idTriagRef, Long fkPessId) {
        this.idPac = idPac;
        this.dtAprov = dtAprov;
        this.sttsTrat = sttsTrat;
        this.idTriagRef = idTriagRef;
        this.fkPessId = fkPessId;
    }

    public Long getIdPac() { return idPac; }
    public void setIdPac(Long idPac) { this.idPac = idPac; }

    public LocalDate getDtAprov() { return dtAprov; }
    public void setDtAprov(LocalDate dtAprov) { this.dtAprov = dtAprov; }

    public String getSttsTrat() { return sttsTrat; }
    public void setSttsTrat(String sttsTrat) { this.sttsTrat = sttsTrat; }

    public Long getIdTriagRef() { return idTriagRef; }
    public void setIdTriagRef(Long idTriagRef) { this.idTriagRef = idTriagRef; }

    public Long getFkPessId() { return fkPessId; }
    public void setFkPessId(Long fkPessId) { this.fkPessId = fkPessId; }

    @Override
    public String toString() {
        return "Paciente{idPac=" + idPac + "\nfkPessId=" + fkPessId + "\nsttsTrat=" + sttsTrat + "}";
    }
}
