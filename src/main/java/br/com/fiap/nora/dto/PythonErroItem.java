package br.com.fiap.nora.dto;

public class PythonErroItem {

    private String campo;
    private String erro;

    public PythonErroItem() {}

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }
}
