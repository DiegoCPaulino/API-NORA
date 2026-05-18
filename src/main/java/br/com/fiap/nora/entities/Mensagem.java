package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class Mensagem {

    private long idMensagem;
    private long idConversa;
    private String enviadoPor;
    private String direcao;
    private String conteudo;
    private String tipoMensagem;
    private LocalDateTime dataEnvio;
    private String lida;

    public Mensagem() {}

    public Mensagem(long idMensagem, long idConversa, String enviadoPor, String direcao,
                    String conteudo, String tipoMensagem, LocalDateTime dataEnvio, String lida) {
        this.idMensagem = idMensagem;
        this.idConversa = idConversa;
        this.enviadoPor = enviadoPor;
        this.direcao = direcao;
        this.conteudo = conteudo;
        this.tipoMensagem = tipoMensagem;
        this.dataEnvio = dataEnvio;
        this.lida = lida;
    }

    public long getIdMensagem() { return idMensagem; }
    public void setIdMensagem(long idMensagem) { this.idMensagem = idMensagem; }

    public long getIdConversa() { return idConversa; }
    public void setIdConversa(long idConversa) { this.idConversa = idConversa; }

    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public String getDirecao() { return direcao; }
    public void setDirecao(String direcao) { this.direcao = direcao; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getTipoMensagem() { return tipoMensagem; }
    public void setTipoMensagem(String tipoMensagem) { this.tipoMensagem = tipoMensagem; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public String getLida() { return lida; }
    public void setLida(String lida) { this.lida = lida; }

    @Override
    public String toString() {
        return "Mensagem{idMensagem=" + idMensagem + "\nidConversa=" + idConversa
                + "\nenviadoPor=" + enviadoPor + "\ndirecao=" + direcao
                + "\ntipoMensagem=" + tipoMensagem + "}";
    }
}
