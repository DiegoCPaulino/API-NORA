package br.com.fiap.nora.entities;

import java.time.LocalDate;

public class Usuario {

    private Long idUser;
    private String nmLogin;
    private String senhaHash;
    private String sttsUser;
    private LocalDate dtCriacao;
    private LocalDate dtAcesso;
    private Long fkColabId;
    private Long fkPerfId;

    public Usuario() {}

    public Usuario(Long idUser, String nmLogin, String senhaHash, String sttsUser,
                   LocalDate dtCriacao, LocalDate dtAcesso, Long fkColabId, Long fkPerfId) {
        this.idUser = idUser;
        this.nmLogin = nmLogin;
        this.senhaHash = senhaHash;
        this.sttsUser = sttsUser;
        this.dtCriacao = dtCriacao;
        this.dtAcesso = dtAcesso;
        this.fkColabId = fkColabId;
        this.fkPerfId = fkPerfId;
    }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public String getNmLogin() { return nmLogin; }
    public void setNmLogin(String nmLogin) { this.nmLogin = nmLogin; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getSttsUser() { return sttsUser; }
    public void setSttsUser(String sttsUser) { this.sttsUser = sttsUser; }

    public LocalDate getDtCriacao() { return dtCriacao; }
    public void setDtCriacao(LocalDate dtCriacao) { this.dtCriacao = dtCriacao; }

    public LocalDate getDtAcesso() { return dtAcesso; }
    public void setDtAcesso(LocalDate dtAcesso) { this.dtAcesso = dtAcesso; }

    public Long getFkColabId() { return fkColabId; }
    public void setFkColabId(Long fkColabId) { this.fkColabId = fkColabId; }

    public Long getFkPerfId() { return fkPerfId; }
    public void setFkPerfId(Long fkPerfId) { this.fkPerfId = fkPerfId; }

    @Override
    public String toString() {
        return "Usuario{idUser=" + idUser + "\nnmLogin=" + nmLogin + "\nsttsUser=" + sttsUser + "}";
    }
}
