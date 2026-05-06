# Contexto rápido (para colar em um novo chat)

## Projeto
- **Nome:** Raiz do Bem API
- **Stack:** Java 21 · Quarkus 3.x (README cita 3.34.5) · Maven Wrapper (`mvnw.cmd`) · Oracle DB (FIAP)
- **Pacote raiz:** `br.com.raizdobem.api`
- **Arquitetura (DDD por camadas):** `controller/` → `service/` → `repository/` → `model/` + `exception/` + `dto/`

## Como rodar / validar
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Compilar: `./mvnw compile` (PowerShell: `./mvnw.cmd clean compile`)
- Variáveis de ambiente:
  - `DB_USER`, `DB_PASSWORD`
  - `DB_URL` (default esperado: `jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl`)

## Evidências (prints Swagger)
Pasta: `docs/prints_swagger/`
- Endereço:
  - `GET_enderecos.png`
  - `GET endereco_id.png`
  - `GET_endereco_cidade.png`
  - `POST_endereco_sucesso.png`
  - `trata_erro_exclusao_endereco.png`
- Colaborador:
  - `POST_colaborador_sucesso.png`
- Outros GETs:
  - `GET_especialidades.png`
  - `GET_programa_social.png`
- Tratamentos de erro (404 em DELETE):
  - `trata_erro_excluir_dentista.png`
  - `trata_erro_exclusao_atendimento.png`
  - `trata_erro_exclusao_colaborador.png`
  - `trata_erro_exclusao_pedido.png`

## Estado atual (resumo honesto)
- **Camada de exceções existe** em `src/main/java/br/com/raizdobem/api/exception/`:
  - `NaoEncontradoException` → 404
  - `ValidacaoException` → 422
  - `RegraNegocioException` → 409
  - `ExceptionsMapperGlobal` → padroniza payload com `ErroDTO`
- **Controllers com POST/PUT/DELETE implementados (parcial/real):**
  - `EnderecoController`: POST real (ViaCEP + persistência), DELETE real, PUT ainda stub.
  - `DentistaController`: POST/PUT/DELETE implementados.
  - `ColaboradorController`: POST/PUT/DELETE implementados.
- **Controllers ainda com stubs importantes:**
  - `BeneficiarioController` (POST e GET/{cpf} estão stub/errados)
  - `AtendimentoController` (POST/PUT/GET/{cpf} com stubs)
  - `PedidoAjudaController` (POST/PUT com stubs)
- **Métodos de negócio obrigatórios (Sprint 3 → Sprint 4):**
  - Implementado: `DentistaService.validarCro()` (regex `^[a-zA-Z]{2,}\d{2}$`)
  - Pendentes: `BeneficiarioService.adicionar(...)`, `PedidoAjudaService.processarPedido(...)`, `AtendimentoService.encerrar(...)`

## Documentação
- Índice: `docs/INDEX.md`
- Progresso/evidências: `docs/PROGRESSO_SPRINT4.md`
- Status/checklist: `STATUS_SPRINT4.md` + `RESUMO_RAPIDO.txt`
- PDF Java (base Sprint 3): `docs/documentacao-java/Sprint03Java.pdf`
- PDF DB: `docs/documentacao-database/`
- Relatório IA (snapshot antigo): `docs/status_projeto_claude/guia-sprint4-raiz-do-bem.html`

## Objetivo do próximo ciclo de trabalho
1) Implementar os **3 métodos obrigatórios restantes** (com `@Transactional` e exceções 404/422/409) + expor endpoints
2) Remover stubs e padronizar Responses nos controllers restantes
3) Capturar prints Swagger (sucesso + erro) e fechar o PDF Sprint 4

