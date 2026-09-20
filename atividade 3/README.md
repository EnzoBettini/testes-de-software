# Atividade 3 — Teste estrutural (JUnit + JaCoCo)

Entrega da **Avaliação Prática (item 3)**: testes unitários do projeto **Central de pedidos** e relatório estrutural.

## Projeto

Código e testes em [`central-pedidos/`](./central-pedidos/).

- Implementação: `src/main/java/br/edu/ifpr/pedidos/`
- Testes: `src/test/java/br/edu/ifpr/pedidos/`
- Relatório da disciplina: [`central-pedidos/RELATORIO.md`](./central-pedidos/RELATORIO.md)

## Como executar

Requisitos: **JDK 17+** e **Maven 3.9+**.

```bash
cd central-pedidos
mvn clean test
```

Relatório de cobertura (JaCoCo):

```bash
open target/site/jacoco/index.html
```

## Publicação

Publique o repositório no GitHub e envie o link no formulário da disciplina (mesmo fluxo das atividades 1 e 2).
