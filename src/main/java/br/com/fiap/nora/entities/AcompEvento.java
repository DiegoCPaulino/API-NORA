package br.com.fiap.nora.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDate;

public class AcompEvento {

    private Long idAcomp;
    @JsonAlias({"tipoEvento", "tipo_evento"})
    private String tpEvento;
    private String dsEvento;
    private String origem;
    private LocalDate dtEvento;
    private String resumoIa;
    @JsonAlias({"tipoMensagem", "tipo_mensagem"})
    private String tpMensagem;
    @JsonAlias({"idEncaminhamento", "encaminhamentoId", "fk_encam_id"})
    private Long fkEncamId;
    @JsonAlias({"idUsuario", "usuarioId", "fk_user_id"})
    private Long fkUserId;

    public AcompEvento() {}

    public AcompEvento(Long idAcomp, String tpEvento, String dsEvento, String origem,
                       LocalDate dtEvento, String resumoIa, String tpMensagem,
                       Long fkEncamId, Long fkUserId) {
        this.idAcomp = idAcomp;
        this.tpEvento = tpEvento;
        this.dsEvento = dsEvento;
        this.origem = origem;
        this.dtEvento = dtEvento;
        this.resumoIa = resumoIa;
        this.tpMensagem = tpMensagem;
        this.fkEncamId = fkEncamId;
        this.fkUserId = fkUserId;
    }

    public Long getIdAcomp() { return idAcomp; }
    public void setIdAcomp(Long idAcomp) { this.idAcomp = idAcomp; }

    public String getTpEvento() { return tpEvento; }
    public void setTpEvento(String tpEvento) { this.tpEvento = tpEvento; }

    public String getDsEvento() { return dsEvento; }
    public void setDsEvento(String dsEvento) { this.dsEvento = dsEvento; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public LocalDate getDtEvento() { return dtEvento; }
    public void setDtEvento(LocalDate dtEvento) { this.dtEvento = dtEvento; }

    public String getResumoIa() { return resumoIa; }
    public void setResumoIa(String resumoIa) { this.resumoIa = resumoIa; }

    public String getTpMensagem() { return tpMensagem; }
    public void setTpMensagem(String tpMensagem) { this.tpMensagem = tpMensagem; }

    public Long getFkEncamId() { return fkEncamId; }
    public void setFkEncamId(Long fkEncamId) { this.fkEncamId = fkEncamId; }

    public Long getFkUserId() { return fkUserId; }
    public void setFkUserId(Long fkUserId) { this.fkUserId = fkUserId; }

    @Override
    public String toString() {
        return "AcompEvento{idAcomp=" + idAcomp + "\nfkEncamId=" + fkEncamId
                + "\ntpEvento=" + tpEvento + "\norigem=" + origem + "}";
    }
}
