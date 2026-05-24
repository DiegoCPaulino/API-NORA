package br.com.fiap.nora.dto.response;

public class MensagemDTO {

    private Long id;
    private String enviadoPor;
    private String direcao;
    private String conteudo;
    private String tipoMensagem;
    private String dataEnvio;

    public MensagemDTO() {}

    public MensagemDTO(Long id, String enviadoPor, String direcao, String conteudo,
                       String tipoMensagem, String dataEnvio) {
        this.id = id;
        this.enviadoPor = enviadoPor;
        this.direcao = direcao;
        this.conteudo = conteudo;
        this.tipoMensagem = tipoMensagem;
        this.dataEnvio = dataEnvio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public String getDirecao() { return direcao; }
    public void setDirecao(String direcao) { this.direcao = direcao; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getTipoMensagem() { return tipoMensagem; }
    public void setTipoMensagem(String tipoMensagem) { this.tipoMensagem = tipoMensagem; }

    public String getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(String dataEnvio) { this.dataEnvio = dataEnvio; }
}
