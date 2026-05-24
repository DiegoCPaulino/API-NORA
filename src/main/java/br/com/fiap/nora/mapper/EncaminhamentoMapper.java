package br.com.fiap.nora.mapper;

import br.com.fiap.nora.dto.response.DentistaEmbutidoDTO;
import br.com.fiap.nora.dto.response.EncaminhamentoResponseDTO;
import br.com.fiap.nora.dto.response.FollowUpDTO;
import br.com.fiap.nora.dto.response.PacienteEmbutidoDTO;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Endereco;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.entities.Triagem;

import java.util.ArrayList;
import java.util.List;

public class EncaminhamentoMapper {

    private EncaminhamentoMapper() {}

    public static EncaminhamentoResponseDTO toResponse(Encaminhamento enc,
                                                        PacienteEmbutidoDTO paciente,
                                                        DentistaEmbutidoDTO dentista,
                                                        List<AcompEvento> eventos) {
        EncaminhamentoResponseDTO dto = new EncaminhamentoResponseDTO();
        dto.setId(enc.getIdEncam());
        dto.setPaciente(paciente);
        dto.setDentista(dentista);
        dto.setPrioridade(enc.getPriorEncam());
        dto.setStatus(enc.getSttsEncam());
        dto.setDataEncaminhamento(MapperUtils.formatarData(enc.getDtEncam()));
        dto.setPrevisaoFollowUp(MapperUtils.formatarData(enc.getPrevFollow()));
        dto.setObservacao(enc.getObsEncam());
        dto.setMatchAutomatico(MapperUtils.simNaoParaBoolean(enc.getMatchAuto()));

        List<FollowUpDTO> followUps = new ArrayList<>();
        if (eventos != null) {
            for (AcompEvento ev : eventos) {
                followUps.add(toFollowUp(ev));
            }
        }
        dto.setFollowUps(followUps);

        return dto;
    }

    public static FollowUpDTO toFollowUp(AcompEvento ev) {
        FollowUpDTO dto = new FollowUpDTO();
        dto.setId(ev.getIdAcomp());
        dto.setTipoEvento(ev.getTpEvento());
        dto.setDescricao(ev.getDsEvento());
        dto.setOrigem(ev.getOrigem());
        dto.setDataEvento(MapperUtils.formatarData(ev.getDtEvento()));
        return dto;
    }

    // idPac é o ID da tabela paciente (distinto de pessoa.idPess).
    public static PacienteEmbutidoDTO toPacienteEmbutido(Long idPac, Pessoa pessoa,
                                                          Endereco endereco, Triagem ultimaTriagem) {
        PacienteEmbutidoDTO dto = new PacienteEmbutidoDTO();
        dto.setId(idPac);
        dto.setNome(pessoa.getNmPess());
        dto.setIdade(MapperUtils.calcularIdade(pessoa.getDtNasc()));
        dto.setBairro(endereco != null ? endereco.getBairro() : null);

        if (ultimaTriagem != null) {
            dto.setProblemaBucal(ultimaTriagem.getProblemaBucal());
            dto.setNivelUrgenciaIA(ultimaTriagem.getNivelUrgIa());
        }

        return dto;
    }
}
