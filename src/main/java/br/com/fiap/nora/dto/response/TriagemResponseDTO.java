package br.com.fiap.nora.dto.response;

public class TriagemResponseDTO {

    private Long id;
    private String elegibilidade;
    private String prioridade;
    private String sexo;
    private String problemaBucal;
    private String rendaFamiliar;
    private Double nivelUrgenciaIA;
    private Double confiancaIA;
    private String sugestaoIA;
    private String observacao;
    private String dataTriagem;
    private String status;
    private String decisao;
    private Long pessoaId;

    public TriagemResponseDTO() {}

    public TriagemResponseDTO(Long id, String elegibilidade, String prioridade, String sexo,
                              String problemaBucal, String rendaFamiliar, Double nivelUrgenciaIA,
                              Double confiancaIA, String sugestaoIA, String observacao,
                              String dataTriagem, String status, String decisao, Long pessoaId) {
        this.id = id;
        this.elegibilidade = elegibilidade;
        this.prioridade = prioridade;
        this.sexo = sexo;
        this.problemaBucal = problemaBucal;
        this.rendaFamiliar = rendaFamiliar;
        this.nivelUrgenciaIA = nivelUrgenciaIA;
        this.confiancaIA = confiancaIA;
        this.sugestaoIA = sugestaoIA;
        this.observacao = observacao;
        this.dataTriagem = dataTriagem;
        this.status = status;
        this.decisao = decisao;
        this.pessoaId = pessoaId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getElegibilidade() { return elegibilidade; }
    public void setElegibilidade(String elegibilidade) { this.elegibilidade = elegibilidade; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getProblemaBucal() { return problemaBucal; }
    public void setProblemaBucal(String problemaBucal) { this.problemaBucal = problemaBucal; }

    public String getRendaFamiliar() { return rendaFamiliar; }
    public void setRendaFamiliar(String rendaFamiliar) { this.rendaFamiliar = rendaFamiliar; }

    public Double getNivelUrgenciaIA() { return nivelUrgenciaIA; }
    public void setNivelUrgenciaIA(Double nivelUrgenciaIA) { this.nivelUrgenciaIA = nivelUrgenciaIA; }

    public Double getConfiancaIA() { return confiancaIA; }
    public void setConfiancaIA(Double confiancaIA) { this.confiancaIA = confiancaIA; }

    public String getSugestaoIA() { return sugestaoIA; }
    public void setSugestaoIA(String sugestaoIA) { this.sugestaoIA = sugestaoIA; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getDataTriagem() { return dataTriagem; }
    public void setDataTriagem(String dataTriagem) { this.dataTriagem = dataTriagem; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDecisao() { return decisao; }
    public void setDecisao(String decisao) { this.decisao = decisao; }

    public Long getPessoaId() { return pessoaId; }
    public void setPessoaId(Long pessoaId) { this.pessoaId = pessoaId; }
}
