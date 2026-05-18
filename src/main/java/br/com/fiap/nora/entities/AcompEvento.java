package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class AcompEvento {

    private long idAcompEvento;
    private long idEncaminhamento;
    private String tipoEvento;
    private String dsEvento;
    private String origem;
    private String resumoIa;
    private String tipoMensagem;
    private LocalDateTime dataEvento;

    public AcompEvento() {}

    public AcompEvento(long idAcompEvento, long idEncaminhamento, String tipoEvento,
                       String dsEvento, String origem, String resumoIa,
                       String tipoMensagem, LocalDateTime dataEvento) {
        this.idAcompEvento = idAcompEvento;
        this.idEncaminhamento = idEncaminhamento;
        this.tipoEvento = tipoEvento;
        this.dsEvento = dsEvento;
        this.origem = origem;
        this.resumoIa = resumoIa;
        this.tipoMensagem = tipoMensagem;
        this.dataEvento = dataEvento;
    }

    public long getIdAcompEvento() { return idAcompEvento; }
    public void setIdAcompEvento(long idAcompEvento) { this.idAcompEvento = idAcompEvento; }

    public long getIdEncaminhamento() { return idEncaminhamento; }
    public void setIdEncaminhamento(long idEncaminhamento) { this.idEncaminhamento = idEncaminhamento; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDsEvento() { return dsEvento; }
    public void setDsEvento(String dsEvento) { this.dsEvento = dsEvento; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public String getResumoIa() { return resumoIa; }
    public void setResumoIa(String resumoIa) { this.resumoIa = resumoIa; }

    public String getTipoMensagem() { return tipoMensagem; }
    public void setTipoMensagem(String tipoMensagem) { this.tipoMensagem = tipoMensagem; }

    public LocalDateTime getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDateTime dataEvento) { this.dataEvento = dataEvento; }

    @Override
    public String toString() {
        return "AcompEvento{idAcompEvento=" + idAcompEvento + "\nidEncaminhamento=" + idEncaminhamento
                + "\ntipoEvento=" + tipoEvento + "\norigem=" + origem + "}";
    }
}
