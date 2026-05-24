package br.com.fiap.nora.dto.response;

public class PacienteEmbutidoDTO {

    private Long id;
    private String nome;
    private int idade;
    private String bairro;
    private String problemaBucal;
    private Double nivelUrgenciaIA;

    public PacienteEmbutidoDTO() {}

    public PacienteEmbutidoDTO(Long id, String nome, int idade, String bairro,
                               String problemaBucal, Double nivelUrgenciaIA) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.bairro = bairro;
        this.problemaBucal = problemaBucal;
        this.nivelUrgenciaIA = nivelUrgenciaIA;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getProblemaBucal() { return problemaBucal; }
    public void setProblemaBucal(String problemaBucal) { this.problemaBucal = problemaBucal; }

    public Double getNivelUrgenciaIA() { return nivelUrgenciaIA; }
    public void setNivelUrgenciaIA(Double nivelUrgenciaIA) { this.nivelUrgenciaIA = nivelUrgenciaIA; }
}
