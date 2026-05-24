package br.com.fiap.nora.dto.response;

public class DadosPacienteDTO {

    private Long id;
    private String nome;
    private String telefone;

    public DadosPacienteDTO() {}

    public DadosPacienteDTO(Long id, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
