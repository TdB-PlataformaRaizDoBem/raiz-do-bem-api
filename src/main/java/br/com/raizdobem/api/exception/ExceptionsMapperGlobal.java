package br.com.raizdobem.api.exception;

import br.com.raizdobem.api.dto.response.ErroDTO;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import org.jboss.logging.Logger;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ExceptionsMapperGlobal implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ExceptionsMapperGlobal.class);

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof NaoEncontradoException) {
            return Response.status(404)
                    .entity(new ErroDTO(404, e.getMessage(), LocalDateTime.now()))
                    .build();
        }
        if (e instanceof ValidacaoException) {
            return Response.status(422)
                    .entity(new ErroDTO(422, e.getMessage(), LocalDateTime.now()))
                    .build();
        }
        if (e instanceof RegraNegocioException) {
            return Response.status(409)
                    .entity(new ErroDTO(409, e.getMessage(), LocalDateTime.now()))
                    .build();
        }
        if (e instanceof RequisicaoInvalidaException) {
            return Response.status(400)
                    .entity(new ErroDTO(400, e.getMessage(), LocalDateTime.now()))
                    .build();
        }

        LOG.errorf(e, "[Erro 500]: %s, %s", e.getClass().getSimpleName(), e.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErroDTO(500, "Erro interno do servidor.", LocalDateTime.now()))
                .build();
    }

}
