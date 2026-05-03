# 📌 Progresso Sprint 4 — Evidências (Swagger / Docs)

Este arquivo serve como **log de evidências** (prints + endpoints) para facilitar a montagem do **PDF final da Sprint 4**.

> Dica: ao testar no Swagger, salve o print com nome descritivo e registre aqui a rota, método e o que foi validado.

---

## 🧭 Relatório consolidado (IA)

- Arquivo: `docs/status_projeto_claude/guia-sprint4-raiz-do-bem.html`
- Última atualização do relatório: **02/05 — 17:37**

Use este HTML como **auditoria/checklist-mestre** do que falta (exceptions, métodos de negócio e CRUD completo).

---

## ✅ Evidências já adicionadas (`docs/prints_swagger/`)

### Endereço (`/endereco`)

- ✅ `GET /endereco` — lista todos
  - Print: `listando_enderecos.png`
- ✅ `GET /endereco/id/{id}` — busca por id
  - Print: `lista_endereco_id.png`
- ✅ `GET /endereco/{cidade}` — filtro por cidade
  - Print: `lista_endereco_cidade.png`
- ✅ `DELETE /endereco/{id}` — tratamento de 404
  - Print: `trata_erro_exclusao_endereco.png`

### Especialidades (`/especialidades`)

- ✅ `GET /especialidades` — listagem
  - Print: `lista_especialidades.png`

### Programas sociais (`/programas-sociais`)

- ✅ `GET /programas-sociais` — listagem
  - Print: `lista_programas_sociais.png`

### Tratamento de erro em exclusões (404)

- ✅ `DELETE /dentista/{cpf}`
  - Print: `trata_erro_excluir_dentista.png`
- ✅ `DELETE /atendimento/{id}`
  - Print: `trata_erro_exclusao_atendimento.png`
- ✅ `DELETE /colaborador/{cpf}`
  - Print: `trata_erro_exclusao_colaborador.png`
- ✅ `DELETE /pedido-ajuda/{id}`
  - Print: `trata_erro_exclusao_pedido.png`

---

## 🧾 Documentação base

- PDF base (Sprint 3): `docs/Sprint03Java.pdf`
- Banco/MER (Sprint 4 — em evolução): `docs/database/Sprint4-Banco-desenvolvendo.pdf`
- Diagramas:
  - `docs/diagrams/Diagrama de Classes Simples Verificacao.png`
  - `docs/diagrams/FluxoCentral.png`

---

## 🚧 Evidências que ainda faltam (para fechar o PDF Sprint 4)

### 1) 4 métodos obrigatórios com lógica de negócio (prints + explicação)

Quando estiverem implementados, capturar:

- [ ] **BeneficiarioService.adicionar(idPedido, idProgramaSocial)**
  - print de sucesso + print de erro (status != APROVADO)
- [ ] **DentistaService.validarCro(cro)**
  - print de erro 422 (CRO inválido)
- [ ] **PedidoAjudaService.processarPedido(id, novoStatus, idDentista)**
  - print aprovando e gerando Beneficiario
- [ ] **AtendimentoService.encerrar(id, prontuario, dataFinal, idColaborador)**
  - print encerrando atendimento (data_final preenchida)

### 2) CRUD completo (POST/PUT/DELETE) com status HTTP correto

- [ ] POST + PUT + DELETE de Beneficiario
- [ ] POST + PUT + DELETE de Dentista
- [ ] POST + PUT + DELETE de PedidoAjuda
- [ ] POST + PUT + DELETE de Atendimento
- [ ] POST + PUT + DELETE de Colaborador

### 3) Camada de exceções (`exception/`) comprovada

- [ ] print/explicação do `GlobalExceptionMapper` retornando JSON padronizado

---

## ✅ Check rápido antes de gerar o PDF Sprint 4

- [ ] Atualizar capa / nomes / Sprint 4
- [ ] Inserir tabela de endpoints (todas as rotas + status)
- [ ] Inserir MER do Oracle (pode ser do Sprint 3, se o schema for o mesmo)
- [ ] Inserir diagrama de classes atualizado
- [ ] Inserir prints (mínimo: 1 por método obrigatório + alguns CRUD)
- [ ] Inserir instruções de execução (PowerShell e/ou Bash)

