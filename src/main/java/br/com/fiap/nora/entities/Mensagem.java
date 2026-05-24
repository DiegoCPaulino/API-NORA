package br.com.fiap.nora.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDate;

public class Mensagem {

    private Long idMens;
    private String enviadoPor;
    private String direcao;
    private String conteudo;
    @JsonAlias({"tipoMensagem", "tipoMens", "tipo_mensagem"})
    private String tpMens;
    private LocalDate dtEnvio;
    @JsonAlias({"idConversa", "conversaId"})
    private Long fkConvId;

    public Mensagem() {}

    public Mensagem(Long idMens, String enviadoPor, String direcao, String conteudo,
                    String tpMens, LocalDate dtEnvio, Long fkConvId) {
        this.idMens = idMens;
        this.enviadoPor = enviadoPor;
        this.direcao = direcao;
        this.conteudo = conteudo;
        this.tpMens = tpMens;
        this.dtEnvio = dtEnvio;
        this.fkConvId = fkConvId;
    }

    public Long getIdMens() { return idMens; }
    public void setIdMens(Long idMens) { this.idMens = idMens; }

    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public String getDirecao() { return direcao; }
    public void setDirecao(String direcao) { this.direcao = direcao; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getTpMens() { return tpMens; }
    public void setTpMens(String tpMens) { this.tpMens = tpMens; }

    public LocalDate getDtEnvio() { return dtEnvio; }
    public void setDtEnvio(LocalDate dtEnvio) { this.dtEnvio = dtEnvio; }

    public Long getFkConvId() { return fkConvId; }
    public void setFkConvId(Long fkConvId) { this.fkConvId = fkConvId; }

    @Override
    public String toString() {
        return "Mensagem{idMens=" + idMens + "\nfkConvId=" + fkConvId
                + "\nenviadoPor=" + enviadoPor + "\ndirecao=" + direcao
                + "\ntpMens=" + tpMens + "}";
    }
}
