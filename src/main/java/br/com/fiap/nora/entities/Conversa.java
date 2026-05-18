package br.com.fiap.nora.entities;

import java.time.LocalDateTime;

// ATENÇÃO: apenas um dos três FKs pode estar preenchido por vez — constraint CHK_CONV_CTX_FK no Oracle (CLAUDE.md §10)
public class Conversa {

    private long idConversa;
    private String canalConv;
    private String contexto;
    private String tgThreadId;
    private Long idPessoa;
    private Long idPaciente;
    private Long idDentista;
    private String sttsConv;
    private int naoLidas;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Conversa() {}

    public Conversa(long idConversa, String canalConv, String contexto, String tgThreadId,
                    Long idPessoa, Long idPaciente, Long idDentista, String sttsConv,
                    int naoLidas, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.idConversa = idConversa;
        this.canalConv = canalConv;
        this.contexto = contexto;
        this.tgThreadId = tgThreadId;
        this.idPessoa = idPessoa;
        this.idPaciente = idPaciente;
        this.idDentista = idDentista;
        this.sttsConv = sttsConv;
        this.naoLidas = naoLidas;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public long getIdConversa() { return idConversa; }
    public void setIdConversa(long idConversa) { this.idConversa = idConversa; }

    public String getCanalConv() { return canalConv; }
    public void setCanalConv(String canalConv) { this.canalConv = canalConv; }

    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }

    public String getTgThreadId() { return tgThreadId; }
    public void setTgThreadId(String tgThreadId) { this.tgThreadId = tgThreadId; }

    public Long getIdPessoa() { return idPessoa; }
    public void setIdPessoa(Long idPessoa) { this.idPessoa = idPessoa; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public Long getIdDentista() { return idDentista; }
    public void setIdDentista(Long idDentista) { this.idDentista = idDentista; }

    public String getSttsConv() { return sttsConv; }
    public void setSttsConv(String sttsConv) { this.sttsConv = sttsConv; }

    public int getNaoLidas() { return naoLidas; }
    public void setNaoLidas(int naoLidas) { this.naoLidas = naoLidas; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return "Conversa{idConversa=" + idConversa + "\ncontexto=" + contexto
                + "\ncanalConv=" + canalConv + "\nsttsConv=" + sttsConv + "}";
    }
}
