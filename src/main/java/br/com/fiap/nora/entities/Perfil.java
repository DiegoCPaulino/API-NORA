package br.com.fiap.nora.entities;

public class Perfil {

    private Long idPerfil;
    private String nmPerfil;
    private String dsPerfil;

    public Perfil() {}

    public Perfil(Long idPerfil, String nmPerfil, String dsPerfil) {
        this.idPerfil = idPerfil;
        this.nmPerfil = nmPerfil;
        this.dsPerfil = dsPerfil;
    }

    public Long getIdPerfil() { return idPerfil; }
    public void setIdPerfil(Long idPerfil) { this.idPerfil = idPerfil; }

    public String getNmPerfil() { return nmPerfil; }
    public void setNmPerfil(String nmPerfil) { this.nmPerfil = nmPerfil; }

    public String getDsPerfil() { return dsPerfil; }
    public void setDsPerfil(String dsPerfil) { this.dsPerfil = dsPerfil; }

    @Override
    public String toString() {
        return "Perfil{idPerfil=" + idPerfil + "\nnmPerfil=" + nmPerfil + "}";
    }
}
