package br.com.fiap.nora.entities;

public class Endereco {

    private Long idEnd;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    public Endereco() {}

    public Endereco(Long idEnd, String logradouro, String numero, String complemento,
                    String bairro, String cidade, String uf, String cep) {
        this.idEnd = idEnd;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }

    public Long getIdEnd() { return idEnd; }
    public void setIdEnd(Long idEnd) { this.idEnd = idEnd; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    @Override
    public String toString() {
        return "Endereco{idEnd=" + idEnd + "\ncep=" + cep + "\nlogradouro=" + logradouro
                + "\nbairro=" + bairro + "\ncidade=" + cidade + "\nuf=" + uf + "}";
    }
}
