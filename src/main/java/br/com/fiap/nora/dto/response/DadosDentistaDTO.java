package br.com.fiap.nora.dto.response;

public class DadosDentistaDTO {

    private Long id;
    private String nome;
    private String cro;

    public DadosDentistaDTO() {}

    public DadosDentistaDTO(Long id, String nome, String cro) {
        this.id = id;
        this.nome = nome;
        this.cro = cro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCro() { return cro; }
    public void setCro(String cro) { this.cro = cro; }
}
