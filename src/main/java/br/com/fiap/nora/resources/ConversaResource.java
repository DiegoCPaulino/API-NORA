package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.ConversaBO;
import br.com.fiap.nora.bo.MensagemBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.dto.response.ConversaResponseDTO;
import br.com.fiap.nora.dto.response.MensagemDTO;
import br.com.fiap.nora.entities.Conversa;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

@Path("/conversas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConversaResource {

    private ConversaBO bo = new ConversaBO();
    private MensagemBO mensagemBO = new MensagemBO();

    @GET
    public Response listar(@QueryParam("contexto") String contexto, @QueryParam("stts") String stts) {
        try {
            List<ConversaResponseDTO> lista = bo.listar(contexto, stts);
            return Response.ok(lista).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar conversas.")).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") long id) {
        try {
            ConversaResponseDTO dto = bo.buscarPorId(id);
            return Response.ok(dto).build();
        } catch (RegraNegocioException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.startsWith(ConversaBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            }
            return Response.status(400).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao buscar conversa.")).build();
        }
    }

    @POST
    public Response criar(Conversa conversa, @Context UriInfo uriInfo) {
        try {
            ConversaResponseDTO dto = bo.criar(conversa);
            // Se o id ja existia (upsert), retorna 200; se e nova, retorna 201 com Location.
            if (conversa.getIdConv() != null && conversa.getIdConv().equals(dto.getId())) {
                return Response.created(
                        uriInfo.getAbsolutePathBuilder().path(String.valueOf(dto.getId())).build()
                ).entity(dto).build();
            }
            return Response.ok(dto).build();
        } catch (RegraNegocioException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.startsWith(ConversaBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            } else if (msg.startsWith(ConversaBO.PREFIXO_REGRA)) {
                return Response.status(422).entity(new ErroResponse(msg)).build();
            }
            return Response.status(400).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao criar conversa.")).build();
        }
    }

    @GET
    @Path("/{id}/mensagens")
    public Response listarMensagens(@PathParam("id") long id) {
        try {
            List<MensagemDTO> mensagens = mensagemBO.listarPorConversa(id);
            return Response.ok(mensagens).build();
        } catch (RegraNegocioException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.startsWith(MensagemBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            }
            return Response.status(400).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar mensagens da conversa.")).build();
        }
    }
}
