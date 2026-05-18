package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class Colaborador {

    private long idColaborador;
    private String nome;
    private String email;
    private String senhaHash;
    private String perfil;
    private String statusColab;
    private LocalDateTime dataCriacao;

    public Colaborador() {}

    public Colaborador(long idColaborador, String nome, String email, String senhaHash,
                       String perfil, String statusColab, LocalDateTime dataCriacao) {
        this.idColaborador = idColaborador;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.statusColab = statusColab;
        this.dataCriacao = dataCriacao;
    }

    public long getIdColaborador() { return idColaborador; }
    public void setIdColaborador(long idColaborador) { this.idColaborador = idColaborador; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getStatusColab() { return statusColab; }
    public void setStatusColab(String statusColab) { this.statusColab = statusColab; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    @Override
    public String toString() {
        return "Colaborador{idColaborador=" + idColaborador + "\nnome=" + nome
                + "\nemail=" + email + "\nperfil=" + perfil + "\nstatusColab=" + statusColab + "}";
    }
}
