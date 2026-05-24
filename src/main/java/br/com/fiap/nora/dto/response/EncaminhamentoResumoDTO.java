package br.com.fiap.nora.dto.response;

public class EncaminhamentoResumoDTO {

    private Long id;
    private String dataEncaminhamento;
    private String dentistaNome;
    private String prioridade;
    private String status;

    public EncaminhamentoResumoDTO() {}

    public EncaminhamentoResumoDTO(Long id, String dataEncaminhamento, String dentistaNome,
                                   String prioridade, String status) {
        this.id = id;
        this.dataEncaminhamento = dataEncaminhamento;
        this.dentistaNome = dentistaNome;
        this.prioridade = prioridade;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDataEncaminhamento() { return dataEncaminhamento; }
    public void setDataEncaminhamento(String dataEncaminhamento) { this.dataEncaminhamento = dataEncaminhamento; }

    public String getDentistaNome() { return dentistaNome; }
    public void setDentistaNome(String dentistaNome) { this.dentistaNome = dentistaNome; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
