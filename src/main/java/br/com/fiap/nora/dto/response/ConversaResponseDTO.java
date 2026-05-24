package br.com.fiap.nora.dto.response;

import java.util.List;

public class ConversaResponseDTO {

    private Long id;
    private String camada;
    private String canal;
    private String status;
    private String ultimaMensagem;
    private String ultimoHorario;
    private int naoLidas;
    private List<MensagemDTO> mensagens;
    private DadosPacienteDTO dadosPaciente;
    private DadosDentistaDTO dadosDentista;

    public ConversaResponseDTO() {}

    public ConversaResponseDTO(Long id, String camada, String canal, String status,
                               String ultimaMensagem, String ultimoHorario, int naoLidas,
                               List<MensagemDTO> mensagens, DadosPacienteDTO dadosPaciente,
                               DadosDentistaDTO dadosDentista) {
        this.id = id;
        this.camada = camada;
        this.canal = canal;
        this.status = status;
        this.ultimaMensagem = ultimaMensagem;
        this.ultimoHorario = ultimoHorario;
        this.naoLidas = naoLidas;
        this.mensagens = mensagens;
        this.dadosPaciente = dadosPaciente;
        this.dadosDentista = dadosDentista;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCamada() { return camada; }
    public void setCamada(String camada) { this.camada = camada; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUltimaMensagem() { return ultimaMensagem; }
    public void setUltimaMensagem(String ultimaMensagem) { this.ultimaMensagem = ultimaMensagem; }

    public String getUltimoHorario() { return ultimoHorario; }
    public void setUltimoHorario(String ultimoHorario) { this.ultimoHorario = ultimoHorario; }

    public int getNaoLidas() { return naoLidas; }
    public void setNaoLidas(int naoLidas) { this.naoLidas = naoLidas; }

    public List<MensagemDTO> getMensagens() { return mensagens; }
    public void setMensagens(List<MensagemDTO> mensagens) { this.mensagens = mensagens; }

    public DadosPacienteDTO getDadosPaciente() { return dadosPaciente; }
    public void setDadosPaciente(DadosPacienteDTO dadosPaciente) { this.dadosPaciente = dadosPaciente; }

    public DadosDentistaDTO getDadosDentista() { return dadosDentista; }
    public void setDadosDentista(DadosDentistaDTO dadosDentista) { this.dadosDentista = dadosDentista; }
}
