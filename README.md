# 🌱 Raiz do Bem — API

> Backend REST da plataforma Raiz do Bem, voltada à gestão de beneficiários, atendimentos odontológicos, colaboradores, pedidos de ajuda e programas sociais.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://openjdk.org/projects/jdk/21/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.5-blue?logo=quarkus)](https://quarkus.io/)
[![Oracle DB](https://img.shields.io/badge/Oracle-DB-red?logo=oracle)](https://www.oracle.com/database/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger_UI-green?logo=swagger)](http://localhost:8080/q/swagger-ui)

## Visão geral

A **Raiz do Bem API** foi desenvolvida como entrega acadêmica do **FIAP Challenge 2025-26** para a disciplina *Domain Driven Design Using Java* — **Sprint 4 (entrega final)**.

O projeto evoluiu de uma solução Java/JDBC da Sprint 3 para uma API em **Quarkus 3**, com **Hibernate ORM + Panache**, **Oracle DB**, **OpenAPI/Swagger** e tratamento centralizado de exceções. O objetivo é entregar uma base robusta, documentada e pronta para integração com front-end.

Este README foi expandido para documentar todas as funcionalidades novas e evoluções não abordadas na versão anterior, incluindo regras de negócio migradas para `service/`, mapeamentos JPA alinhados ao schema Oracle existente e convenções de desenvolvimento.

---

## Sumário (os principais tópicos atualizados)

- Novas regras de negócio implementadas nos Services
- Mapeamento JPA e convenções de nomes das tabelas/colunas Oracle
- Repositórios baseados em Panache com métodos customizados
- Tratamento de exceções centralizado e mapeadores HTTP
- Endpoints REST (CRUD) com respostas e códigos HTTP esperados
- Integração ViaCEP e comandos úteis para desenvolvimento em Windows
- Checklist de entrega da Sprint 4 e evidências

---

## Pacote raiz e organização do código

O código-fonte está organizado no pacote raiz `br.com.raizdobem.api` com as camadas seguindo DDD:

- `resource/` → recursos REST (JAX-RS) — camada de apresentação
- `service/` → regras de negócio — camada de domínio (BO)
- `repository/` → acesso a dados com Panache — camada de infraestrutura (DAO)
- `entity/` → entidades JPA e enums — camada de domínio
- `exception/` → exceções customizadas + `GlobalExceptionMapper`
- `dto/` → objetos de entrada e saída quando aplicável

---

## Novas funcionalidades e evoluções (documentadas)

A seguir estão as principais funcionalidades adicionadas/migradas na Sprint 4 e que agora estão documentadas:

1. Conexão com Oracle via Quarkus
   - Dependências: `quarkus-hibernate-orm-panache` e `quarkus-jdbc-oracle` (devem estar habilitadas no `pom.xml`).
   - Variáveis de ambiente para credenciais (não usar hardcode): `DB_USER`, `DB_PASSWORD`, `DB_URL`.
   - `application.properties` usa placeholders para as variáveis de ambiente; a URL padrão aponta para o servidor FIAP.

2. Mapeamento de entidades JPA fiel ao schema Oracle
   - Anotações JPA: `@Entity`, `@Table(name = "<Tabela>")`, `@Id`, `@GeneratedValue`, `@Column(name = "<coluna>")`, `@ManyToOne` onde houver FK.
   - Nomes das tabelas/colunas usados exatamente como no banco (ex.: tabela `Beneficiario` com coluna PK `id_beneficiario`).
   - Enums persistidos como String via `@Enumerated(EnumType.STRING)`.
   - Campo `disponivel` do `Dentista` mapeado como boolean no Java e armazenado como `CHAR(1)` ('S' / 'N') no Oracle (conversão aplicada via `@Convert` ou lógica em `@PostLoad`/`@PrePersist`).

3. Repositórios com Panache
   - Todos os repositórios estendem `PanacheRepository<T>`.
   - Implementados métodos extras com JPQL para buscas por CPF, cidade, programa, data, dentistas disponíveis etc.

4. Services com regras de negócio (migradas da Sprint 3)
   - `BeneficiarioService`:
     - `adicionar(idPedido, idProgramaSocial)` — converte um `PedidoAjuda` aprovado em `Beneficiario`; lança `RegraDeNegocioException`/`ValidacaoException` quando aplicável.
     - `listarPorCidade(cidade)` — lista beneficiários filtrando pelo `Endereco.cidade` (JOIN).
     - `listarPorPrograma(idPrograma)` — filtra por `id_programa_social`.
   - `DentistaService`:
     - `validarCro(cro)` — valida CRO com regex `^[a-zA-Z]{2,}\d{2}$`; lança `ValidacaoException` em caso de CRO inválido.
     - `listarDisponiveis()` — retorna dentistas com `disponivel = 'S'`.
     - `listarPorCidade(cidade)` — JOIN com `Endereco`.
   - `PedidoAjudaService`:
     - `processarPedido(id, novoStatus, idDentista)` — atualiza `status_pedido` e `id_dentista`; se `novoStatus == APROVADO`, invoca `BeneficiarioService.adicionar(id, idPrograma)`.
     - `listarPorData(data)` — filtra por `data_pedido`.
     - `validarIdadePedido(dataNasc)` — checa se maior ou igual a 18 anos (usa `Period.between`).
   - `AtendimentoService`:
     - `criar(prontuario, idBeneficiario, idDentista)` — cria `Atendimento` com `dataInicial = LocalDate.now()`.
     - `encerrar(id, prontuario, dataFinal, idColaborador)` — atualiza `data_final`, `prontuario` e `id_colaborador`.

   - Observações: serviços anotados com `@ApplicationScoped`, dependências com `@Inject` e métodos de escrita com `@Transactional`.

5. Tratamento de exceções
   - Exceções customizadas disponíveis em `exception/`:
     - `RecursoNaoEncontradoException` → mapeado para HTTP 404
     - `ValidacaoException` → mapeado para HTTP 422
     - `RegraDeNegocioException` → mapeado para HTTP 409
   - `GlobalExceptionMapper` (anotado com `@Provider`) converte exceções em payload JSON consistente (timestamp, mensagem, detalhes, status).

6. Endpoints REST (CRUD completo + documentação OpenAPI)
   - Controllers em `resource/` anotados com `@RequestScoped` e operações documentadas com `@Operation` e `@APIResponse`.
   - Todos os recursos expõem os CRUDs esperados (GET, POST, PUT, DELETE) e endpoints específicos como `GET /dentista/disponiveis` e `GET /endereco/viacep/{cep}`.
   - Respostas usam `Response` com status adequados: 200, 201, 204, 404, 409, 422.

7. Integração ViaCEP
   - Endpoint `GET /endereco/viacep/{cep}` consulta a API pública ViaCEP e retorna os dados do CEP para acelerar cadastros.

8. Importação de dados de demonstração
   - `src/main/resources/import.sql` contém inserts para popular o banco em desenvolvimento (mantido e atualizado conforme a evolução).

9. Testes e qualidade
   - Projetos com testes unitários nas pastas `src/test/java/` cobrindo serviços e mapeamentos básicos.
   - Comando de execução: `.
   mvnw.cmd test` (Windows PowerShell).

10. Docker / Build para produção
    - Dockerfiles para diferentes cenários em `src/main/docker/` (JVM, native stub etc.).

---

## Endpoints (resumo atualizado com rotas e respostas esperadas)

Base URL local: `http://localhost:8080`
Swagger UI: `http://localhost:8080/q/swagger-ui`

Resumo dos recursos expostos (com exemplos de status esperados):

- Beneficiario
  - GET /beneficiario — 200 OK
  - POST /beneficiario — 201 Created
  - GET /beneficiario/{cpf} — 200 OK / 404 Not Found
  - PUT /beneficiario/{cpf} — 200 OK / 404 Not Found / 422
  - DELETE /beneficiario/{cpf} — 204 No Content / 404 Not Found

- Dentista
  - GET /dentista — 200 OK
  - POST /dentista — 201 Created / 422 (CRO inválido)
  - GET /dentista/disponiveis — 200 OK
  - GET /dentista/cidade/{cidade} — 200 OK
  - GET /dentista/{cpf} — 200 OK / 404
  - PUT /dentista/{cpf} — 200 OK / 404 / 422
  - DELETE /dentista/{cpf} — 204 / 404

- Atendimento
  - GET /atendimento — 200 OK
  - POST /atendimento — 201 Created (dataInicial = hoje)
  - GET /atendimento/{cpf} — 200 OK
  - PUT /atendimento/{id} — 200 OK (encerrar/atualizar)
  - DELETE /atendimento/{id} — 204 / 404

- PedidoAjuda
  - GET /pedido-ajuda — 200 OK
  - POST /pedido-ajuda — 201 Created
  - GET /pedido-ajuda/data/{data} — 200 OK
  - PUT /pedido-ajuda/{id} — 200 OK (processar status) / 404 / 409
  - DELETE /pedido-ajuda/{id} — 204 / 404

- Colaborador
  - GET /colaborador — 200 OK
  - POST /colaborador — 201 Created
  - PUT /colaborador/{cpf} — 200 OK / 404
  - DELETE /colaborador/{cpf} — 204 / 404

- Endereco
  - GET /endereco — 200 OK
  - GET /endereco/id/{id} — 200 / 404
  - GET /endereco/{cidade} — 200 OK
  - GET /endereco/viacep/{cep} — 200 OK (ViaCEP)
  - POST /endereco — 201 Created
  - PUT /endereco/{id} — 200 OK / 404
  - DELETE /endereco/{id} — 204 / 404

- Especialidade
  - GET /especialidades — 200 OK
  - GET /especialidades/{id} — 200 / 404

- ProgramaSocial
  - GET /programas-sociais — 200 OK
  - GET /programas-sociais/{id} — 200 / 404

Observação: Consulte o Swagger UI para a especificação completa de modelos, exemplos de request/response e mensagens de erro padronizadas.

---

## Banco de dados — detalhes do mapeamento

O projeto foi alinhado ao schema Oracle já existente. Algumas observações importantes para manutenção:

- As entidades usam os nomes exatos das tabelas e colunas presentes no Oracle — evite renomear sem ajustar `@Table`/`@Column`.
- Chaves primárias seguem a convenção do banco (ex.: `id_beneficiario`, `id_dentista`, `id_atendimento` etc.) e são geradas por sequences do Oracle mapeadas via `@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "...")`.
- O campo `sexo` e `status_pedido` são armazenados como VARCHAR no banco, mapeados para enums Java com `@Enumerated(EnumType.STRING)`.
- `disponivel` no `Dentista` é um CHAR(1) no banco — no Java é boolean; existe um conversor/transformação para manter compatibilidade.

---

## Como executar (Windows PowerShell)

1) Configurar variáveis de ambiente (example):

```powershell
$env:DB_USER = "seu_usuario"
$env:DB_PASSWORD = "sua_senha"
$env:DB_URL = "jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl"
```

2) Executar em modo desenvolvimento (hot reload):

