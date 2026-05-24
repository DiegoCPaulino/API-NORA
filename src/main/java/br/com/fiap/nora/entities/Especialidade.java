package br.com.fiap.nora.entities;

public class Especialidade {

    private Long idEspec;
    private String nmEspec;
    private String dsEspec;

    public Especialidade() {}

    public Especialidade(Long idEspec, String nmEspec, String dsEspec) {
        this.idEspec = idEspec;
        this.nmEspec = nmEspec;
        this.dsEspec = dsEspec;
    }

    public Long getIdEspec() { return idEspec; }
    public void setIdEspec(Long idEspec) { this.idEspec = idEspec; }

    public String getNmEspec() { return nmEspec; }
    public void setNmEspec(String nmEspec) { this.nmEspec = nmEspec; }

    public String getDsEspec() { return dsEspec; }
    public void setDsEspec(String dsEspec) { this.dsEspec = dsEspec; }

    @Override
    public String toString() {
        return "Especialidade{idEspec=" + idEspec + "\nnmEspec=" + nmEspec + "}";
    }
}
