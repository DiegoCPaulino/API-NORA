package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.DentistaBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.dto.request.DentistaRequest;
import br.com.fiap.nora.dto.response.DentistaResponseDTO;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

@Path("/dentistas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DentistaResource {

    private DentistaBO dentistaBO = new DentistaBO();

    @GET
    public Response listar() {
        try {
            List<DentistaResponseDTO> lista = dentistaBO.listarDentistas();
            return Response.ok(lista).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar dentistas.")).build();
        }
    }

    @GET
    @Path("/disponiveis")
    public Response listarDisponiveis() {
        try {
            List<DentistaResponseDTO> lista = dentistaBO.listarDisponiveis();
            return Response.ok(lista).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar dentistas disponiveis.")).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") long id) {
        try {
            DentistaResponseDTO dto = dentistaBO.buscarDentista(id);
            if (dto == null) {
                return Response.status(404).entity(new ErroResponse("Dentista nao encontrado.")).build();
            }
            return Response.ok(dto).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao buscar dentista.")).build();
        }
    }

    @POST
    public Response criar(DentistaRequest req, @Context UriInfo uriInfo) {
        try {
            DentistaResponseDTO dto = dentistaBO.criarDentista(req);
            return Response.created(
                    uriInfo.getAbsolutePathBuilder().path(String.valueOf(dto.getId())).build()
            ).entity(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao criar dentista.")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") long id) {
        try {
            dentistaBO.deletarDentista(id);
            return Response.noContent().build();

        } catch (RegraNegocioException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";

            if (msg.startsWith(DentistaBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            }

            if (msg.startsWith(DentistaBO.PREFIXO_REGRA)) {
                return Response.status(409).entity(new ErroResponse(msg)).build();
            }

            return Response.status(400).entity(new ErroResponse(msg)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao excluir dentista.")).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") long id, DentistaRequest req) {
        try {
            DentistaResponseDTO dto = dentistaBO.atualizarDentista(id, req);
            if (dto == null) {
                return Response.status(404).entity(new ErroResponse("Dentista nao encontrado.")).build();
            }
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao atualizar dentista.")).build();
        }
    }
}
