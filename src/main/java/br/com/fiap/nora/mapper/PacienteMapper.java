package br.com.fiap.nora.mapper;

import br.com.fiap.nora.dto.response.EncaminhamentoResumoDTO;
import br.com.fiap.nora.dto.response.PacienteResponseDTO;
import br.com.fiap.nora.dto.response.TriagemResumoDTO;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Endereco;
import br.com.fiap.nora.entities.Pessoa;
import br.com.fiap.nora.entities.Triagem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacienteMapper {

    private PacienteMapper() {}

    public static PacienteResponseDTO toResponse(Pessoa pessoa, Endereco endereco,
                                                  List<Triagem> triagens,
                                                  List<Encaminhamento> encaminhamentos,
                                                  Map<Long, String> nomesDentistas) {
        Triagem ultima = ultimaTriagem(triagens);

        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(pessoa.getIdPess());
        dto.setNome(pessoa.getNmPess());
        dto.setCpf(pessoa.getCpfPess());
        dto.setTelefone(pessoa.getTelPess());
        dto.setEmail(pessoa.getEmailPess());
        dto.setDataNascimento(MapperUtils.formatarData(pessoa.getDtNasc()));
        dto.setIdade(MapperUtils.calcularIdade(pessoa.getDtNasc()));
        dto.setCanalOrigem(pessoa.getCanalOrig());
        dto.setDataCadastro(MapperUtils.formatarData(pessoa.getDtCad()));
        dto.setStatus(pessoa.getSttsPess());

        if (endereco != null) {
            dto.setCep(endereco.getCep());
            dto.setBairro(endereco.getBairro());
            dto.setCidade(endereco.getCidade());
            dto.setUf(endereco.getUf());
        }

        if (ultima != null) {
            dto.setSexo(ultima.getSexoPess());
            dto.setProblemaBucal(ultima.getProblemaBucal());
            dto.setRendaFamiliar(ultima.getRendaFamiliar());
            dto.setNivelUrgenciaIA(ultima.getNivelUrgIa());
            dto.setConfIA(ultima.getConfIa());
        }

        List<TriagemResumoDTO> listaTriagens = new ArrayList<>();
        if (triagens != null) {
            for (Triagem t : triagens) {
                listaTriagens.add(toResumoTriagem(t));
            }
        }
        dto.setTriagens(listaTriagens);

        List<EncaminhamentoResumoDTO> listaEncam = new ArrayList<>();
        if (encaminhamentos != null) {
            for (Encaminhamento e : encaminhamentos) {
                String dentistaNome = nomesDentistas != null ? nomesDentistas.get(e.getFkDentId()) : null;
                listaEncam.add(toResumoEncaminhamento(e, dentistaNome));
            }
        }
        dto.setEncaminhamentos(listaEncam);

        return dto;
    }

    public static TriagemResumoDTO toResumoTriagem(Triagem t) {
        TriagemResumoDTO dto = new TriagemResumoDTO();
        dto.setId(t.getIdTriag());
        dto.setDataTriagem(MapperUtils.formatarData(t.getDtTriag()));
        dto.setElegibilidade(t.getElegTriag());
        dto.setPrioridade(t.getPriorTriag());
        dto.setStatusTriagem(t.getSttsTriag());
        dto.setNivelUrgenciaIA(t.getNivelUrgIa());
        dto.setProblemaBucal(t.getProblemaBucal());
        dto.setRendaFamiliar(t.getRendaFamiliar());
        return dto;
    }

    public static EncaminhamentoResumoDTO toResumoEncaminhamento(Encaminhamento e, String dentistaNome) {
        EncaminhamentoResumoDTO dto = new EncaminhamentoResumoDTO();
        dto.setId(e.getIdEncam());
        dto.setDataEncaminhamento(MapperUtils.formatarData(e.getDtEncam()));
        dto.setDentistaNome(dentistaNome);
        dto.setPrioridade(e.getPriorEncam());
        dto.setStatus(e.getSttsEncam());
        return dto;
    }

    // Retorna a triagem com dtTriag mais recente; null se lista vazia ou nula.
    private static Triagem ultimaTriagem(List<Triagem> triagens) {
        if (triagens == null || triagens.isEmpty()) return null;
        Triagem ultima = triagens.get(0);
        for (Triagem t : triagens) {
            if (t.getDtTriag() != null && ultima.getDtTriag() != null
                    && t.getDtTriag().isAfter(ultima.getDtTriag())) {
                ultima = t;
            }
        }
        return ultima;
    }
}
