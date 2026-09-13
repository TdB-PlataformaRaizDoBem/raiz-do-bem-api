Tenho todas as informações necessárias para produzir a análise completa. Vou compor agora.O sub-agente fez um levantamento completo. Aqui está a visão geral consolidada:

---

# Análise de Arquitetura — `raiz-do-bem-api`

## Contexto do Projeto

Backend REST para a plataforma **Raiz do Bem**, da ONG Turma do Bem, desenvolvido no FIAP Challenge 2025-26 (Sprint 4 — DDD com Java). Centraliza gestão de beneficiários, pedidos de ajuda, dentistas voluntários, colaboradores, atendimentos e programas sociais.

---

## Stack e Tecnologias

| Camada | Tecnologia |
|---|---|
| Runtime | Java 21 + Quarkus 3.34.5 |
| Persistência | Hibernate ORM + Panache + Oracle JDBC |
| Segurança | SmallRye JWT (RSA), `@RolesAllowed`, Bcrypt |
| API Docs | SmallRye OpenAPI + Swagger UI |
| Integrações | MicroProfile REST Client (ViaCEP, Google Maps Routes API) |
| Utilitários | Lombok, Gson, Hibernate Validator |
| Build/Deploy | Maven, Azure Pipelines → Azure Web App (canadacentral, F1) |
| Container | Docker JVM (ubi9/openjdk-21-runtime) |

---

## Arquitetura em Camadas

```
HTTP Request
    │
    ▼
Resource (@RequestScoped) — validação de entrada (@Valid), @RolesAllowed
    │
    ▼
Service (@ApplicationScoped) — regras de negócio, @Transactional
    │         │
    │         ├──► EnderecoService → ViaCepClient (externo)
    │         ├──► AtendimentoMatchService → GoogleMapsService → GoogleMapsClient (externo)
    │         └──► ValidacaoService (utilitário estático)
    │
    ▼
Repository (PanacheRepository) — queries JPQL
    │
    ▼
Entity (JPA) → Oracle DB

Exceções → ExceptionsMapperGlobal (@Provider) → ErroDTO (JSON)
Entidades → Mapper (estático) → DTO de resposta
```

---

## Fluxo Central do Domínio

```
1. POST /pedido-ajuda  (@PermitAll)
   └─ Cria pedido PENDENTE, consulta ViaCEP para enriquecer endereço
   └─ Regra: homem maior de 18 anos → status automaticamente REJEITADO

2. PUT /pedido-ajuda/{id}  (COLABORADOR/ADMIN)
   └─ Dentista COORDENADOR é vinculado; status vai para APROVADO ou REJEITADO

3. POST /beneficiario  (COLABORADOR/ADMIN)
   └─ Requer pedido APROVADO; copia dados do pedido para o beneficiário

4. POST /atendimento  (COLABORADOR/ADMIN)
   └─ Consulta Google Maps Routes API para encontrar dentista disponível mais próximo
   └─ Persiste com dataInicial = hoje

5. PUT /atendimento/{cpf}  (COLABORADOR/ADMIN)
   └─ Vincula colaborador, atualiza prontuário, dataFinal = hoje

Autenticação: POST /auth/login → JWT com role → @RolesAllowed nos endpoints
```

---

## Pontos Fortes

**Separação de camadas clara** — o fluxo Resource → Service → Repository → Entity é consistente em todo o projeto, sem vazamentos de lógica para fora da service.

**DTOs como Java Records** — imutabilidade e concisão na camada de transporte, com Bean Validation diretamente nas anotações.

**Tratamento centralizado de erros** — `ExceptionsMapperGlobal` captura todas as exceções customizadas e retorna `ErroDTO` padronizado. Hierarquia bem definida: `NaoEncontradoException (404)`, `ValidacaoException (422)`, `RegraNegocioException (409)`, `RequisicaoInvalidaException (400)`.

**Segurança bem estruturada** — JWT com RSA, bcrypt nas senhas, roles granulares por endpoint, chaves externas via variáveis de ambiente.

**Integrações externas desacopladas** — ViaCEP e Google Maps como interfaces MicroProfile REST Client; configuração via `application.properties`; chave da API via env var.

**Match geográfico inteligente** — uso da Google Maps Routes API para selecionar o dentista disponível mais próximo ao beneficiário, com fallback em caso de falha externa.

**Configuração sem segredos hardcoded** — banco, chaves JWT e API keys via `${env_var}`.

