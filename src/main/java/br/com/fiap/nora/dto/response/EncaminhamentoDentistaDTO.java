package br.com.fiap.nora.dto.response;

public class EncaminhamentoDentistaDTO {

    private Long id;
    private String dataEncaminhamento;
    private String pacienteNome;
    private String prioridade;
    private String status;

    public EncaminhamentoDentistaDTO() {}

    public EncaminhamentoDentistaDTO(Long id, String dataEncaminhamento, String pacienteNome,
                                     String prioridade, String status) {
        this.id = id;
        this.dataEncaminhamento = dataEncaminhamento;
        this.pacienteNome = pacienteNome;
        this.prioridade = prioridade;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDataEncaminhamento() { return dataEncaminhamento; }
    public void setDataEncaminhamento(String dataEncaminhamento) { this.dataEncaminhamento = dataEncaminhamento; }

    public String getPacienteNome() { return pacienteNome; }
    public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
