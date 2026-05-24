package br.com.fiap.nora.mapper;

import br.com.fiap.nora.dto.response.ConversaResponseDTO;
import br.com.fiap.nora.dto.response.DadosDentistaDTO;
import br.com.fiap.nora.dto.response.DadosPacienteDTO;
import br.com.fiap.nora.dto.response.MensagemDTO;
import br.com.fiap.nora.entities.Conversa;
import br.com.fiap.nora.entities.Mensagem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConversaMapper {

    private ConversaMapper() {}

    // ultimoHorario vem da dtEnvio da última mensagem (LocalDate — tabela mensagem não tem timestamp).
    public static ConversaResponseDTO toResponse(Conversa conversa,
                                                  String ultimaMensagem,
                                                  LocalDate ultimoHorario,
                                                  List<Mensagem> mensagens,
                                                  DadosPacienteDTO dadosPaciente,
                                                  DadosDentistaDTO dadosDentista) {
        ConversaResponseDTO dto = new ConversaResponseDTO();
        dto.setId(conversa.getIdConv());
        dto.setCamada(MapperUtils.mapearCamada(conversa.getContexto()));
        dto.setCanal(conversa.getCanalConv());
        dto.setStatus(conversa.getSttsConv());
        dto.setUltimaMensagem(ultimaMensagem);
        dto.setUltimoHorario(MapperUtils.formatarData(ultimoHorario));
        // coluna lida não existe no DDL — naoLidas fixo em 0, limitação consciente
        dto.setNaoLidas(0);

        List<MensagemDTO> listaMensagens = new ArrayList<>();
        if (mensagens != null) {
            for (Mensagem m : mensagens) {
                listaMensagens.add(toMensagemDTO(m));
            }
        }
        dto.setMensagens(listaMensagens);

        dto.setDadosPaciente(dadosPaciente);
        dto.setDadosDentista(dadosDentista);

        return dto;
    }

    public static MensagemDTO toMensagemDTO(Mensagem m) {
        MensagemDTO dto = new MensagemDTO();
        dto.setId(m.getIdMens());
        dto.setEnviadoPor(m.getEnviadoPor());
        dto.setDirecao(m.getDirecao());
        dto.setConteudo(m.getConteudo());
        dto.setTipoMensagem(m.getTpMens());
        dto.setDataEnvio(MapperUtils.formatarData(m.getDtEnvio()));
        return dto;
    }
}
