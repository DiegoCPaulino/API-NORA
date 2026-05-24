package br.com.fiap.nora.mapper;

import br.com.fiap.nora.dto.response.DentistaEmbutidoDTO;
import br.com.fiap.nora.dto.response.DentistaResponseDTO;
import br.com.fiap.nora.dto.response.EncaminhamentoDentistaDTO;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Endereco;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DentistaMapper {

    private DentistaMapper() {}

    public static DentistaResponseDTO toResponse(Dentista dentista, Endereco endereco,
                                                  List<String> especialidades,
                                                  int encaminhamentosAtivos,
                                                  List<Encaminhamento> encaminhamentos,
                                                  Map<Long, String> nomesPacientes) {
        DentistaResponseDTO dto = new DentistaResponseDTO();
        dto.setId(dentista.getIdDent());
        dto.setNome(dentista.getNmDent());
        dto.setCro(dentista.getCroDent());
        dto.setTelefone(dentista.getTelDent());
        dto.setEmail(dentista.getEmailDent());
        dto.setCapMensal(dentista.getCapMensal());
        dto.setEncaminhamentosAtivos(encaminhamentosAtivos);
        dto.setStatus(dentista.getSttsDent());
        dto.setDataCredenciamento(MapperUtils.formatarData(dentista.getDtCred()));
        dto.setEspecialidades(especialidades != null ? especialidades : new ArrayList<>());

        if (endereco != null) {
            dto.setCep(endereco.getCep());
            dto.setBairro(endereco.getBairro());
            dto.setCidade(endereco.getCidade());
            dto.setUf(endereco.getUf());
        }

        List<EncaminhamentoDentistaDTO> listaEncam = new ArrayList<>();
        if (encaminhamentos != null) {
            for (Encaminhamento e : encaminhamentos) {
                String pacienteNome = nomesPacientes != null ? nomesPacientes.get(e.getFkPacId()) : null;
                listaEncam.add(toEncaminhamentoDentista(e, pacienteNome));
            }
        }
        dto.setEncaminhamentos(listaEncam);

        return dto;
    }

    public static EncaminhamentoDentistaDTO toEncaminhamentoDentista(Encaminhamento e, String pacienteNome) {
        EncaminhamentoDentistaDTO dto = new EncaminhamentoDentistaDTO();
        dto.setId(e.getIdEncam());
        dto.setDataEncaminhamento(MapperUtils.formatarData(e.getDtEncam()));
        dto.setPacienteNome(pacienteNome);
        dto.setPrioridade(e.getPriorEncam());
        dto.setStatus(e.getSttsEncam());
        return dto;
    }

    public static DentistaEmbutidoDTO toEmbutido(Dentista dentista, Endereco endereco,
                                                  List<String> especialidades, Double distanciaKm) {
        DentistaEmbutidoDTO dto = new DentistaEmbutidoDTO();
        dto.setId(dentista.getIdDent());
        dto.setNome(dentista.getNmDent());
        dto.setCro(dentista.getCroDent());
        dto.setEspecialidades(especialidades != null ? especialidades : new ArrayList<>());
        dto.setDistanciaKm(distanciaKm);

        if (endereco != null) {
            dto.setBairro(endereco.getBairro());
        }

        return dto;
    }
}
