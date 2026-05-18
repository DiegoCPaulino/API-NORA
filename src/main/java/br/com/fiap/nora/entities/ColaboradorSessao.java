package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class ColaboradorSessao {

    private long idSessao;
    private long idColaborador;
    private String token;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataExpiracao;
    private String statusSessao;

    public ColaboradorSessao() {}

    public ColaboradorSessao(long idSessao, long idColaborador, String token,
                             LocalDateTime dataCriacao, LocalDateTime dataExpiracao,
                             String statusSessao) {
        this.idSessao = idSessao;
        this.idColaborador = idColaborador;
        this.token = token;
        this.dataCriacao = dataCriacao;
        this.dataExpiracao = dataExpiracao;
        this.statusSessao = statusSessao;
    }

    public long getIdSessao() { return idSessao; }
    public void setIdSessao(long idSessao) { this.idSessao = idSessao; }

    public long getIdColaborador() { return idColaborador; }
    public void setIdColaborador(long idColaborador) { this.idColaborador = idColaborador; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) { this.dataExpiracao = dataExpiracao; }

    public String getStatusSessao() { return statusSessao; }
    public void setStatusSessao(String statusSessao) { this.statusSessao = statusSessao; }

    @Override
    public String toString() {
        return "ColaboradorSessao{idSessao=" + idSessao + "\nidColaborador=" + idColaborador
                + "\nstatusSessao=" + statusSessao + "\ndataExpiracao=" + dataExpiracao + "}";
    }
}
