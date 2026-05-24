package br.com.fiap.nora.dto.response;

public class TriagemResumoDTO {

    private Long id;
    private String dataTriagem;
    private String elegibilidade;
    private String prioridade;
    private String statusTriagem;
    private Double nivelUrgenciaIA;
    private String problemaBucal;
    private String rendaFamiliar;

    public TriagemResumoDTO() {}

    public TriagemResumoDTO(Long id, String dataTriagem, String elegibilidade, String prioridade,
                            String statusTriagem, Double nivelUrgenciaIA, String problemaBucal,
                            String rendaFamiliar) {
        this.id = id;
        this.dataTriagem = dataTriagem;
        this.elegibilidade = elegibilidade;
        this.prioridade = prioridade;
        this.statusTriagem = statusTriagem;
        this.nivelUrgenciaIA = nivelUrgenciaIA;
        this.problemaBucal = problemaBucal;
        this.rendaFamiliar = rendaFamiliar;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDataTriagem() { return dataTriagem; }
    public void setDataTriagem(String dataTriagem) { this.dataTriagem = dataTriagem; }

    public String getElegibilidade() { return elegibilidade; }
    public void setElegibilidade(String elegibilidade) { this.elegibilidade = elegibilidade; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatusTriagem() { return statusTriagem; }
    public void setStatusTriagem(String statusTriagem) { this.statusTriagem = statusTriagem; }

    public Double getNivelUrgenciaIA() { return nivelUrgenciaIA; }
    public void setNivelUrgenciaIA(Double nivelUrgenciaIA) { this.nivelUrgenciaIA = nivelUrgenciaIA; }

    public String getProblemaBucal() { return problemaBucal; }
    public void setProblemaBucal(String problemaBucal) { this.problemaBucal = problemaBucal; }

    public String getRendaFamiliar() { return rendaFamiliar; }
    public void setRendaFamiliar(String rendaFamiliar) { this.rendaFamiliar = rendaFamiliar; }
}
