package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.AcompEventoBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

// Único Resource hospeda ambos os endpoints: evita criar EncaminhamentoResource só para um GET
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AcompEventoResource {

    private AcompEventoBO bo = new AcompEventoBO();

    @POST
    @Path("/acomp_evento")
    public Response criar(AcompEvento evento) {
        try {
            AcompEvento resultado = bo.criar(evento);
            return Response.status(201).entity(resultado).build();
        } catch (RegraNegocioException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.startsWith(AcompEventoBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            } else if (msg.startsWith(AcompEventoBO.PREFIXO_REGRA)) {
                return Response.status(422).entity(new ErroResponse(msg)).build();
            }
            return Response.status(400).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao criar evento de acompanhamento.")).build();
        }
    }

    @GET
    @Path("/encaminhamentos/{id}/eventos")
    public Response listarPorEncaminhamento(@PathParam("id") long id) {
        try {
            List<AcompEvento> eventos = bo.listarPorEncaminhamento(id);
            return Response.ok(eventos).build();
        } catch (RegraNegocioException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.startsWith(AcompEventoBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            }
            return Response.status(400).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar eventos do encaminhamento.")).build();
        }
    }
}
