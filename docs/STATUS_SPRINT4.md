# 📊 STATUS DO PROJETO RAIZ DO BEM — Sprint 4

**Data:** 6 de maio de 2026  
**Prazo final:** 17 de maio de 2026 (15 dias)  
**Status global:** 🟡 **Em andamento (auditoria manual em 06/05)**

> Este documento é um “placar vivo” do que está **realmente** implementado no repositório vs. requisitos da Sprint 4.

---

## 📈 RESUMO EXECUTIVO (estado real em 06/05)

### O que melhorou recentemente (e já vale ponto)

- ✅ **Camada `exception/` criada** com handler global (`ExceptionsMapperGlobal`) e `ErroDTO` retornando JSON.
  - Mapeamentos atuais:
    - `NaoEncontradoException` → **404**
    - `ValidacaoException` → **422**
    - `RegraNegocioException` → **409**
    - `RequisicaoInvalidaException` → **400** (extra útil)
- ✅ **DTOs de criação/atualização** existem para os principais recursos (`Criar*DTO`, `Atualizar*DTO`).
- ✅ Controllers estão estruturados e já existem prints de Swagger para GETs e erros de exclusão.

### Principais gaps (o que ainda derruba pontuação se ficar assim)

- 🚨 **Config Oracle via env vars ainda NÃO está conforme requisito**
  - `src/main/resources/application.properties` ainda tem `system/123` e URL localhost hardcoded.
- 🚨 **4 métodos de negócio obrigatórios (Sprint 3 → Services) ainda não estão implementados “de ponta a ponta”**.
- 🟡 **CRUD ainda está parcial/inconsistente** em alguns recursos (ex.: `PUT` comentado em `PedidoAjudaController`; services ainda com campos comentados e vínculos pendentes).
- 🟡 **import.sql** ainda está template (sem dados demo) — ok para Oracle real, mas falta justificar/organizar evidência.

---

## ✅ O QUE JÁ ESTÁ IMPLEMENTADO (EVIDÊNCIA NO CÓDIGO)

### 1) Dependências / Stack (pom.xml)

- ✅ Quarkus REST + Jackson (`quarkus-rest`, `quarkus-rest-jackson`)
- ✅ OpenAPI/Swagger (`quarkus-smallrye-openapi`)
- ✅ Panache (`quarkus-hibernate-orm-panache`)
- ✅ Oracle JDBC (`quarkus-jdbc-oracle`)
- ✅ Validator (`quarkus-hibernate-validator`)
- ✅ Lombok presente como `provided` (permitido para a entrega)

### 2) Exceptions (requisito Sprint 4)

- ✅ `src/main/java/.../exception/` existe e está funcional
- ✅ `ExceptionsMapperGlobal` anotado com `@Provider` e retornando `ErroDTO` em JSON

> Observação: os nomes das exceções não são exatamente os do enunciado (ex.: `NaoEncontradoException` vs. `RecursoNaoEncontradoException`), mas o comportamento HTTP está correto. Ajustar no doc final (ou renomear se quiser alinhar 100%).

### 3) DTOs

- ✅ Existem DTOs separados para criação e atualização:
  - `CriarBeneficiarioDTO`, `AtualizarBeneficiarioDTO`
  - `CriarDentistaDTO`, `AtualizarDentistaDTO`
  - `CriarPedidoAjudaDTO`, `AtualizarPedidoAjudaDTO`
  - `CriarAtendimentoDTO`, `AtualizarAtendimentoDTO`
  - `CriarColaboradorDTO`, `AtualizarColaboradorDTO`
  - `EnderecoRequestDTO`, `ResponseViaCepDTO`, `ErroDTO`

---

## 🟡 IMPLEMENTADO PARCIALMENTE (precisa fechar)

### 1) Configuração de Oracle (application.properties)

**Estado atual (06/05):** hardcoded.  
**Requisito:** usar env vars.

Falta ajustar para:
- `quarkus.datasource.username=${DB_USER}`
- `quarkus.datasource.password=${DB_PASSWORD}`
- `quarkus.datasource.jdbc.url=${DB_URL:jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl}`
- `quarkus.hibernate-orm.database.generation=update`
- `quarkus.swagger-ui.always-include=true`

### 2) Services com regras de negócio obrigatórias

**Estado atual (06/05):** services existem e CRUD básico existe, mas regras Sprint 3→4 ainda não estão completas.

Faltam os métodos (conforme enunciado):
- `BeneficiarioService.adicionar(idPedido, idProgramaSocial)`
- `DentistaService.validarCro(cro)` → hoje existe e retorna boolean; falta **lançar `ValidacaoException`** quando inválido e usar na criação/atualização.
- `PedidoAjudaService.processarPedido(id, novoStatus, idDentista)`
- `AtendimentoService.encerrar(id, prontuario, dataFinal, idColaborador)`

Além disso, ainda existem pontos pendentes nos services atuais:
- `PedidoAjudaService.criar()`: há campos inconsistentes (ex.: `descricaoProblema` sendo setado com `email`) e vínculo com `Endereco` está comentado.
- `AtendimentoService.criarAtendimento()`: ainda não seta `dataInicial=LocalDate.now()` nem amarra beneficiário/dentista.
- `BeneficiarioService.criarBeneficiario()`: ainda não amarra pedido/endereço/programa.

### 3) Controllers (CRUD)

**Estado atual (06/05):** estrutura OK, mas alguns endpoints ainda estão faltando ou com inconsistências.

- `PedidoAjudaController`: `PUT` está comentado.
- Padronização: garantir `Response` consistente em todos os controllers, e evitar `try/catch` genérico que vira 400 quando deveria ser 404/422.

---

## 🔜 PRÓXIMOS PASSOS (ordem sugerida)

1) **Arrumar `application.properties` para env vars** (bloqueador da entrega Oracle).
2) **Fechar os 4 métodos obrigatórios** (com `@Transactional` quando escrever) + prints no Swagger.
3) **Completar CRUD com DTOs** (principalmente os `PUT` e validações) e padronizar status HTTP.
4) **Atualizar evidências** em `docs/PROGRESSO_SPRINT4.md` e organizar prints.
5) **Decidir estratégia do `import.sql`**: 
   - ou adicionar dados simples para dev; 
   - ou documentar que a base de evidência é o Oracle da FIAP (sem depender de import).

---

## 📌 Notas rápidas (qualidade)

- ✅ Melhorou bastante ao trocar mensagens genéricas por exceções específicas.
- ✅ Ter DTOs separados (create/update) deixa a API mais limpa e evita “entidade como request”.
- ⚠️ Próximo ganho de qualidade vem de **alinhar nomes, vínculos (FKs) e regras** (PedidoAjuda → Beneficiario; Atendimento → Beneficiario/Dentista/Colaborador) e testar fluxos completos.

```
Meta: com config Oracle + 4 métodos de negócio + CRUD consistente + evidências, dá pra chegar em 85+.
```

---

> Se quiser, eu também posso gerar um “roteiro de commits” (commit-by-commit) pra você subir as mudanças em blocos pequenos e fáceis de revisar pela equipe.
