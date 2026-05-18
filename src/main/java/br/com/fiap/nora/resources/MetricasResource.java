package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.MetricasBO;
import br.com.fiap.nora.dto.ErroResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// Sem @Consumes — todos os endpoints são GET puros
// Sem @QueryParam filtros — métricas retornam consolidado total (fora do escopo)
@Path("/metricas")
@Produces(MediaType.APPLICATION_JSON)
public class MetricasResource {

    private MetricasBO bo = new MetricasBO();

    @GET
    @Path("/resumo")
    public Response resumo() {
        try {
            return Response.ok(bo.resumo()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Falha ao consultar metricas.")).build();
        }
    }

    @GET
    @Path("/triagens-por-status")
    public Response triagensPorStatus() {
        try {
            return Response.ok(bo.triagensPorStatus()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Falha ao consultar metricas.")).build();
        }
    }

    @GET
    @Path("/encaminhamentos-por-prioridade")
    public Response encaminhamentosPorPrioridade() {
        try {
            return Response.ok(bo.encaminhamentosPorPrioridade()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Falha ao consultar metricas.")).build();
        }
    }

    @GET
    @Path("/leads-por-canal")
    public Response leadsPorCanal() {
        try {
            return Response.ok(bo.leadsPorCanal()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Falha ao consultar metricas.")).build();
        }
    }

    @GET
    @Path("/leads-por-mes")
    public Response leadsPorMes() {
        try {
            return Response.ok(bo.leadsPorMes()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Falha ao consultar metricas.")).build();
        }
    }

    @GET
    @Path("/regioes")
    public Response regioes() {
        try {
            return Response.ok(bo.regioes()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Falha ao consultar metricas.")).build();
        }
    }
}
