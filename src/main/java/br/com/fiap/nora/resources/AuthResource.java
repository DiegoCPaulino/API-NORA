package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.AuthBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.dto.LoginRequest;
import br.com.fiap.nora.dto.LoginResponse;
import br.com.fiap.nora.exceptions.RegraNegocioException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private AuthBO authBO = new AuthBO();

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            // Aceita email (com @) ou login (nm_login). email tem prioridade se ambos forem enviados.
            String identificador = (request.getEmail() != null && !request.getEmail().isBlank())
                    ? request.getEmail()
                    : request.getLogin();
            LoginResponse response = authBO.login(identificador, request.getSenha());
            if (response == null) {
                return Response.status(401).entity(new ErroResponse("Credenciais invalidas.")).build();
            }
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (RegraNegocioException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro interno no servidor.")).build();
        }
    }
}
