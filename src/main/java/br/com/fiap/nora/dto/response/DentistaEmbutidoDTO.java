package br.com.fiap.nora.dto.response;

import java.util.List;

public class DentistaEmbutidoDTO {

    private Long id;
    private String nome;
    private String cro;
    private String bairro;
    private List<String> especialidades;
    private Double distanciaKm;

    public DentistaEmbutidoDTO() {}

    public DentistaEmbutidoDTO(Long id, String nome, String cro, String bairro,
                               List<String> especialidades, Double distanciaKm) {
        this.id = id;
        this.nome = nome;
        this.cro = cro;
        this.bairro = bairro;
        this.especialidades = especialidades;
        this.distanciaKm = distanciaKm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCro() { return cro; }
    public void setCro(String cro) { this.cro = cro; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public List<String> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<String> especialidades) { this.especialidades = especialidades; }

    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }
}
