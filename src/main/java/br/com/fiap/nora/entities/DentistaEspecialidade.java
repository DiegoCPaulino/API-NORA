package br.com.fiap.nora.entities;

public class DentistaEspecialidade {

    private Long fkDentId;
    private Long fkEspecId;

    public DentistaEspecialidade() {}

    public DentistaEspecialidade(Long fkDentId, Long fkEspecId) {
        this.fkDentId = fkDentId;
        this.fkEspecId = fkEspecId;
    }

    public Long getFkDentId() { return fkDentId; }
    public void setFkDentId(Long fkDentId) { this.fkDentId = fkDentId; }

    public Long getFkEspecId() { return fkEspecId; }
    public void setFkEspecId(Long fkEspecId) { this.fkEspecId = fkEspecId; }

    @Override
    public String toString() {
        return "DentistaEspecialidade{fkDentId=" + fkDentId + "\nfkEspecId=" + fkEspecId + "}";
    }
}
