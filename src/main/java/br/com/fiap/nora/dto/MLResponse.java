package br.com.fiap.nora.dto;

// Stub — endpoint de predição ML não disponível na API Python atual.
public class MLResponse {

    private Double nivel_urgencia;
    private Double confianca;

    public MLResponse() {}

    public Double getNivel_urgencia() { return nivel_urgencia; }
    public void setNivel_urgencia(Double nivel_urgencia) { this.nivel_urgencia = nivel_urgencia; }

    public Double getConfianca() { return confianca; }
    public void setConfianca(Double confianca) { this.confianca = confianca; }
}
