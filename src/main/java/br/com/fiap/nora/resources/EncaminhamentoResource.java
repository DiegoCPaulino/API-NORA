package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.EncaminhamentoBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.dto.response.EncaminhamentoResponseDTO;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import jakarta.ws.rs.Consumes;
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

@Path("/encaminhamentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EncaminhamentoResource {

    private EncaminhamentoBO bo = new EncaminhamentoBO();

    @GET
    public Response listar() {
        try {
            List<EncaminhamentoResponseDTO> lista = bo.listarTodos();
            return Response.ok(lista).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar encaminhamentos.")).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") long id) {
        try {
            EncaminhamentoResponseDTO dto = bo.buscarPorId(id);
            if (dto == null) {
                return Response.status(404).entity(new ErroResponse("Encaminhamento nao encontrado.")).build();
            }
            return Response.ok(dto).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao buscar encaminhamento.")).build();
        }
    }

    @POST
    public Response criar(Encaminhamento encaminhamento, @Context UriInfo uriInfo) {
        try {
            EncaminhamentoResponseDTO dto = bo.criar(encaminhamento);
            return Response.created(
                    uriInfo.getAbsolutePathBuilder().path(String.valueOf(dto.getId())).build()
            ).entity(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (RegraNegocioException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.startsWith(EncaminhamentoBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            }
            return Response.status(422).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao criar encaminhamento.")).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") long id, Encaminhamento encaminhamento) {
        try {
            EncaminhamentoResponseDTO dto = bo.atualizar(id, encaminhamento.getSttsEncam(), encaminhamento.getObsEncam());
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (RegraNegocioException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.startsWith(EncaminhamentoBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            }
            return Response.status(422).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao atualizar encaminhamento.")).build();
        }
    }
}
