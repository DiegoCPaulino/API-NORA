package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.EncaminhamentoBO;
import br.com.fiap.nora.bo.TriagemBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.dto.response.EncaminhamentoResponseDTO;
import br.com.fiap.nora.dto.response.TriagemResponseDTO;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Triagem;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import br.com.fiap.nora.services.AprovacaoTriagemService;
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

@Path("/triagens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TriagemResource {

    private TriagemBO triagemBO = new TriagemBO();
    private AprovacaoTriagemService aprovacaoService = new AprovacaoTriagemService();
    private EncaminhamentoBO encaminhamentoBO = new EncaminhamentoBO();

    @GET
    public Response listar() {
        try {
            List<TriagemResponseDTO> lista = triagemBO.listar();
            return Response.ok(lista).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar triagens.")).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") long id) {
        try {
            TriagemResponseDTO dto = triagemBO.buscarPorId(id);
            if (dto == null) {
                return Response.status(404).entity(new ErroResponse("Triagem nao encontrada.")).build();
            }
            return Response.ok(dto).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao buscar triagem.")).build();
        }
    }

    @POST
    public Response criar(Triagem triagem, @Context UriInfo uriInfo) {
        try {
            TriagemResponseDTO dto = triagemBO.criar(triagem);
            return Response.created(
                    uriInfo.getAbsolutePathBuilder().path(String.valueOf(dto.getId())).build()
            ).entity(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (RegraNegocioException e) {
            return Response.status(422).entity(new ErroResponse(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao criar triagem.")).build();
        }
    }

    @POST
    @Path("/{id}/aprovar")
    public Response aprovar(@PathParam("id") long id) {
        try {
            Encaminhamento encam = aprovacaoService.aprovar(id);
            EncaminhamentoResponseDTO dto = encaminhamentoBO.buscarPorId(encam.getIdEncam());
            return Response.status(201).entity(dto).build();
        } catch (RegraNegocioException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.startsWith(AprovacaoTriagemService.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            } else if (msg.startsWith(AprovacaoTriagemService.PREFIXO_JA_PROCESSADA)) {
                return Response.status(409).entity(new ErroResponse(msg)).build();
            } else if (msg.startsWith(AprovacaoTriagemService.PREFIXO_SEM_DENTISTA)) {
                return Response.status(422).entity(new ErroResponse(msg)).build();
            }
            return Response.status(400).entity(new ErroResponse(msg)).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Falha ao aprovar triagem.")).build();
        }
    }

    // PUT atualiza somente stts_triag e decisao — nenhum outro campo do body e processado
    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") long id, Triagem triagem) {
        try {
            TriagemResponseDTO dto = triagemBO.atualizarStatus(id, triagem.getSttsTriag(), triagem.getDecisao());
            if (dto == null) {
                return Response.status(404).entity(new ErroResponse("Triagem nao encontrada.")).build();
            }
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao atualizar triagem.")).build();
        }
    }
}
