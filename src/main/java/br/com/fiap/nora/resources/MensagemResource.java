package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.MensagemBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.entities.Mensagem;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/mensagens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MensagemResource {

    private MensagemBO bo = new MensagemBO();

    @POST
    public Response criar(Mensagem mensagem, @Context UriInfo uriInfo) {
        try {
            Mensagem resultado = bo.criar(mensagem);
            return Response.created(
                    uriInfo.getAbsolutePathBuilder().path(String.valueOf(resultado.getIdMensagem())).build()
            ).entity(resultado).build();
        } catch (RegraNegocioException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.startsWith(MensagemBO.PREFIXO_NAO_ENCONTRADA)) {
                return Response.status(404).entity(new ErroResponse(msg)).build();
            } else if (msg.startsWith("REGRA:")) {
                return Response.status(422).entity(new ErroResponse(msg)).build();
            }
            return Response.status(400).entity(new ErroResponse(msg)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao criar mensagem.")).build();
        }
    }
}
