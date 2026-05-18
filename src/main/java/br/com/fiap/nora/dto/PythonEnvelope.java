package br.com.fiap.nora.dto;

import java.util.List;

// Envelope padrao da API Python — CLAUDE.md §12.4
// Necessario porque o Python usa { status, code, message, data, erro } em todas as respostas
public class PythonEnvelope<T> {

    private boolean status;
    private int code;
    private String message;
    private T data;
    private List<PythonErroItem> erro;

    public PythonEnvelope() {}

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public List<PythonErroItem> getErro() { return erro; }
    public void setErro(List<PythonErroItem> erro) { this.erro = erro; }
}
