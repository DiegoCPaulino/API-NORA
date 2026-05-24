package br.com.fiap.nora.dto.request;

public class PessoaRequest {

    // Dados da pessoa
    private String nome;
    private String cpf;
    private String rg;
    private String telefone;
    private String email;
    private String dataNascimento; // DD/MM/AAAA — convertido para LocalDate no BO
    private String tgChatId;
    private String canalOrigem;
    // sexo: ignorado no POST de pessoa — não existe coluna em pessoa, fica em triagem
    private String sexo;

    // Dados do endereço — preencher idEndereco OU os campos abaixo (nunca os dois)
    private Long idEndereco;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;

    public PessoaRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTgChatId() { return tgChatId; }
    public void setTgChatId(String tgChatId) { this.tgChatId = tgChatId; }

    public String getCanalOrigem() { return canalOrigem; }
    public void setCanalOrigem(String canalOrigem) { this.canalOrigem = canalOrigem; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Long getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Long idEndereco) { this.idEndereco = idEndereco; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

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
}
