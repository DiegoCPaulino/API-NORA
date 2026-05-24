package br.com.fiap.nora.dto.response;

import java.util.List;

public class DentistaResponseDTO {

    private Long id;
    private String nome;
    private String cro;
    private String telefone;
    private String email;
    private String cep;
    private String bairro;
    private String cidade;
    private String uf;
    private int capMensal;
    private int encaminhamentosAtivos;
    private String status;
    private String dataCredenciamento;
    private List<String> especialidades;
    private List<EncaminhamentoDentistaDTO> encaminhamentos;

    public DentistaResponseDTO() {}

    public DentistaResponseDTO(Long id, String nome, String cro, String telefone, String email,
                               String cep, String bairro, String cidade, String uf, int capMensal,
                               int encaminhamentosAtivos, String status, String dataCredenciamento,
                               List<String> especialidades, List<EncaminhamentoDentistaDTO> encaminhamentos) {
        this.id = id;
        this.nome = nome;
        this.cro = cro;
        this.telefone = telefone;
        this.email = email;
        this.cep = cep;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.capMensal = capMensal;
        this.encaminhamentosAtivos = encaminhamentosAtivos;
        this.status = status;
        this.dataCredenciamento = dataCredenciamento;
        this.especialidades = especialidades;
        this.encaminhamentos = encaminhamentos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCro() { return cro; }
    public void setCro(String cro) { this.cro = cro; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public int getCapMensal() { return capMensal; }
    public void setCapMensal(int capMensal) { this.capMensal = capMensal; }

    public int getEncaminhamentosAtivos() { return encaminhamentosAtivos; }
    public void setEncaminhamentosAtivos(int encaminhamentosAtivos) { this.encaminhamentosAtivos = encaminhamentosAtivos; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataCredenciamento() { return dataCredenciamento; }
    public void setDataCredenciamento(String dataCredenciamento) { this.dataCredenciamento = dataCredenciamento; }

    public List<String> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<String> especialidades) { this.especialidades = especialidades; }

    public List<EncaminhamentoDentistaDTO> getEncaminhamentos() { return encaminhamentos; }
    public void setEncaminhamentos(List<EncaminhamentoDentistaDTO> encaminhamentos) { this.encaminhamentos = encaminhamentos; }
}
