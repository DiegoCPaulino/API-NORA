package br.com.fiap.nora.filters;

import br.com.fiap.nora.bo.AuthBO;
import br.com.fiap.nora.dto.ErroResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter {

    private AuthBO authBO = new AuthBO();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Libera preflight CORS antes de qualquer validacao de autenticacao
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;

        // Libera login e endpoints internos do Quarkus sem validacao de token
        if (normalizedPath.startsWith("auth/login") || normalizedPath.startsWith("q/")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(requestContext, "Token ausente ou invalido.");
            return;
        }

        String token = authHeader.substring(7).trim();
        try {
            if (authBO.validarToken(token) == null) {
                abort(requestContext, "Token invalido ou expirado.");
            }
        } catch (Exception e) {
            abort(requestContext, "Erro ao validar token.");
        }
    }

    private void abort(ContainerRequestContext ctx, String mensagem) {
        ctx.abortWith(
                Response.status(401)
                        .entity(new ErroResponse(mensagem))
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }
}
