package br.com.fiap.nora.resources;

import br.com.fiap.nora.bo.PessoaBO;
import br.com.fiap.nora.dto.ErroResponse;
import br.com.fiap.nora.entities.Pessoa;
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

@Path("/pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {

    private PessoaBO pessoaBO = new PessoaBO();

    @GET
    public Response listar() {
        try {
            List<Pessoa> lista = pessoaBO.listar();
            return Response.ok(lista).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao listar pessoas.")).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") long id) {
        try {
            Pessoa pessoa = pessoaBO.buscarPorId(id);
            if (pessoa == null) {
                return Response.status(404).entity(new ErroResponse("Pessoa nao encontrada.")).build();
            }
            return Response.ok(pessoa).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao buscar pessoa.")).build();
        }
    }

    @POST
    public Response criar(Pessoa pessoa, @Context UriInfo uriInfo) {
        try {
            pessoaBO.criar(pessoa);
            return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(pessoa).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao criar pessoa.")).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") long id, Pessoa pessoa) {
        try {
            Pessoa existente = pessoaBO.buscarPorId(id);
            if (existente == null) {
                return Response.status(404).entity(new ErroResponse("Pessoa nao encontrada.")).build();
            }
            pessoa.setIdPessoa(id);
            pessoaBO.atualizar(pessoa);
            return Response.ok(pessoa).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(new ErroResponse(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new ErroResponse("Erro ao atualizar pessoa.")).build();
        }
    }
}
