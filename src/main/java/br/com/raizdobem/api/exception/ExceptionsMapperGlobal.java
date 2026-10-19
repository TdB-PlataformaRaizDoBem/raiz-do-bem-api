package br.com.raizdobem.api.exception;

import br.com.raizdobem.api.dto.response.ErroResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import org.jboss.logging.Logger;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ExceptionsMapperGlobal implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ExceptionsMapperGlobal.class);

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof NaoEncontradoException) {
            LOG.warnf("[HTTP 404] %s: %s", e.getClass().getSimpleName(), e.getMessage());
            return Response.status(404)
                    .entity(new ErroResponse(404, e.getMessage(), LocalDateTime.now()))
                    .build();
        }
        if (e instanceof ValidacaoException) {
            LOG.warnf("[HTTP 422] %s: %s", e.getClass().getSimpleName(), e.getMessage());
            return Response.status(422)
                    .entity(new ErroResponse(422, e.getMessage(), LocalDateTime.now()))
                    .build();
        }
        if (e instanceof RegraNegocioException) {
            LOG.warnf("[HTTP 409] %s: %s", e.getClass().getSimpleName(), e.getMessage());
            return Response.status(409)
                    .entity(new ErroResponse(409, e.getMessage(), LocalDateTime.now()))
                    .build();
        }
        if (e instanceof RequisicaoInvalidaException) {
            LOG.warnf("[HTTP 400] %s: %s", e.getClass().getSimpleName(), e.getMessage());
            return Response.status(400)
                    .entity(new ErroResponse(400, e.getMessage(), LocalDateTime.now()))
                    .build();
        }
        if (e instanceof ConstraintViolationException) {
            LOG.warnf("[HTTP 400] %s: %s", e.getClass().getSimpleName(), e.getMessage());
            return Response.status(400)
                    .entity(new ErroResponse(400, e.getMessage(), LocalDateTime.now()))
                    .build();
        }

        if (e instanceof WebApplicationException wae && wae.getResponse().getStatus() < 500) {
            int status = wae.getResponse().getStatus();
            String mensagem = status == 404 ? "Recurso não encontrado." : e.getMessage();
            LOG.warnf("[HTTP %d] %s: %s", status, wae.getClass().getSimpleName(), mensagem);
            return Response.status(status)
                    .entity(new ErroResponse(status, mensagem, LocalDateTime.now()))
                    .build();
        }

        if (isViolacaoIntegridadeBanco(e)) {
            LOG.warnf("[HTTP 409] %s: Conflito de integridade de dados (chave duplicada no banco)", e.getClass().getSimpleName());
            return Response.status(409)
                    .entity(new ErroResponse(409, "Registro já existente ou violação de integridade dos dados.", LocalDateTime.now()))
                    .build();
        }

        LOG.errorf(e, "[HTTP 500] %s: %s", e.getClass().getSimpleName(), e.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErroResponse(500, "Erro interno do servidor.", LocalDateTime.now()))
                .build();
    }

    private boolean isViolacaoIntegridadeBanco(Throwable t) {
        Throwable current = t;
        while (current != null) {
            if (current instanceof org.hibernate.exception.ConstraintViolationException
                    || current instanceof SQLIntegrityConstraintViolationException
                    || current instanceof EntityExistsException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

}
