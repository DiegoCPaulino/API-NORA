package br.com.fiap.nora.entities;

import java.time.LocalDate;

public class Colaborador {

    private Long idColab;
    private String nmColab;
    private String cpfColab;
    private String emailColab;
    private String cargoColab;
    private LocalDate dtEntrada;
    private String sttsColab;
    private Long fkEndId;

    public Colaborador() {}

    public Colaborador(Long idColab, String nmColab, String cpfColab, String emailColab,
                       String cargoColab, LocalDate dtEntrada, String sttsColab, Long fkEndId) {
        this.idColab = idColab;
        this.nmColab = nmColab;
        this.cpfColab = cpfColab;
        this.emailColab = emailColab;
        this.cargoColab = cargoColab;
        this.dtEntrada = dtEntrada;
        this.sttsColab = sttsColab;
        this.fkEndId = fkEndId;
    }

    public Long getIdColab() { return idColab; }
    public void setIdColab(Long idColab) { this.idColab = idColab; }

    public String getNmColab() { return nmColab; }
    public void setNmColab(String nmColab) { this.nmColab = nmColab; }

    public String getCpfColab() { return cpfColab; }
    public void setCpfColab(String cpfColab) { this.cpfColab = cpfColab; }

    public String getEmailColab() { return emailColab; }
    public void setEmailColab(String emailColab) { this.emailColab = emailColab; }

    public String getCargoColab() { return cargoColab; }
    public void setCargoColab(String cargoColab) { this.cargoColab = cargoColab; }

    public LocalDate getDtEntrada() { return dtEntrada; }
    public void setDtEntrada(LocalDate dtEntrada) { this.dtEntrada = dtEntrada; }

    public String getSttsColab() { return sttsColab; }
    public void setSttsColab(String sttsColab) { this.sttsColab = sttsColab; }

    public Long getFkEndId() { return fkEndId; }
    public void setFkEndId(Long fkEndId) { this.fkEndId = fkEndId; }

    @Override
    public String toString() {
        return "Colaborador{idColab=" + idColab + "\nnmColab=" + nmColab
                + "\nemailColab=" + emailColab + "\nsttsColab=" + sttsColab + "}";
    }
}
