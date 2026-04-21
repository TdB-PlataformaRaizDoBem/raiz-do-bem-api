# Prompt: Completar Endpoints REST + Tratamento de Exceções

## 1. Criar exceções customizadas em `exception/`

Criar o pacote `br.com.raizdobem.api.exception` com as seguintes classes:

### `RecursoNaoEncontradoException.java`
```java
package br.com.raizdobem.api.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}
```

### `ValidacaoException.java`
```java
package br.com.raizdobem.api.exception;

public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String message) {
        super(message);
    }
}
```

### `RegraDeNegocioException.java`
```java
package br.com.raizdobem.api.exception;

public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String message) {
        super(message);
    }
}
```

### `ErroResponse.java` (DTO de resposta de erro)
```java
package br.com.raizdobem.api.exception;

public class ErroResponse {
    private String mensagem;
    private int status;

    public ErroResponse(String mensagem, int status) {
        this.mensagem = mensagem;
        this.status = status;
    }
    // getters
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
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErroResponse(ex.getMessage(), 404)).build();
        }
        if (ex instanceof ValidacaoException) {
            return Response.status(422)
                    .entity(new ErroResponse(ex.getMessage(), 422)).build();
        }
        if (ex instanceof RegraDeNegocioException) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErroResponse(ex.getMessage(), 409)).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErroResponse("Erro interno: " + ex.getMessage(), 500)).build();
    }
}
```

---

## 2. Padrão de Controller com CRUD completo

Aplicar o padrão abaixo a **todos** os controllers. Exemplo para `BeneficiarioController`:

```java
@RequestScoped
@Path("/beneficiario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Beneficiario", description = "Gestão de beneficiários.")
public class BeneficiarioController {

    @Inject
    BeneficiarioService service;

    @GET
    @Operation(summary = "Lista todos os beneficiários.")
    @APIResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    public Response listarTodos() {
        return Response.ok(service.listarTodos()).build();
    }

    @POST
    @Operation(summary = "Cadastra novo beneficiário.")
    @APIResponse(responseCode = "201", description = "Beneficiário criado.")
    @APIResponse(responseCode = "422", description = "CPF já cadastrado.")
    public Response criar(Beneficiario beneficiario) {
        return Response.status(Response.Status.CREATED)
                .entity(service.criar(beneficiario)).build();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Busca beneficiário pelo CPF.")
    @APIResponse(responseCode = "200", description = "Beneficiário encontrado.")
    @APIResponse(responseCode = "404", description = "Beneficiário não encontrado.")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        return Response.ok(service.buscarPorCpf(cpf)).build();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Atualiza dados do beneficiário.")
    @APIResponse(responseCode = "200", description = "Atualizado com sucesso.")
    @APIResponse(responseCode = "404", description = "Beneficiário não encontrado.")
    public Response atualizar(@PathParam("cpf") String cpf, Beneficiario dados) {
        return Response.ok(service.atualizar(cpf, dados)).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Remove beneficiário.")
    @APIResponse(responseCode = "204", description = "Removido com sucesso.")
    @APIResponse(responseCode = "404", description = "Beneficiário não encontrado.")
    public Response excluir(@PathParam("cpf") String cpf) {
        service.excluir(cpf);
        return Response.noContent().build();
    }
}
```

Replicar para: `AtendimentoController`, `DentistaController`, `ColaboradorController`,
`PedidoAjudaController`, `ProgramaSocialController`, `EnderecoController`, `EspecialidadeController`.

---

## 3. Tabela de Endpoints (para a documentação)

| Recurso         | Método | URI                         | Status Esperado     |
|-----------------|--------|-----------------------------|---------------------|
| Beneficiario    | GET    | /beneficiario               | 200                 |
| Beneficiario    | POST   | /beneficiario               | 201, 422            |
| Beneficiario    | GET    | /beneficiario/{cpf}         | 200, 404            |
| Beneficiario    | PUT    | /beneficiario/{cpf}         | 200, 404            |
| Beneficiario    | DELETE | /beneficiario/{cpf}         | 204, 404            |
| Atendimento     | GET    | /atendimento                | 200                 |
| Atendimento     | POST   | /atendimento                | 201, 409            |
| Atendimento     | GET    | /atendimento/{id}           | 200, 404            |
| Atendimento     | PUT    | /atendimento/{id}           | 200, 404            |
| Atendimento     | DELETE | /atendimento/{id}           | 204, 404            |
| Dentista        | GET    | /dentista                   | 200                 |
| Dentista        | POST   | /dentista                   | 201, 422            |
| Dentista        | GET    | /dentista/{id}              | 200, 404            |
| Dentista        | PUT    | /dentista/{id}              | 200, 404            |
| Dentista        | DELETE | /dentista/{id}              | 204, 404            |
| PedidoAjuda     | GET    | /pedido-ajuda               | 200                 |
| PedidoAjuda     | POST   | /pedido-ajuda               | 201                 |
| PedidoAjuda     | GET    | /pedido-ajuda/{id}          | 200, 404            |
| PedidoAjuda     | PUT    | /pedido-ajuda/{id}          | 200, 409            |
| PedidoAjuda     | DELETE | /pedido-ajuda/{id}          | 204, 404            |
| ProgramaSocial  | GET    | /programa-social            | 200                 |
| ProgramaSocial  | POST   | /programa-social            | 201                 |
| ProgramaSocial  | GET    | /programa-social/{id}       | 200, 404            |
| ProgramaSocial  | PUT    | /programa-social/{id}       | 200, 404            |
| ProgramaSocial  | DELETE | /programa-social/{id}       | 204, 404            |
| Colaborador     | GET    | /colaborador                | 200                 |
| Colaborador     | POST   | /colaborador                | 201, 422            |
| Colaborador     | GET    | /colaborador/{id}           | 200, 404            |
| Colaborador     | PUT    | /colaborador/{id}           | 200, 404            |
| Colaborador     | DELETE | /colaborador/{id}           | 204, 404            |
| Endereco        | GET    | /endereco                   | 200                 |
| Endereco        | POST   | /endereco                   | 201                 |
| Endereco        | GET    | /endereco/{id}              | 200, 404            |
| Endereco        | PUT    | /endereco/{id}              | 200, 404            |
| Endereco        | DELETE | /endereco/{id}              | 204, 404            |
| Especialidade   | GET    | /especialidade              | 200                 |
| Especialidade   | POST   | /especialidade              | 201, 422            |
| Especialidade   | GET    | /especialidade/{id}         | 200, 404            |
| Especialidade   | PUT    | /especialidade/{id}         | 200, 404            |
| Especialidade   | DELETE | /especialidade/{id}         | 204, 404            |

Copie essa tabela diretamente para a seção "Tabela de Endpoints" da documentação PDF.
