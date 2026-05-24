package br.com.fiap.nora.dto.response;

import java.util.List;

public class PacienteResponseDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String dataNascimento;
    private int idade;
    private String sexo;
    private String cep;
    private String bairro;
    private String cidade;
    private String uf;
    private String canalOrigem;
    private String dataCadastro;
    private String status;
    private String problemaBucal;
    private String rendaFamiliar;
    private Double nivelUrgenciaIA;
    private Double confIA;
    private List<TriagemResumoDTO> triagens;
    private List<EncaminhamentoResumoDTO> encaminhamentos;

    public PacienteResponseDTO() {}

    public PacienteResponseDTO(Long id, String nome, String cpf, String telefone, String email,
                               String dataNascimento, int idade, String sexo, String cep,
                               String bairro, String cidade, String uf, String canalOrigem,
                               String dataCadastro, String status, String problemaBucal,
                               String rendaFamiliar, Double nivelUrgenciaIA, Double confIA,
                               List<TriagemResumoDTO> triagens, List<EncaminhamentoResumoDTO> encaminhamentos) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.idade = idade;
        this.sexo = sexo;
        this.cep = cep;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.canalOrigem = canalOrigem;
        this.dataCadastro = dataCadastro;
        this.status = status;
        this.problemaBucal = problemaBucal;
        this.rendaFamiliar = rendaFamiliar;
        this.nivelUrgenciaIA = nivelUrgenciaIA;
        this.confIA = confIA;
        this.triagens = triagens;
        this.encaminhamentos = encaminhamentos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCanalOrigem() { return canalOrigem; }
    public void setCanalOrigem(String canalOrigem) { this.canalOrigem = canalOrigem; }

    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProblemaBucal() { return problemaBucal; }
    public void setProblemaBucal(String problemaBucal) { this.problemaBucal = problemaBucal; }

    public String getRendaFamiliar() { return rendaFamiliar; }
    public void setRendaFamiliar(String rendaFamiliar) { this.rendaFamiliar = rendaFamiliar; }

    public Double getNivelUrgenciaIA() { return nivelUrgenciaIA; }
    public void setNivelUrgenciaIA(Double nivelUrgenciaIA) { this.nivelUrgenciaIA = nivelUrgenciaIA; }

    public Double getConfIA() { return confIA; }
    public void setConfIA(Double confIA) { this.confIA = confIA; }

    public List<TriagemResumoDTO> getTriagens() { return triagens; }
    public void setTriagens(List<TriagemResumoDTO> triagens) { this.triagens = triagens; }

    public List<EncaminhamentoResumoDTO> getEncaminhamentos() { return encaminhamentos; }
    public void setEncaminhamentos(List<EncaminhamentoResumoDTO> encaminhamentos) { this.encaminhamentos = encaminhamentos; }
}
