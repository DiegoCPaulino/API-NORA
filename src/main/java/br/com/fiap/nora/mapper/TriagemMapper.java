package br.com.fiap.nora.mapper;

import br.com.fiap.nora.dto.response.TriagemResponseDTO;
import br.com.fiap.nora.entities.Triagem;

public class TriagemMapper {

    private TriagemMapper() {}

    public static TriagemResponseDTO toResponse(Triagem t) {
        if (t == null) return null;
        TriagemResponseDTO dto = new TriagemResponseDTO();
        dto.setId(t.getIdTriag());
        dto.setElegibilidade(t.getElegTriag());
        dto.setPrioridade(t.getPriorTriag());
        dto.setSexo(t.getSexoPess());
        dto.setProblemaBucal(t.getProblemaBucal());
        dto.setRendaFamiliar(t.getRendaFamiliar());
        dto.setNivelUrgenciaIA(t.getNivelUrgIa());
        dto.setConfiancaIA(t.getConfIa());
        dto.setSugestaoIA(t.getSugestaoIa());
        dto.setObservacao(t.getObsTriag());
        dto.setDataTriagem(MapperUtils.formatarData(t.getDtTriag()));
        dto.setStatus(t.getSttsTriag());
        dto.setDecisao(t.getDecisao());
        dto.setPessoaId(t.getFkPessId());
        return dto;
    }
}
