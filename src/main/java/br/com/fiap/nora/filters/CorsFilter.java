package br.com.fiap.nora.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    private static final List<String> ORIGENS_PERMITIDAS = List.of(
            "http://localhost:5173",
            "https://projeto-nora.vercel.app"
    );

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String origin = requestContext.getHeaderString("Origin");
        if (origin != null && ORIGENS_PERMITIDAS.contains(origin)) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        }
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
