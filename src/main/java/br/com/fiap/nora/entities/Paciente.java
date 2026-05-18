package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class Paciente {

    private long idPaciente;
    private long idPessoa;
    private long idTriagem;
    private int idade;
    private LocalDateTime dtAprov;
    private String sttsTrat;
    private String observacoes;

    public Paciente() {}

    public Paciente(long idPaciente, long idPessoa, long idTriagem, int idade,
                    LocalDateTime dtAprov, String sttsTrat, String observacoes) {
        this.idPaciente = idPaciente;
        this.idPessoa = idPessoa;
        this.idTriagem = idTriagem;
        this.idade = idade;
        this.dtAprov = dtAprov;
        this.sttsTrat = sttsTrat;
        this.observacoes = observacoes;
    }

    public long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(long idPaciente) { this.idPaciente = idPaciente; }

    public long getIdPessoa() { return idPessoa; }
    public void setIdPessoa(long idPessoa) { this.idPessoa = idPessoa; }

    public long getIdTriagem() { return idTriagem; }
    public void setIdTriagem(long idTriagem) { this.idTriagem = idTriagem; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public LocalDateTime getDtAprov() { return dtAprov; }
    public void setDtAprov(LocalDateTime dtAprov) { this.dtAprov = dtAprov; }

    public String getSttsTrat() { return sttsTrat; }
    public void setSttsTrat(String sttsTrat) { this.sttsTrat = sttsTrat; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() {
        return "Paciente{idPaciente=" + idPaciente + "\nidPessoa=" + idPessoa
                + "\nidTriagem=" + idTriagem + "\nsttsTrat=" + sttsTrat + "}";
    }
}
