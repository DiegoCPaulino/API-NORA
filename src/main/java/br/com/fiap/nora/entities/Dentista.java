package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

public class Dentista {

    private long idDentista;
    private long idEndereco;
    private String nome;
    private String cro;
    private String email;
    private String telefone;
    private String sttsDent;
    private int capMensal;
    private int ativos;
    private LocalDateTime dataCadastro;
    private String observacoes;

    public Dentista() {}

    public Dentista(long idDentista, long idEndereco, String nome, String cro, String email,
                    String telefone, String sttsDent, int capMensal, int ativos,
                    LocalDateTime dataCadastro, String observacoes) {
        this.idDentista = idDentista;
        this.idEndereco = idEndereco;
        this.nome = nome;
        this.cro = cro;
        this.email = email;
        this.telefone = telefone;
        this.sttsDent = sttsDent;
        this.capMensal = capMensal;
        this.ativos = ativos;
        this.dataCadastro = dataCadastro;
        this.observacoes = observacoes;
    }

    public long getIdDentista() { return idDentista; }
    public void setIdDentista(long idDentista) { this.idDentista = idDentista; }

    public long getIdEndereco() { return idEndereco; }
    public void setIdEndereco(long idEndereco) { this.idEndereco = idEndereco; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCro() { return cro; }
    public void setCro(String cro) { this.cro = cro; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSttsDent() { return sttsDent; }
    public void setSttsDent(String sttsDent) { this.sttsDent = sttsDent; }

    public int getCapMensal() { return capMensal; }
    public void setCapMensal(int capMensal) { this.capMensal = capMensal; }

    public int getAtivos() { return ativos; }
    public void setAtivos(int ativos) { this.ativos = ativos; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    // Disponível quando há vagas no mês (ativos < capMensal) e o dentista está ativo.
    public boolean verificarDisponibilidade(int ativos) {
        return ativos < this.capMensal && "ativo".equals(this.sttsDent);
    }

    @Override
    public String toString() {
        return "Dentista{idDentista=" + idDentista + "\nnome=" + nome + "\ncro=" + cro
                + "\nsttsDent=" + sttsDent + "\ncapMensal=" + capMensal + "\nativos=" + ativos + "}";
    }
}
