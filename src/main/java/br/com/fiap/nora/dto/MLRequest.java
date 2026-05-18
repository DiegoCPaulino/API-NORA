package br.com.fiap.nora.dto;

public class MLRequest {

    private String sexo;
    private int idade;
    private String problema_bucal;
    private String renda_familiar;

    public MLRequest() {}

    public MLRequest(String sexo, int idade, String problema_bucal, String renda_familiar) {
        this.sexo = sexo;
        this.idade = idade;
        this.problema_bucal = problema_bucal;
        this.renda_familiar = renda_familiar;
    }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public String getProblema_bucal() { return problema_bucal; }
    public void setProblema_bucal(String problema_bucal) { this.problema_bucal = problema_bucal; }

    public String getRenda_familiar() { return renda_familiar; }
    public void setRenda_familiar(String renda_familiar) { this.renda_familiar = renda_familiar; }
}
