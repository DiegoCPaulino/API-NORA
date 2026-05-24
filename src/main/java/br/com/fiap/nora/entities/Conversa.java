package br.com.fiap.nora.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDate;

// Apenas uma FK contextual pode estar preenchida por vez — constraint conv_lado_ck no Oracle.
public class Conversa {

    private Long idConv;
    private String canalConv;
    private String contexto;
    private String iniciadoPor;
    private LocalDate dtInicio;
    @JsonAlias({"status", "stts_conv"})
    private String sttsConv;
    private String tgThreadId;
    @JsonAlias({"idPessoa", "pessoaId", "fk_pess_id"})
    private Long fkPessId;
    @JsonAlias({"idPaciente", "pacienteId", "fk_pac_id"})
    private Long fkPacId;
    @JsonAlias({"idDentista", "dentistaId", "fk_dent_id"})
    private Long fkDentId;
    @JsonAlias({"idUsuario", "usuarioId", "fk_user_id"})
    private Long fkUserId;

    public Conversa() {}

    public Conversa(Long idConv, String canalConv, String contexto, String iniciadoPor,
                    LocalDate dtInicio, String sttsConv, String tgThreadId,
                    Long fkPessId, Long fkPacId, Long fkDentId, Long fkUserId) {
        this.idConv = idConv;
        this.canalConv = canalConv;
        this.contexto = contexto;
        this.iniciadoPor = iniciadoPor;
        this.dtInicio = dtInicio;
        this.sttsConv = sttsConv;
        this.tgThreadId = tgThreadId;
        this.fkPessId = fkPessId;
        this.fkPacId = fkPacId;
        this.fkDentId = fkDentId;
        this.fkUserId = fkUserId;
    }

    public Long getIdConv() { return idConv; }
    public void setIdConv(Long idConv) { this.idConv = idConv; }

    public String getCanalConv() { return canalConv; }
    public void setCanalConv(String canalConv) { this.canalConv = canalConv; }

    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }

    public String getIniciadoPor() { return iniciadoPor; }
    public void setIniciadoPor(String iniciadoPor) { this.iniciadoPor = iniciadoPor; }

    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }

    public String getSttsConv() { return sttsConv; }
    public void setSttsConv(String sttsConv) { this.sttsConv = sttsConv; }

    public String getTgThreadId() { return tgThreadId; }
    public void setTgThreadId(String tgThreadId) { this.tgThreadId = tgThreadId; }

    public Long getFkPessId() { return fkPessId; }
    public void setFkPessId(Long fkPessId) { this.fkPessId = fkPessId; }

    public Long getFkPacId() { return fkPacId; }
    public void setFkPacId(Long fkPacId) { this.fkPacId = fkPacId; }

    public Long getFkDentId() { return fkDentId; }
    public void setFkDentId(Long fkDentId) { this.fkDentId = fkDentId; }

    public Long getFkUserId() { return fkUserId; }
    public void setFkUserId(Long fkUserId) { this.fkUserId = fkUserId; }

    @Override
    public String toString() {
        return "Conversa{idConv=" + idConv + "\ncontexto=" + contexto
                + "\ncanalConv=" + canalConv + "\nsttsConv=" + sttsConv + "}";
    }
}
