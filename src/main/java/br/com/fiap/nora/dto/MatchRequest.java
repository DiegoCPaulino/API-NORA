package br.com.fiap.nora.dto;

public class MatchRequest {

    private long id_paciente;

    public MatchRequest() {}

    public MatchRequest(long id_paciente) {
        this.id_paciente = id_paciente;
    }

    public long getId_paciente() { return id_paciente; }
    public void setId_paciente(long id_paciente) { this.id_paciente = id_paciente; }
}