---

## Inconsistências

**Enums vs. documentação divergentes**
- `Sexo` no código: `M`, `F`, `O`. Na documentação (README, copilot-instructions): `MASCULINO`/`FEMININO`. Se o banco foi criado com os valores longos, as inserções falharão.
- `TipoEndereco` no código: `PROFISSIONAL`. Na documentação: `COMERCIAL`. O mesmo risco se aplica ao schema.

**Delimitadores CSV inconsistentes** — `CsvUtil` usa `,` para beneficiários/dentistas e `|` para atendimentos, sem justificativa.

**Validação de CPF apenas formal** — `ValidacaoService.validarCpf` verifica só `\d{11}`, sem calcular os dígitos verificadores. Aceita `00000000000` como válido.

**`AtendimentoDTO` usa `Object` para campos tipados** — `beneficiario`, `dentista` e `dataFim` são `Object`, misturando strings e tipos complexos. Quebra type-safety e pode gerar problemas de serialização.

**`ColaboradorResource.buscarUnico` retorna a entidade `Colaborador` diretamente** — o campo `senha` (mesmo que hashado) fica exposto na resposta. `ColaboradorMapper` existe mas está completamente vazio.

---

## Bugs / Problemas Funcionais

| # | Arquivo | Problema |
|---|---|---|
| 1 | `PedidoAjudaRepository.atualizar` | Chama `find(...).firstResult()` e descarta o resultado — não executa nenhuma atualização |
| 2 | `AtendimentoRepository.atualizar` | Método completamente vazio (comentado "atualização é feita na service", mas a service também não atualiza campos básicos via SQL) |
| 3 | `DentistaService.criarDentista` | Hardcoda ids 1 e 2 para `ProgramaSocial` sem verificar existência — potencial `NullPointerException` se o banco não tiver esses registros |
| 4 | `BeneficiarioService.listarParaExportacao` | Chama `listarTodos()` e remapeia para lista idêntica sem transformação — método redundante e inútil |
| 5 | `AtendimentoResource.criar` | Retorna `HTTP 200` em vez de `201 Created` para criação de recurso |
| 6 | `PedidoAjudaMapper` (vs. outros mappers) | É o único mapper que **lança exceção** quando o pedido é null — comportamento inconsistente com os demais |

---

## Débitos Técnicos

**Ausência total de testes** — `src/test` não existe. As dependências `quarkus-junit` e `rest-assured` estão no `pom.xml` mas sem nenhuma implementação. Para um projeto de ONG com regras de negócio sensíveis (elegibilidade por sexo/idade, match geográfico, aprovação de pedidos), a ausência de testes é o principal risco.

**`import.sql` vazio** — arquivo existe mas sem dados de seed. O README lista isso como pendente no checklist.

**CORS aberto (`*`)** — adequado para desenvolvimento, não para produção.

**Regra de negócio com critério questionável** — pedidos de homens maiores de 18 anos são automaticamente rejeitados *em silêncio* (persistidos com status `REJEITADO` sem erro ao usuário). Quem faz o pedido recebe `HTTP 201` sem saber que foi rejeitado na mesma operação.

**`listarTodos()` lança exceção em lista vazia** — `NaoEncontradoException` para lista vazia é semanticamente incorreto (404 implica recurso não encontrado). O correto seria retornar `[]` com `HTTP 200`.

**Entidade `Dentista` com comentário interno** — "Havia me esquecido da relação Dentista N:N com o programaSocial" no código de produção.

**`ProgramaSocial` e `Especialidade` sem IDENTITY** — o banco precisa ter os registros pré-existentes; não há seed data e o `import.sql` está vazio.

---

## Resumo de Riscos por Prioridade

| Prioridade | Problema |
|---|---|
| 🔴 Alta | Ausência de testes + regras de negócio críticas não cobertas |
| 🔴 Alta | `ColaboradorResource` expõe entidade com hash da senha |
| 🔴 Alta | `PedidoAjudaRepository.atualizar` não funciona (bug silencioso) |
| 🟡 Média | Hardcode de ids de ProgramaSocial (risco de NPE em produção) |
| 🟡 Média | Enums divergentes entre código e documentação (risco de falha no banco) |
| 🟡 Média | Rejeição silenciosa de pedidos (UX enganosa) |
| 🟢 Baixa | CORS `*`, delimitadores CSV inconsistentes, `import.sql` vazio |