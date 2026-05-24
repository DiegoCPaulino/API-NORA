package br.com.fiap.nora.resources;

import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.services.FollowUpService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// Endpoint manual para disparar follow-up sem esperar o cron das 9h.
// @Inject é necessário pois FollowUpService é @ApplicationScoped (requisito do @Scheduled).
// Protegido automaticamente pelo AuthFilter (Bearer token).
@Path("/follow-up")
@Produces(MediaType.APPLICATION_JSON)
public class FollowUpResource {

    @Inject
    FollowUpService followUpService;

    @POST
    @Path("/executar")
    public Response executar() {
        try {
            followUpService.executar();
            return Response.ok("{\"mensagem\":\"Follow-up executado.\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao executar follow-up.")).build();
        }
    }
}
