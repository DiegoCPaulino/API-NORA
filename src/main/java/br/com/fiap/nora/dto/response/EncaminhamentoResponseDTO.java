package br.com.fiap.nora.dto.response;

import java.util.List;

public class EncaminhamentoResponseDTO {

    private Long id;
    private PacienteEmbutidoDTO paciente;
    private DentistaEmbutidoDTO dentista;
    private String prioridade;
    private String status;
    private String dataEncaminhamento;
    private String previsaoFollowUp;
    private String observacao;
    private boolean matchAutomatico;
    private List<FollowUpDTO> followUps;

    public EncaminhamentoResponseDTO() {}

    public EncaminhamentoResponseDTO(Long id, PacienteEmbutidoDTO paciente, DentistaEmbutidoDTO dentista,
                                     String prioridade, String status, String dataEncaminhamento,
                                     String previsaoFollowUp, String observacao, boolean matchAutomatico,
                                     List<FollowUpDTO> followUps) {
        this.id = id;
        this.paciente = paciente;
        this.dentista = dentista;
        this.prioridade = prioridade;
        this.status = status;
        this.dataEncaminhamento = dataEncaminhamento;
        this.previsaoFollowUp = previsaoFollowUp;
        this.observacao = observacao;
        this.matchAutomatico = matchAutomatico;
        this.followUps = followUps;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PacienteEmbutidoDTO getPaciente() { return paciente; }
    public void setPaciente(PacienteEmbutidoDTO paciente) { this.paciente = paciente; }

    public DentistaEmbutidoDTO getDentista() { return dentista; }
    public void setDentista(DentistaEmbutidoDTO dentista) { this.dentista = dentista; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataEncaminhamento() { return dataEncaminhamento; }
    public void setDataEncaminhamento(String dataEncaminhamento) { this.dataEncaminhamento = dataEncaminhamento; }

    public String getPrevisaoFollowUp() { return previsaoFollowUp; }
    public void setPrevisaoFollowUp(String previsaoFollowUp) { this.previsaoFollowUp = previsaoFollowUp; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public boolean isMatchAutomatico() { return matchAutomatico; }
    public void setMatchAutomatico(boolean matchAutomatico) { this.matchAutomatico = matchAutomatico; }

    public List<FollowUpDTO> getFollowUps() { return followUps; }
    public void setFollowUps(List<FollowUpDTO> followUps) { this.followUps = followUps; }
}
