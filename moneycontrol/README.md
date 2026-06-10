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

## Pré-requisitos

1. **Java 21** (JDK, não apenas JRE):
   - Windows/macOS: https://adoptium.net  
   - Linux: `sudo apt install openjdk-21-jdk`

2. **Maven 3.8+**:
   - https://maven.apache.org/download.cgi  
   - Linux: `sudo apt install maven`

---

## Como compilar e executar

### 1. Clone / extraia o projeto

```
moneycontrol/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/moneycontrol/
│       │   ├── MainApp.java
│       │   ├── dao/          ← acesso ao banco
│       │   ├── model/        ← entidades
│       │   ├── service/      ← regras de negócio
│       │   ├── ui/           ← telas JavaFX
│       │   └── util/         ← utilitários
│       └── resources/
│           └── styles.css
└── README.md
```

### 2. Compile

```bash
mvn clean package -q
```

Isso gera dois arquivos em `target/`:
- `moneycontrol-1.0.0.jar` (sem dependências)
- `moneycontrol-1.0.0-executable.jar` ← **use este**

### 3. Execute

```bash
java -jar target/moneycontrol-1.0.0-executable.jar
```

> **Windows:** dê duplo clique no arquivo `-executable.jar`  
> (desde que o Java 21 esteja instalado e associado a arquivos `.jar`)

---

## Banco de dados

O SQLite cria o arquivo automaticamente em:
- **Windows:** `C:\Users\SeuNome\MoneyControl\moneycontrol.db`
- **macOS/Linux:** `~/MoneyControl/moneycontrol.db`

Você pode abrir este arquivo com o [DB Browser for SQLite](https://sqlitebrowser.org) para ver os dados persistidos — ótimo para apresentação!

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

## Arquitetura

```
MainApp (JavaFX)
│
├── ui/          ← Camada de Apresentação (Views)
│   ├── LoginView, CadastroView
│   ├── DashboardView, GastosView
│   ├── NovoGastoView, RelatorioView
│   ├── GastoItemCard (componente reutilizável)
│   └── Navbar (componente reutilizável)
│
├── service/     ← Camada de Negócio (regras, validações)
│   ├── UsuarioService
│   └── GastoService
│
├── dao/         ← Camada de Acesso a Dados (SQL)
│   ├── DatabaseConnection
│   ├── UsuarioDAO
│   └── GastoDAO
│
├── model/       ← Entidades
│   ├── Usuario, Gasto, Categoria
│
└── util/        ← Utilitários
    ├── Session (usuário logado)
    └── Formatador (moeda, data)
```
