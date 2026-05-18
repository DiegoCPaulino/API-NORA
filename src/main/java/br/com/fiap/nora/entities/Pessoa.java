package br.com.fiap.nora.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pessoa {

    private long idPessoa;
    private long idEndereco;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataNascimento;
    private int idade;
    private String sexo;
    private String email;
    private String telefone;
    private String tgChatId;
    private String canalOrig;
    private String sttsPess;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Pessoa() {}

    public Pessoa(long idPessoa, long idEndereco, String nomeCompleto, String cpf,
                  LocalDate dataNascimento, int idade, String sexo, String email, String telefone,
                  String tgChatId, String canalOrig, String sttsPess,
                  LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.idPessoa = idPessoa;
        this.idEndereco = idEndereco;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.idade = idade;
        this.sexo = sexo;
        this.email = email;
        this.telefone = telefone;
        this.tgChatId = tgChatId;
        this.canalOrig = canalOrig;
        this.sttsPess = sttsPess;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public long getIdPessoa() { return idPessoa; }
    public void setIdPessoa(long idPessoa) { this.idPessoa = idPessoa; }

    public long getIdEndereco() { return idEndereco; }
    public void setIdEndereco(long idEndereco) { this.idEndereco = idEndereco; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getTgChatId() { return tgChatId; }
    public void setTgChatId(String tgChatId) { this.tgChatId = tgChatId; }

    public String getCanalOrig() { return canalOrig; }
    public void setCanalOrig(String canalOrig) { this.canalOrig = canalOrig; }

    public String getSttsPess() { return sttsPess; }
    public void setSttsPess(String sttsPess) { this.sttsPess = sttsPess; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return "Pessoa{idPessoa=" + idPessoa + "\nnomeCompleto=" + nomeCompleto
                + "\nidade=" + idade + "\nsttsPess=" + sttsPess + "}";
    }
}
