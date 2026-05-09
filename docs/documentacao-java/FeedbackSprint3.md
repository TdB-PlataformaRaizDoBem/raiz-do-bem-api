# Feedback de Avaliação - Sprint 3 (Java)

## 📌 Contexto do Projeto
- **Projeto:** Raiz do Bem - Backend Java
- **Repositório:** [GitHub - Raiz do Bem](https://github.com/TdB-PlataformaRaizDoBem/raiz-do-bem-backend-java)
- **Tecnologia:** Java Puro
- **Nota Final:** **85**

---

## 📝 Feedback Oficial do Professor

### Resumo Geral
A equipe apresentou um projeto funcional e alinhado aos requisitos da sprint, com implementação das principais funcionalidades e organização geral do código adequada. Observa-se um bom entendimento dos conceitos trabalhados, especialmente na estruturação da aplicação e na implementação das operações principais.

### 🔍 Análise de Arquitetura e Responsabilidades
* **Violação do Padrão MVC:** A classe de conexão foi localizada na camada `Model`. Isso é considerado um erro de responsabilidade, pois mistura **infraestrutura** com **domínio**.
* **Recomendação:** Mover a lógica de conexão para uma camada de infraestrutura (ex: `ConnectionFactory`), separando adequadamente o acesso a dados.
* **Orquestração de Testes:** Os testes foram considerados fragmentados. Embora funcionais, indicam necessidade de uma visão mais sistêmica e fluxos integrados.

### ✅ Pontos Fortes
* Projeto bem organizado.
* Padrão arquitetural presente e consistente.
* Estrutura geral sólida.

### ⚠️ Pontos de Melhoria / Desconto
* **Arquitetura:** Falta de aderência à Clean Architecture (violação de camadas).
* **Testes:** Organização funcional, mas sem grande valor de integração.

> **Conclusão do Instrutor:** "Projeto bem construído, mas com inconsistência arquitetural relevante."

---

## 💡 Minha Opinião Pessoal (Análise de Nota)

Na minha percepção, o desconto da nota foi motivado quase exclusivamente pela localização da classe de conexão. Caso eu tivesse criado uma `ConnectionFactory` isolada ou uma camada de infraestrutura dedicada, separando a conexão da camada `Model/VO`, a nota provavelmente seria 100.

## 🚀 Contexto de Evolução Técnica (Atualização)
* **Observação Importante**: Este feedback refere-se exclusivamente à entrega da Sprint 3 (Java Puro).

Desde então, o projeto passou por uma reestruturação completa e migração tecnológica:

* **Novo Repositório**: O desenvolvimento foi movido para uma nova arquitetura baseada no framework Quarkus.

* **Objetivo Final**: Esta migração visa não apenas atender aos requisitos da entrega final, mas também otimizar o desempenho e a manutenibilidade do backend da plataforma Raiz do Bem.