# Raiz do Bem API — Instruções para o GitHub Copilot

## Visão Geral do Projeto

**Solução:** Raiz do Bem — plataforma de gestão de beneficiários, atendimentos odontológicos, colaboradores e programas sociais.  
**Contexto acadêmico:** FIAP Challenge 2025-26 · disciplina Domain Driven Design Using Java · **Sprint 4 (entrega final)**.  
**Stack:** Java 21 · Quarkus 3.x · Maven · Oracle DB · REST/JSON · OpenAPI (Swagger UI).

### Pacote raiz
```
br.com.raizdobem.api
```

### Estrutura de camadas (DDD)
```
controller/   → recursos REST (JAX-RS) — camada de apresentação
service/      → regras de negócio (BO) — camada de domínio
repository/   → acesso a dados (DAO) — camada de infraestrutura
model/        → entidades e enums — camada de domínio
exception/    → exceções customizadas — camada transversal
```

---

## Entidades existentes

| Classe          | Pacote `model`      | Papel principal                            |
|-----------------|---------------------|--------------------------------------------|
| Beneficiario    | model               | Pessoa atendida pelos programas sociais    |
| PedidoAjuda     | model               | Solicitação de auxílio feita pelo beneficiário |
| ProgramaSocial  | model               | Programa de assistência vinculado ao beneficiário |
| Atendimento     | model               | Consulta/atendimento odontológico          |
| Dentista        | model               | Profissional odontológico                  |
| Especialidade   | model               | Especialidade do dentista                  |
| Colaborador     | model               | Voluntário/funcionário da ONG              |
| Endereco        | model               | Endereço de beneficiário ou colaborador    |
| Categoria       | model (enum)        | Categoria do pedido de ajuda               |
| Sexo            | model (enum)        | Sexo do beneficiário/dentista              |
| StatusPedido    | model (enum)        | Status do pedido de ajuda                  |
| TipoEndereco    | model (enum)        | Residencial / Comercial                    |

---

## Requisitos da Sprint 4 ainda a implementar

### 1. Conexão com o banco de dados (Oracle)
- Descomentar e configurar `quarkus-hibernate-orm-panache` e `quarkus-jdbc-oracle` no `pom.xml`.
- Anotar entidades com `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@ManyToOne`, `@OneToMany`.
- Adicionar em `application.properties`:
  ```properties
  quarkus.datasource.db-kind=oracle
  quarkus.datasource.username=${DB_USER}
  quarkus.datasource.password=${DB_PASSWORD}
  quarkus.datasource.jdbc.url=${DB_URL}
  quarkus.hibernate-orm.database.generation=update
  ```
- Usar variáveis de ambiente para credenciais — nunca hardcode de senhas.

### 2. Camada Repository (DAO)
- Fazer cada `*Repository` estender `PanacheRepository<Entidade>` (ou `PanacheRepositoryBase`).
- Adicionar métodos de busca customizados com JPQL quando necessário.

### 3. Camada Service (BO)
- Injetar o Repository via `@Inject`.
- Implementar **no mínimo 4 métodos com lógica de negócio relevante**, por exemplo:
  1. `agendarAtendimento` — valida disponibilidade do dentista e cria atendimento.
  2. `avaliarElegibilidadeBeneficiario` — verifica critérios para entrada no programa social.
  3. `aprovarOuRejeitarPedido` — muda o status do pedido com base em regras definidas.
  4. `calcularEstatisticasAtendimento` — retorna métricas por período/dentista.

### 4. Tratamento de Exceções
- Criar exceções customizadas em `exception/`: `RecursoNaoEncontradoException`, `ValidacaoException`, `RegraDeNegocioException`.
- Criar um `@Provider` implementando `ExceptionMapper<>` para cada exceção, retornando JSON padronizado e status HTTP correto (404, 422, 409 etc.).

### 5. Controllers REST (CRUD completo)
- Cada controller deve ter: `GET /`, `POST /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`.
- Usar `@Consumes(MediaType.APPLICATION_JSON)` nos métodos que recebem corpo.
- Retornar `Response` com status correto (`201 Created`, `204 No Content`, `404` etc.).
- Anotar com `@Operation` e `@APIResponse` do MicroProfile OpenAPI.

---

## Convenções de código

- **Java 21** — pode usar records, sealed classes e pattern matching onde fizer sentido.
- **Quarkus CDI**: `@ApplicationScoped` para services/repositories; `@RequestScoped` para controllers.
- **Sem lombok** — gerar getters/setters/construtores explicitamente, conforme padrão atual do projeto.
- **Sem comentários** desnecessários; manter código autoexplicativo.
- **Validação de entrada**: usar Bean Validation (`@NotNull`, `@NotBlank`, `@CPF` etc.) nas entidades e parâmetros de controller.
- **Transactions**: anotar métodos de escrita no service com `@Transactional`.
- **import.sql**: popular o arquivo `src/main/resources/import.sql` com dados de exemplo para demonstração.

---

## Comandos de desenvolvimento

```bash
# Rodar em modo dev (hot reload)
./mvnw quarkus:dev

# Compilar
./mvnw compile

# Executar testes unitários
./mvnw test

# Build para produção (JAR)
./mvnw package -DskipTests

# Build imagem Docker
docker build -f src/main/docker/Dockerfile.jvm -t raiz-do-bem-api .
```

Swagger UI disponível em: `http://localhost:8080/q/swagger-ui`

---

## Checklist da entrega final (Sprint 4)

- [ ] ≥ 6 classes modelo com encapsulamento completo
- [ ] Conexão Oracle configurada via variáveis de ambiente
- [ ] Repositories estendem `PanacheRepository`
- [ ] ≥ 4 métodos com lógica de negócio implementados nos Services
- [ ] Exceções customizadas + ExceptionMappers
- [ ] CRUD completo em todos os controllers
- [ ] Endpoints documentados com OpenAPI/Swagger
- [ ] `import.sql` com dados de demonstração
- [ ] Código compila e roda sem erros
