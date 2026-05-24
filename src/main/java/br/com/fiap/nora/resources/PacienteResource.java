package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.PessoaBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.dto.response.PacienteResponseDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteResource {

    private PessoaBO pessoaBO = new PessoaBO();

    @GET
    public Response listar() {
        try {
            List<PacienteResponseDTO> lista = pessoaBO.listarPacientes();
            return Response.ok(lista).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar pacientes.")).build();
        }
    }

    // {id} = id_pess; retorna 404 se pessoa nao for paciente
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") long id) {
        try {
            PacienteResponseDTO dto = pessoaBO.buscarPaciente(id);
            if (dto == null) {
                return Response.status(404).entity(new ErroResponse("Paciente nao encontrado.")).build();
            }
            return Response.ok(dto).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao buscar paciente.")).build();
        }
    }
}
