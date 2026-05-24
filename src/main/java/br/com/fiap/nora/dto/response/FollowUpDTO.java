package br.com.fiap.nora.dto.response;

public class FollowUpDTO {

    private Long id;
    private String tipoEvento;
    private String descricao;
    private String origem;
    private String dataEvento;

    public FollowUpDTO() {}

    public FollowUpDTO(Long id, String tipoEvento, String descricao, String origem, String dataEvento) {
        this.id = id;
        this.tipoEvento = tipoEvento;
        this.descricao = descricao;
        this.origem = origem;
        this.dataEvento = dataEvento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public String getDataEvento() { return dataEvento; }
    public void setDataEvento(String dataEvento) { this.dataEvento = dataEvento; }
}
