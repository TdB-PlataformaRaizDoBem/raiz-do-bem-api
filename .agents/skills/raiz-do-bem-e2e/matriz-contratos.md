# Matriz de contratos e cenários — Raiz do Bem

Fonte: leitura estática aprofundada em 2026-10-19.
Back-end: Java 21, Quarkus 3.34.5, Hibernate ORM Panache, Oracle.
Front-end: React 19, TypeScript, Vite, TanStack Query.

---

## 1. URL da API e Ambientes

- **Frontend**: lê `import.meta.env.API_BASE_URL ?? ''` (`Raiz-do-Bem/src/services/httpClient.ts` e services). Padrão local (`.env.template`): `http://localhost:8080`.
- **Produção**: URL não versionada no repositório. Configurada no painel Vercel (`API_BASE_URL`) e injetada no build client-side.
- **Backend Azure**: `azure-pipelines.yml` e `pom.xml` (`azure-webapp-maven-plugin`) definem `appName = raizdobem-backend` no grupo `rg-raizdobem-backend` (plano F1, canadacentral). O hostname público exato deve ser consultado no portal da nuvem, sem deduções precipitadas.
- **Serviços auxiliares externos**: `CHAT_API_URL` (porta 8000) e `GEO_API_URL` (FastAPI /api/v1/vulnerabilidade/*) não pertencem a este repositório Java.
- **Terceiros integrados no backend**: ViaCEP (`https://viacep.com.br/ws/{cep}/json/`) e Google Routes (`https://routes.googleapis.com/distanceMatrix/v2:computeRouteMatrix`).

---

## 2. Autenticação e Autorização

- `POST /auth/tokenAcesso` `{email, senha}` → `{token, refreshToken, tipo: "BearerToken"}`. Senha verificada com `BcryptUtil.matches`.
- `POST /auth/refreshToken` `{refreshToken}` → valida claim `type == "refresh"`, busca colaborador e emite novo par.
- **Tokens**: JWT RS256 com emissor `raiz-do-bem`. Access token expira em 30 min; Refresh token expira em 7 dias. Chaves em `keyPublica.pem` e `keyPrivada.pem`.
- **Permissões (RBAC)**:
  - `@PermitAll`: público.
  - `@RolesAllowed({"ADMIN", "COLABORADOR"})`: acesso operacional geral.
  - `@RolesAllowed("ADMIN")`: ações destrutivas (exclusões de entidades) e gestão de contas/colaboradores.

---

## 3. Matriz Completa de Endpoints do Backend

| Recurso / Rota | Método | Papel | DTO Entrada | DTO Saída | Efeito nos Dados | Cenário de Teste |
|---|---|---|---|---|---|---|
| `/auth/tokenAcesso` | POST | `@PermitAll` | `LoginRequest` (email, senha) | `Map<String, String>` (tokens) | Nenhum (leitura) | MOCK / P1-06 |
| `/auth/refreshToken` | POST | `@PermitAll` | `RefreshTokenRequest` (@NotBlank) | `Map<String, String>` (tokens) | Nenhum (leitura) | MOCK / P1-05 |
| `/colaborador` | POST | `@RolesAllowed("ADMIN")` | `ColaboradorCreateRequest` | `ColaboradorResponse` (201) | Cria Colaborador (bcrypt) | I-0 (P1-03) / I-DB |
| `/colaborador` | GET | `ADMIN`, `COLABORADOR` | QueryParam: pagina, size | `PageResponse<ColaboradorResponse>` | Nenhum (cache colaboradores) | I-0 / I-DB |
| `/colaborador/legado` | GET | `ADMIN`, `COLABORADOR` | – | `List<ColaboradorResponse>` | Nenhum (sem paginação) | I-0 / I-DB |
| `/colaborador/{cpf}` | GET | `ADMIN`, `COLABORADOR` | PathParam: cpf | `ColaboradorResponse` | Nenhum | I-0 / I-DB |
| `/colaborador/{cpf}` | PUT | `@RolesAllowed("ADMIN")` | `ColaboradorUpdateRequest` | 200 OK | Atualiza email/senha | I-0 (P1-03) / I-DB |
| `/colaborador/{cpf}` | DELETE | `@RolesAllowed("ADMIN")` | PathParam: cpf | 204 No Content | Exclui colaborador | I-0 (P1-03) / I-DB |
| `/dentista` | POST | `@PermitAll` | `DentistaCreateRequest` (@Valid) | `DentistaResponse` (201) | Cria Dentista e Endereço | MOCK / P1-04 / I-DB |
| `/dentista` | GET | `ADMIN`, `COLABORADOR` | QueryParam: pagina, size | `PageResponse<DentistaResponse>` | Nenhum (cache dentistas) | I-0 / I-DB |
| `/dentista/disponiveis` | GET | `ADMIN`, `COLABORADOR` | – | `List<DentistaResponse>` | Nenhum | I-0 / I-DB |
| `/dentista/{cpf}` | GET | `ADMIN`, `COLABORADOR` | PathParam: cpf | `DentistaResponse` | Nenhum | I-0 / I-DB |
| `/dentista/cidade/{cidade}`| GET | `ADMIN`, `COLABORADOR` | PathParam: cidade | `List<DentistaResponse>` | Nenhum | I-0 / I-DB |
| `/dentista/exportarCsv` | GET | `ADMIN`, `COLABORADOR` | – | 200 OK (`text/csv`) | Nenhum | I-0 / I-DB |
| `/dentista/{cpf}` | PUT | `ADMIN`, `COLABORADOR` | `DentistaUpdateRequest` | `DentistaResponse` (200) | Atualiza Dentista/Endereço | I-0 / I-DB |
| `/dentista/{cpf}` | DELETE | `@RolesAllowed("ADMIN")` | PathParam: cpf | 204 No Content | Exclui Dentista | I-0 (P1-03) / I-DB |
| `/pedido-ajuda` | POST | `@PermitAll` | `PedidoAjudaCreateRequest` (@Valid) | `PedidoAjudaResponse` (201) | Cria Pedido e Endereço | MOCK / P1-04 / I-DB |
| `/pedido-ajuda` | GET | `ADMIN`, `COLABORADOR` | QueryParam: pagina, size | `PageResponse<PedidoAjudaResponse>`| Nenhum (cache pedidos) | I-0 / I-DB |
| `/pedido-ajuda/data/{data}`| GET | `ADMIN`, `COLABORADOR` | PathParam: data | `List<PedidoAjudaResponse>` | Nenhum | I-0 / I-DB |
| `/pedido-ajuda/{id}` | PUT | `ADMIN`, `COLABORADOR` | `PedidoAjudaUpdateRequest` | `PedidoAjudaResponse` (200) | Atualiza status (APROVADO/REJEITADO) | I-0 / I-DB |
| `/pedido-ajuda/{id}` | DELETE | `@RolesAllowed("ADMIN")` | PathParam: id | 204 No Content | Exclui Pedido | I-0 (P1-03) / I-DB |
| `/beneficiario` | POST | `ADMIN`, `COLABORADOR` | `BeneficiarioCreateRequest` (@NotNull) | `BeneficiarioResponse` (201) | Cria Beneficiário de Pedido APROVADO | I-0 / I-DB |
| `/beneficiario` | GET | `ADMIN`, `COLABORADOR` | QueryParam: pagina, size | `PageResponse<BeneficiarioResponse>`| Nenhum (cache beneficiarios) | I-0 / I-DB |
| `/beneficiario/{cpf}` | GET | `ADMIN`, `COLABORADOR` | PathParam: cpf | `BeneficiarioResponse` | Nenhum | I-0 / I-DB |
| `/beneficiario/cidade/{c}` | GET | `ADMIN`, `COLABORADOR` | PathParam: cidade | `List<BeneficiarioResponse>` | Nenhum | I-0 / I-DB |
| `/beneficiario/programa/{id}`| GET| `ADMIN`, `COLABORADOR` | PathParam: idProgramaSocial | `List<BeneficiarioResponse>` | Nenhum | I-0 / I-DB |
| `/beneficiario/exportarCsv`| GET | `@RolesAllowed("ADMIN")` | – | 200 OK (`text/csv`) | Nenhum | I-0 (P1-03) / I-DB |
| `/beneficiario/{cpf}` | PUT | `ADMIN`, `COLABORADOR` | `BeneficiarioUpdateRequest` | `BeneficiarioResponse` (200) | Atualiza Beneficiário/Endereço | I-0 / I-DB |
| `/beneficiario/{cpf}` | DELETE | `@RolesAllowed("ADMIN")` | PathParam: cpf | 204 No Content | Exclui Beneficiário | I-0 (P1-03) / I-DB |
| `/atendimento/` | POST | `ADMIN`, `COLABORADOR` | `AtendimentoCreateRequest` (@Valid) | 201 Created (Header Location) | Cria Atendimento e vincula match | I-0 / I-DB |
| `/atendimento/` | GET | `ADMIN`, `COLABORADOR` | QueryParam: pagina, size | `PageResponse<AtendimentoResponse>`| Nenhum (cache atendimentos) | I-0 / I-DB |
| `/atendimento/{cpf}` | GET | `ADMIN`, `COLABORADOR` | PathParam: cpf (beneficiário) | `AtendimentoResponse` | Nenhum | I-0 / I-DB |
| `/atendimento/exportarCsv` | GET | `@RolesAllowed("ADMIN")` | – | 200 OK (`text/csv`, pipe delim) | Nenhum | I-0 (P1-03) / I-DB |
| `/atendimento/{cpf}` | PUT | `ADMIN`, `COLABORADOR` | `AtendimentoUpdateRequest` | 200 OK | Finaliza atendimento (dataFinal) | I-0 / I-DB |
| `/atendimento/{id}` | DELETE | `@RolesAllowed("ADMIN")` | PathParam: id | 204 No Content | Exclui Atendimento | I-0 (P1-03) / I-DB |
| `/endereco` | POST | `ADMIN`, `COLABORADOR` | `EnderecoCompletoRequest` | `Endereco` (201) | Cria endereço avulso | I-0 / I-DB |
| `/endereco` | GET | `ADMIN`, `COLABORADOR` | – | `List<EnderecoResponse>` | Nenhum (cache enderecos) | I-0 / I-DB |
| `/endereco/id/{id}` | GET | `ADMIN`, `COLABORADOR` | PathParam: id | `EnderecoResponse` | Nenhum | I-0 / I-DB |
| `/endereco/{cidade}` | GET | `ADMIN`, `COLABORADOR` | PathParam: cidade | `List<EnderecoResponse>` | Nenhum | I-0 / I-DB |
| `/endereco/viacep/{cep}` | GET | `ADMIN`, `COLABORADOR` | PathParam: cep | `ViaCepDTO` | Proxy externo ViaCep | I-0 / I-DB |
| `/endereco/{id}` | PUT | `ADMIN`, `COLABORADOR` | `EnderecoCompletoRequest` | `EnderecoResponse` | Atualiza Endereço | I-0 / I-DB |
| `/endereco/{id}` | DELETE | `@RolesAllowed("ADMIN")` | PathParam: id | 204 No Content | Exclui Endereço | I-0 (P1-03) / I-DB |
| `/especialidades` | GET | `@PermitAll` | – | `List<Especialidade>` | Nenhum | I-0 / I-DB |
| `/especialidades/{id}` | GET | `@PermitAll` | PathParam: id | `Especialidade` | Nenhum | I-0 / I-DB |
| `/programas-sociais` | GET | `@PermitAll` | – | `List<ProgramaSocial>` | Nenhum | I-0 / I-DB |
| `/programas-sociais/{id}` | GET | `@PermitAll` | PathParam: id | `ProgramaSocial` | Nenhum | I-0 / I-DB |

---

## 4. Retificação de Alarmes Falsos Anteriores

1. **Invalidação de Cache de Pedidos**:
   - *Status Anterior*: Hipótese de bug por falta de invalidação.
   - *Correção Confirmada*: **Refutada**. Em `PedidoAjudaService.java` (linha 40), o método `criar()` possui a anotação `@CacheInvalidateAll(cacheName = "pedidos")` e a classe está sob `@Transactional` (linha 28). O cache é invalidado na criação, processamento e exclusão de pedidos.
2. **Exposição de Botão CSV para Colaborador na UI**:
   - *Status Anterior*: Hipótese de que a interface pudesse expor o botão e induzir erro 403 para perfil COLABORADOR.
   - *Correção Confirmada*: **Refutada**. Nos componentes do frontend (`Beneficiarios.tsx`, `Designacao.tsx` e `Dentistas.tsx`), o botão de exportar CSV é condicionado estritamente à verificação `isAdmin` (`user?.role === 'ADMIN'`). Apenas no backend há uma assimetria permissiva em `GET /dentista/exportarCsv`, que aceita `COLABORADOR`, ao passo que beneficiários e atendimentos exigem `ADMIN`.

---

## 5. Riscos e Inconsistências Técnicas Confirmadas (R1 a R11)

- **R1 — Exceção de Data Inesperada (500 Internal Server Error)**:
  `GET /pedido-ajuda/data/{data}` utiliza `LocalDate.parse(data)` sem tratamento de erro ou validação prévia. Datas em formato inválido estouram `DateTimeParseException`, que não é capturada especificamente no `ExceptionsMapperGlobal` e resulta em HTTP 500 em vez de 400.
- **R2 — Conversão de Enums sem Tratamento em Endpoints Públicos (500)**:
  Endpoints públicos (`POST /pedido-ajuda`, `POST /dentista`) realizam `Sexo.valueOf(...)` diretamente sobre entrada do usuário. Valores fora de `M`, `F`, `O` geram `IllegalArgumentException` não tratada e retornam HTTP 500 para requisições anônimas. Idem em `/endereco` para `TipoEndereco.valueOf(...)` e `cep` nulo.
- **R3 — Ausência de Escape e Vulnerabilidade a Injeção em CSVs**:
  A classe `CsvUtil` monta CSVs via concatenação de strings sem tratar aspas, vírgulas, quebras de linha ou caracteres de gatilho de fórmulas em planilhas (`=`, `+`, `-`, `@`). Campos vindos de formulários abertos na internet podem ser exportados diretamente para administradores sem sanitização. Há também incoerência de delimitador (`|` em atendimentos vs `,` em dentistas/beneficiários).
- **R4 — Paginação sem Limites e Risco de Sobrecarga de Memória**:
  Parâmetros `pagina` e `size` não possuem validação de teto máximo (ex.: `size=100000`) nem de valores negativos. Repositórios chamam `Page.of(pagina, tamanho)` sem barreiras. O cache `@CacheResult` armazena respostas inteiras em memória sem política explícita de TTL ou despejo configurada em `application.properties`.
- **R5 — Cabeçalho Location Relativo e Inconsistente em Atendimento**:
  `AtendimentoResource.criar` devolve HTTP 201 com `Location: api/atendimento/{id}`. Trata-se de URI relativa contendo o prefixo `api/`, que não existe no roteamento padrão da aplicação (`/atendimento/`), gerando URLs inválidas para clientes REST estritos.
- **R6 — Autodeclaração de Categoria e Disponibilidade em Cadastro Público**:
  `POST /dentista` é público (`@PermitAll`) e aceita qualquer valor para `categoria` e `disponivel`. No backend, a aprovação de pedidos de ajuda exige `dentista.getCategoria().equals("COORDENADOR")`, e o algoritmo de match geográfico busca dentistas marcados como disponíveis. Um voluntário externo pode cadastrar-se como coordenador ativo sem etapa prévia de moderação.
- **R7 — Criação de Colaborador com Validação Frouxa**:
  `POST /colaborador` permite `senha` nula (executando hash bcrypt de null), aceita qualquer string como `role` e não verifica duplicidade de e-mail na inserção (enquanto o login utiliza `find("email").firstResult()`).
- **R8 — Ausência de Rate Limiting e de Deny-Unannotated-Endpoints**:
  Não há interceptadores de limitação de taxa em endpoints sensíveis (como tentativas repetitivas de login em `/auth/tokenAcesso`). Se novos endpoints forem criados sem anotações de segurança, o comportamento padrão do Quarkus pode deixá-los desprotegidos.
- **R9 — Clientes REST Externos sem Timeout em Métodos Transacionais**:
  As chamadas para ViaCEP e Google Maps ocorrem dentro de métodos marcados com `@Transactional` (ex.: `criarDentista`, `criarAtendimento`). As chamadas não possuem timeout explícito configurado nas propriedades, retendo conexões abertas do pool de banco de dados (limitado a 10 conexões com timeout de aquisição de 5s).
- **R10 — Consultas Paginadas sem Cláusula ORDER BY Explícita**:
  Métodos `listagemPaginacao` em repositórios utilizam `findAll().page(Page.of(pagina, tamanho))` sem ordenação explícita. No banco Oracle, a ordenação de linhas sem `ORDER BY` é não-determinística, podendo gerar duplicidade ou omissão de registros ao navegar entre páginas.
- **R11 — Ausência de Migrações de Banco Versionadas (DDL não Canônico)**:
  O projeto utiliza `database.generation=none` e o arquivo `import.sql` está vazio. Não há ferramentas como Flyway ou Liquibase configuradas. As restrições de integridade referencial (`ON DELETE CASCADE`, chaves estrangeiras, índices únicos) dependem exclusivamente de criação manual no schema Oracle, impedindo reconstrução autônoma do banco para testes.
