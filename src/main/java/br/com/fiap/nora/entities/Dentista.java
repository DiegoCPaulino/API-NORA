package br.com.fiap.nora.entities;

import java.time.LocalDate;

public class Dentista {

    private Long idDent;
    private String nmDent;
    private String croDent;
    private String telDent;
    private String emailDent;
    private String tgChatId;
    private int capMensal;
    private String sttsDent;
    private LocalDate dtCred;
    private Long fkEndId;

    public Dentista() {}

    public Dentista(Long idDent, String nmDent, String croDent, String telDent, String emailDent,
                    String tgChatId, int capMensal, String sttsDent, LocalDate dtCred, Long fkEndId) {
        this.idDent = idDent;
        this.nmDent = nmDent;
        this.croDent = croDent;
        this.telDent = telDent;
        this.emailDent = emailDent;
        this.tgChatId = tgChatId;
        this.capMensal = capMensal;
        this.sttsDent = sttsDent;
        this.dtCred = dtCred;
        this.fkEndId = fkEndId;
    }

    public Long getIdDent() { return idDent; }
    public void setIdDent(Long idDent) { this.idDent = idDent; }

    public String getNmDent() { return nmDent; }
    public void setNmDent(String nmDent) { this.nmDent = nmDent; }

    public String getCroDent() { return croDent; }
    public void setCroDent(String croDent) { this.croDent = croDent; }

    public String getTelDent() { return telDent; }
    public void setTelDent(String telDent) { this.telDent = telDent; }

    public String getEmailDent() { return emailDent; }
    public void setEmailDent(String emailDent) { this.emailDent = emailDent; }

    public String getTgChatId() { return tgChatId; }
    public void setTgChatId(String tgChatId) { this.tgChatId = tgChatId; }

    public int getCapMensal() { return capMensal; }
    public void setCapMensal(int capMensal) { this.capMensal = capMensal; }

    public String getSttsDent() { return sttsDent; }
    public void setSttsDent(String sttsDent) { this.sttsDent = sttsDent; }

    public LocalDate getDtCred() { return dtCred; }
    public void setDtCred(LocalDate dtCred) { this.dtCred = dtCred; }

    public Long getFkEndId() { return fkEndId; }
    public void setFkEndId(Long fkEndId) { this.fkEndId = fkEndId; }

    // ativos e calculado externamente (COUNT de encaminhamentos ativos) — nao existe coluna ATIVOS na tabela
    public boolean verificarDisponibilidade(int ativos) {
        return ativos < this.capMensal && "ativo".equals(this.sttsDent);
    }

    @Override
    public String toString() {
        return "Dentista{idDent=" + idDent + "\nnmDent=" + nmDent + "\ncroDent=" + croDent
                + "\nsttsDent=" + sttsDent + "\ncapMensal=" + capMensal + "}";
    }
}
