package br.com.fiap.nora.entities;

import java.time.LocalDate;

public class Pessoa {

    private Long idPess;
    private String nmPess;
    private String cpfPess;
    private String rgPess;
    private String telPess;
    private String emailPess;
    private LocalDate dtNasc;
    private String tgChatId;
    private String canalOrig;
    private LocalDate dtCad;
    private String sttsPess;
    private Long fkEndId;

    public Pessoa() {}

    public Pessoa(Long idPess, String nmPess, String cpfPess, String rgPess, String telPess,
                  String emailPess, LocalDate dtNasc, String tgChatId, String canalOrig,
                  LocalDate dtCad, String sttsPess, Long fkEndId) {
        this.idPess = idPess;
        this.nmPess = nmPess;
        this.cpfPess = cpfPess;
        this.rgPess = rgPess;
        this.telPess = telPess;
        this.emailPess = emailPess;
        this.dtNasc = dtNasc;
        this.tgChatId = tgChatId;
        this.canalOrig = canalOrig;
        this.dtCad = dtCad;
        this.sttsPess = sttsPess;
        this.fkEndId = fkEndId;
    }

    public Long getIdPess() { return idPess; }
    public void setIdPess(Long idPess) { this.idPess = idPess; }

    public String getNmPess() { return nmPess; }
    public void setNmPess(String nmPess) { this.nmPess = nmPess; }

    public String getCpfPess() { return cpfPess; }
    public void setCpfPess(String cpfPess) { this.cpfPess = cpfPess; }

    public String getRgPess() { return rgPess; }
    public void setRgPess(String rgPess) { this.rgPess = rgPess; }

    public String getTelPess() { return telPess; }
    public void setTelPess(String telPess) { this.telPess = telPess; }

    public String getEmailPess() { return emailPess; }
    public void setEmailPess(String emailPess) { this.emailPess = emailPess; }

    public LocalDate getDtNasc() { return dtNasc; }
    public void setDtNasc(LocalDate dtNasc) { this.dtNasc = dtNasc; }

    public String getTgChatId() { return tgChatId; }
    public void setTgChatId(String tgChatId) { this.tgChatId = tgChatId; }

    public String getCanalOrig() { return canalOrig; }
    public void setCanalOrig(String canalOrig) { this.canalOrig = canalOrig; }

    public LocalDate getDtCad() { return dtCad; }
    public void setDtCad(LocalDate dtCad) { this.dtCad = dtCad; }

    public String getSttsPess() { return sttsPess; }
    public void setSttsPess(String sttsPess) { this.sttsPess = sttsPess; }

    public Long getFkEndId() { return fkEndId; }
    public void setFkEndId(Long fkEndId) { this.fkEndId = fkEndId; }

    @Override
    public String toString() {
        return "Pessoa{idPess=" + idPess + "\nnmPess=" + nmPess + "\nsttsPess=" + sttsPess + "}";
    }
}
