package br.com.fiap.nora.dto;

// Espelha o campo "data" do envelope retornado por /api/triagens/{id}/sugestao-dentista
// Campos em snake_case para que Gson mapeie diretamente sem @SerializedName (padrao academico)
public class MatchResponse {

    private DentistaSugerido dentista_sugerido;
    private Double distancia_km;
    private String metodo_calculo;
    private String criterio_fallback;
    private String observacao;

    public MatchResponse() {}

    public DentistaSugerido getDentista_sugerido() { return dentista_sugerido; }
    public void setDentista_sugerido(DentistaSugerido dentista_sugerido) { this.dentista_sugerido = dentista_sugerido; }

    public Double getDistancia_km() { return distancia_km; }
    public void setDistancia_km(Double distancia_km) { this.distancia_km = distancia_km; }

    public String getMetodo_calculo() { return metodo_calculo; }
    public void setMetodo_calculo(String metodo_calculo) { this.metodo_calculo = metodo_calculo; }

    public String getCriterio_fallback() { return criterio_fallback; }
    public void setCriterio_fallback(String criterio_fallback) { this.criterio_fallback = criterio_fallback; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public static class DentistaSugerido {

        private Integer id_dentista;
        private String nome;
        private String especialidade;
        private String cidade;
        private String uf;
        private String cep;
        private Integer vagas_disponiveis;

        public DentistaSugerido() {}

        public Integer getId_dentista() { return id_dentista; }
        public void setId_dentista(Integer id_dentista) { this.id_dentista = id_dentista; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEspecialidade() { return especialidade; }
        public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

        public String getCidade() { return cidade; }
        public void setCidade(String cidade) { this.cidade = cidade; }

        public String getUf() { return uf; }
        public void setUf(String uf) { this.uf = uf; }

        public String getCep() { return cep; }
        public void setCep(String cep) { this.cep = cep; }

        public Integer getVagas_disponiveis() { return vagas_disponiveis; }
        public void setVagas_disponiveis(Integer vagas_disponiveis) { this.vagas_disponiveis = vagas_disponiveis; }
    }
}
