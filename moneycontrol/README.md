# MoneyControl 💰
**Sistema de Controle de Gastos Pessoais**
Projeto Prático — Engenharia de Software II
Integrantes: Gabriel Henrique Amador, Eric Faria dos Santos

---

## Tecnologias
| Camada     | Tecnologia              |
|------------|-------------------------|
| Linguagem  | Java 21                 |
| Interface  | JavaFX 21               |
| Banco      | SQLite (arquivo local)  |
| Senhas     | BCrypt (jBCrypt 0.4)    |
| Build      | Maven 3.8+              |

---



## Funcionalidades implementadas

| RF | Descrição                              | Status |
|----|----------------------------------------|--------|
| 01 | Cadastro de usuários                   | ✅     |
| 02 | Registro de gastos                     | ✅     |
| 03 | Classificar gastos por categoria       | ✅     |
| 04 | Cálculo automático do total            | ✅     |
| 05 | Relatório de gastos                    | ✅     |
| 06 | Consultar gastos cadastrados           | ✅     |
| 07 | Remover registros de gastos            | ✅     |

---

## Telas

1. **Login** — autenticação com e-mail e senha (hash BCrypt)
2. **Cadastro** — criação de conta com validação de campos
3. **Dashboard** — resumo de gastos + recentes + barras por categoria
4. **Meus Gastos** — listagem completa com filtro por categoria e exclusão
5. **Novo Gasto** — formulário com descrição, valor, data e categoria
6. **Relatório** — total, maior gasto, distribuição por categoria e listagem completa

---

