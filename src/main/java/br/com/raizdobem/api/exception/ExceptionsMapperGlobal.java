package br.com.raizdobem.api.exception;

import br.com.raizdobem.api.dto.ErroDTO;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ExceptionsMapperGlobal implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof NaoEncontradoException) {
            return Response.status(404)
                    .entity(new ErroDTO(404, e.getMessage()))
                    .build();
        }
        if (e instanceof ValidacaoException) {
            return Response.status(422)
                    .entity(new ErroDTO(422, e.getMessage()))
                    .build();
        }
        if (e instanceof RegraNegocioException) {
            return Response.status(409)
                    .entity(new ErroDTO(409, e.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErroDTO(500, "Erro interno do servidor: " + e.getMessage()))
                .build();
    }

}
