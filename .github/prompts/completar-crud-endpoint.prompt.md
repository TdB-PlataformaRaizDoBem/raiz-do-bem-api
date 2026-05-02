# Prompt: Completar Endpoints REST + Tratamento de Excecoes

## 1. Criar excecoes customizadas em `exception/`

Criar o pacote `br.com.raizdobem.api.exception` com:

### `RecursoNaoEncontradoException.java`
```java
package br.com.raizdobem.api.exception;
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message) { super(message); }
}
```

### `ValidacaoException.java`
```java
package br.com.raizdobem.api.exception;
public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String message) { super(message); }
}
```

### `RegraDeNegocioException.java`
```java
package br.com.raizdobem.api.exception;
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String message) { super(message); }
}
```

### `ErroResponse.java`
```java
package br.com.raizdobem.api.exception;
public class ErroResponse {
    private final String mensagem;
    private final int status;
    public ErroResponse(String mensagem, int status) {
        this.mensagem = mensagem;
        this.status = status;
    }
    public String getMensagem() { return mensagem; }
    public int getStatus() { return status; }
}
```

### `GlobalExceptionMapper.java`
```java
package br.com.raizdobem.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException ex) {
        if (ex instanceof RecursoNaoEncontradoException) {
            return Response.status(404).entity(new ErroResponse(ex.getMessage(), 404)).build();
        }
        if (ex instanceof ValidacaoException) {
            return Response.status(422).entity(new ErroResponse(ex.getMessage(), 422)).build();
        }
        if (ex instanceof RegraDeNegocioException) {
            return Response.status(409).entity(new ErroResponse(ex.getMessage(), 409)).build();
        }
        return Response.status(500).entity(new ErroResponse("Erro interno: " + ex.getMessage(), 500)).build();
    }
}
```

---

## 2. Padrao de Controller com CRUD completo

Aplicar o padrao abaixo a todos os controllers. Exemplo para `BeneficiarioController`:

```java
@RequestScoped
@Path("/beneficiarioDTO")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "BeneficiarioDTO", description = "Gestao de beneficiarios.")
public class BeneficiarioController {

    @Inject BeneficiarioService service;

    @GET
    @Operation(summary = "Lista todos os beneficiarios.")
    @APIResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    public Response listarTodos() {
        return Response.ok(service.listarTodos()).build();
    }

    @POST
    @Operation(summary = "Cria beneficiarioDTO a partir de pedido aprovado.")
    @APIResponse(responseCode = "201", description = "BeneficiarioDTO criado.")
    @APIResponse(responseCode = "404", description = "Pedido de ajuda nao encontrado.")
    @APIResponse(responseCode = "409", description = "Pedido nao aprovado.")
    public Response criar(@QueryParam("idPedido") Long idPedido,
                          @QueryParam("idProgramaSocial") Long idProgramaSocial) {
        return Response.status(201).entity(service.criarBeneficiario(idPedido, idProgramaSocial)).build();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Busca beneficiarioDTO pelo CPF.")
    @APIResponse(responseCode = "200", description = "BeneficiarioDTO encontrado.")
    @APIResponse(responseCode = "404", description = "BeneficiarioDTO nao encontrado.")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        return Response.ok(service.buscarPorCpf(cpf)).build();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Atualiza dados do beneficiarioDTO.")
    @APIResponse(responseCode = "200", description = "Atualizado com sucesso.")
    @APIResponse(responseCode = "404", description = "BeneficiarioDTO nao encontrado.")
    public Response atualizar(@PathParam("cpf") String cpf, Beneficiario dados) {
        return Response.ok(service.atualizar(cpf, dados)).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Remove beneficiarioDTO.")
    @APIResponse(responseCode = "204", description = "Removido com sucesso.")
    @APIResponse(responseCode = "404", description = "BeneficiarioDTO nao encontrado.")
    public Response excluir(@PathParam("cpf") String cpf) {
        service.excluir(cpf);
        return Response.noContent().build();
    }
}
```

Replicar para: `AtendimentoController`, `DentistaController`, `ColaboradorController`,
`PedidoAjudaController`, `ProgramaSocialController`, `EnderecoController`.

---

## 3. Tabela de Endpoints (para a documentacao PDF)

| Recurso         | Metodo | URI                             | Status Esperado     |
|-----------------|--------|---------------------------------|---------------------|
| Beneficiario    | GET    | /beneficiarioDTO                   | 200                 |
| Beneficiario    | POST   | /beneficiarioDTO?idPedido=&idPrograma= | 201, 404, 409   |
| Beneficiario    | GET    | /beneficiarioDTO/{cpf}             | 200, 404            |
| Beneficiario    | PUT    | /beneficiarioDTO/{cpf}             | 200, 404            |
| Beneficiario    | DELETE | /beneficiarioDTO/{cpf}             | 204, 404            |
| Atendimento     | GET    | /atendimentoDTO                    | 200                 |
| Atendimento     | POST   | /atendimentoDTO                    | 201, 409            |
| Atendimento     | GET    | /atendimentoDTO/{id}               | 200, 404            |
| Atendimento     | PUT    | /atendimentoDTO/{id}               | 200, 404            |
| Atendimento     | DELETE | /atendimentoDTO/{id}               | 204, 404            |
| Dentista        | GET    | /dentistaDTO                       | 200                 |
| Dentista        | GET    | /dentistaDTO/disponiveis           | 200                 |
| Dentista        | POST   | /dentistaDTO                       | 201, 422            |
| Dentista        | GET    | /dentistaDTO/{cpf}                 | 200, 404            |
| Dentista        | PUT    | /dentistaDTO/{cpf}                 | 200, 404, 422       |
| Dentista        | DELETE | /dentistaDTO/{cpf}                 | 204, 404            |
| PedidoAjuda     | GET    | /pedido-ajuda                   | 200                 |
| PedidoAjuda     | POST   | /pedido-ajuda                   | 201, 422            |
| PedidoAjuda     | GET    | /pedido-ajuda/{id}              | 200, 404            |
| PedidoAjuda     | PUT    | /pedido-ajuda/{id}/processar    | 200, 409, 422       |
| PedidoAjuda     | DELETE | /pedido-ajuda/{id}              | 204, 404            |
| ProgramaSocial  | GET    | /programa-social                | 200                 |
| ProgramaSocial  | POST   | /programa-social                | 201                 |
| ProgramaSocial  | GET    | /programa-social/{id}           | 200, 404            |
| ProgramaSocial  | PUT    | /programa-social/{id}           | 200, 404            |
| ProgramaSocial  | DELETE | /programa-social/{id}           | 204, 404            |
| Colaborador     | GET    | /colaboradorDTO                    | 200                 |
| Colaborador     | POST   | /colaboradorDTO                    | 201, 422            |
| Colaborador     | GET    | /colaboradorDTO/{cpf}              | 200, 404            |
| Colaborador     | PUT    | /colaboradorDTO/{cpf}              | 200, 404            |
| Colaborador     | DELETE | /colaboradorDTO/{cpf}              | 204, 404            |
| Endereco        | GET    | /enderecoDTO                       | 200                 |
| Endereco        | POST   | /enderecoDTO                       | 201                 |
| Endereco        | GET    | /enderecoDTO/{id}                  | 200, 404            |
| Endereco        | PUT    | /enderecoDTO/{id}                  | 200, 404            |
| Endereco        | DELETE | /enderecoDTO/{id}                  | 204, 404            |

Copie essa tabela diretamente para a secao "Tabela de Endpoints" da documentacao PDF.