```powershell
.\mvnw.cmd quarkus:dev
```

3) Compilar o projeto:

```powershell
.\mvnw.cmd compile
```

4) Executar testes:

```powershell
.\mvnw.cmd test
```

5) Gerar build de produção:

```powershell
.\mvnw.cmd package -DskipTests
```

6) Acessar a aplicação localmente:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/q/swagger-ui`

---

## Desenvolvimento e contribuição

- Seguir convenções do projeto (pacote `br.com.raizdobem.api`, camadas, nomes de tabelas/colunas).
- Antes de commitar, certifique-se de que `./mvnw.cmd test` roda sem falhas.
- Atualize `import.sql` com dados de demonstração quando adicionar novas entidades obrigatórias.
- Documente endpoints adicionados com `@Operation`/`@APIResponse` para aparecer no Swagger.

Pull requests devem conter descrição clara do que foi alterado, classes afetadas e prints do Swagger (quando houver alteração de endpoint).

---

## Observações e dicas técnicas

- Use `@Transactional` somente em métodos que fazem escrita no banco (create/update/delete).
- Mantenha a lógica de negócio dentro de `service/` — os resources devem orquestrar chamadas e lidar com mapeamento HTTP.
- Quando alterar tipos persistidos (ex.: `disponivel`), atualize o conversor JPA e testes correspondentes.
- Para debugar problemas de SQL gerado pelo Hibernate, habilite logs SQL no `application.properties` temporariamente.

---

## Evidence & documentação

- A pasta `docs/prints_swagger/` contém evidências em imagem de endpoints executados via Swagger (GETs, POSTs e tratamento de erros).
- Documentação complementar e progresso estão em `docs/documentacao-java/` e diagramas em `docs/diagrams/`.

---

## Checklist da entrega (Sprint 4)

- [ ] ≥ 6 classes modelo com encapsulamento completo e anotações JPA
- [ ] Conexão Oracle configurada via variáveis de ambiente
- [ ] Repositories estendem `PanacheRepository`
- [ ] ≥ 4 métodos com lógica de negócio implementados nos Services
- [ ] Exceções customizadas + `GlobalExceptionMapper`
- [ ] CRUD completo em todos os controllers com `Response` tipado
- [ ] Endpoints documentados com OpenAPI/Swagger
- [ ] `import.sql` com dados de demonstração
- [ ] Código compila e roda sem erros (`.\mvnw.cmd compile`)

---

## Changelog resumido das evoluções (Sprint 3 → Sprint 4)

- Migração do backend Java/JDBC para Quarkus + Hibernate ORM + Panache.
- Regras de negócio centralizadas (services) e expostas por recursos REST.
- Mapeamentos JPA atualizados para refletir fielmente o schema Oracle.
- Implementação de `GlobalExceptionMapper` e exceções customizadas.
- Integração com ViaCEP para auxiliar no cadastro de endereços.
- Adição de testes automatizados e import.sql para dados de demonstração.

---

## Contatos / referência

- Repositório original (histórico): Azure DevOps — raiz-do-bem-backend
- Diretor do projeto / contato académico: verificar cabeçalho do trabalho entregue à FIAP

---

Se desejar, posso também:
- Gerar automaticamente uma tabela completa (CSV/Markdown) com todos os endpoints e modelos de request/response extraídos do código.
- Adicionar badges de build/quality (GitHub Actions / SonarQube) e um `CONTRIBUTING.md`.

Caso queira que eu já aplique uma dessas melhorias, diga qual e eu atualizo os arquivos correspondentes (README, docs ou arquivos de configuração).
