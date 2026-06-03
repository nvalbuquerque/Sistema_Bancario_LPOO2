# ROADMAP - Sistema Bancario LPOO II

Equipe: Dani, Laura e Nathalia  
Periodo de finalizacao: 04/06/2026 a 12/06/2026  
Objetivo: adaptar o sistema bancario Swing existente para MVC + DAO + JDBC, usando MySQL, `DataBase.properties`, modelagem relacional forte para heranca de contas, testes e materiais de defesa.

## Datas E Marcos

| Marco | Data | Observacao |
| --- | --- | --- |
| Inicio e arquitetura | 04/06/2026 | Divisao por modulos, banco definido e estrutura MVC/DAO planejada |
| Base JDBC + banco | 05/06/2026 | `ConnectionFactory`, `DataBase.properties`, `banco.sql` inicial |
| Modulos em desenvolvimento | 06/06 a 09/06 | Laura: Clientes; Nathalia: Contas; Dani: Operacoes/Integracao |
| Integracao geral | 10/06/2026 | Telas ligadas aos controllers e DAOs |
| Testes e defesa simulada | 11/06/2026 | Testes completos, bugs e roteiro de apresentacao |
| Entrega final | 12/06/2026 | Codigo, SQL, diagramas, README e JAR executavel |

## Divisao Principal Por Modulos

Esta divisao reduz conflito no Git e ajuda cada pessoa a defender uma parte completa do sistema.

| Membro | Modulo | Responsavel por | Entregaveis |
| --- | --- | --- | --- |
| Laura | Clientes | CRUD de clientes, buscas, ordenacoes, exclusao em cascata, `ClienteDao`, `ClienteController` | Tela de clientes integrada ao banco, DAO e controller de clientes |
| Nathalia | Contas | Vinculacao cliente-conta, `ContaCorrente`, `ContaInvestimento`, `ContaDao`, regras de negocio | Criacao de contas, persistencia das contas e regras do enunciado |
| Dani | Operacoes e integracao | Tela de operacoes, saque, deposito, saldo, remuneracao, tratamento de erros, integracao final | Operacoes bancarias completas, saldo persistido e fluxo final funcionando |

## Trabalho Compartilhado

Algumas partes devem ser decididas juntas porque afetam todos os modulos.

| Item | Responsaveis | Resultado esperado |
| --- | --- | --- |
| Banco de dados | Todas | Tabelas e relacionamentos aprovados antes de codar DAOs |
| Padrao MVC | Todas | Views chamam controllers; controllers chamam DAOs; DAOs usam JDBC |
| `DataBase.properties` | Todas, com Laura liderando | Configuracao unica de URL, usuario e senha do MySQL |
| Roteiro de defesa | Todas | Cada uma sabe explicar sua parte e o fluxo completo |

## Arquitetura Alvo

Fluxo esperado:

```text
View Swing -> Controller -> DAO -> JDBC -> MySQL
```

Estrutura sugerida:

```text
src/
  controller/
    ClienteController.java
    ContaController.java
    OperacaoController.java

  model/
    Cliente.java
    Conta.java
    ContaCorrente.java
    ContaInvestimento.java
    ContaI.java

  model/dao/
    Dao.java
    DaoFactory.java
    DaoType.java
    ConnectionFactory.java
    DataBase.properties
    ClienteDao.java
    ClienteDaoSql.java
    ContaDao.java
    ContaDaoSql.java

  model/tablemodel/
    ClienteTableModel.java

  view/
    Main.java
    TelaManterClientes.java
    TelaCadastroCliente.java
    TelaVincularConta.java
    TelaOperacoesConta.java

  Utils/
    ButtonColumn.java
```

Observacao: se preferirem mexer menos no projeto, `ClienteTableModel` pode continuar em `model`. O importante para a defesa e conseguir explicar o papel dele.

## Modelagem Do Banco

Usar a modelagem mais forte, separando a tabela base `conta` das tabelas especificas `conta_corrente` e `conta_investimento`. Isso combina melhor com a heranca em Java.

```text
cliente
  id
  nome
  sobrenome
  rg
  cpf
  endereco

conta
  id
  numero
  saldo
  tipo
  cliente_id

conta_corrente
  conta_id
  limite

conta_investimento
  conta_id
  montante_minimo
  deposito_minimo
```

Relacionamentos:

- Um cliente pode ter no maximo uma conta.
- Uma conta pertence a um cliente.
- Uma conta corrente tem um registro em `conta` e outro em `conta_corrente`.
- Uma conta investimento tem um registro em `conta` e outro em `conta_investimento`.
- Ao excluir um cliente, a conta deve ser excluida junto por `ON DELETE CASCADE`.

Script base:

```sql
CREATE DATABASE IF NOT EXISTS sistema_bancario;
USE sistema_bancario;

CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sobrenome VARCHAR(100),
    rg VARCHAR(20) NOT NULL UNIQUE,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    endereco VARCHAR(255)
);

CREATE TABLE conta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    saldo DOUBLE NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    cliente_id INT NOT NULL UNIQUE,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

CREATE TABLE conta_corrente (
    conta_id INT PRIMARY KEY,
    limite DOUBLE NOT NULL,
    FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE
);

CREATE TABLE conta_investimento (
    conta_id INT PRIMARY KEY,
    montante_minimo DOUBLE NOT NULL,
    deposito_minimo DOUBLE NOT NULL,
    FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE
);
```

Opcional se sobrar tempo:

```sql
CREATE TABLE movimentacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conta_id INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    valor DOUBLE NOT NULL,
    data_hora DATETIME NOT NULL,
    FOREIGN KEY (conta_id) REFERENCES conta(id) ON DELETE CASCADE
);
```

A tabela `movimentacao` nao e obrigatoria no enunciado. So fazer se o resto estiver pronto.

## DataBase.properties

Usar arquivo de configuracao igual ao estilo do professor, em vez de deixar usuario e senha fixos dentro do Java.

Arquivo:

```text
src/model/dao/DataBase.properties
```

Conteudo:

```properties
db.url=jdbc:mysql://localhost:3306/sistema_bancario
db.user=root
db.pwd=123456
```

`ConnectionFactory` deve ler esse arquivo:

```java
public class ConnectionFactory {
    private static Properties properties;

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException, IOException {
        readProperties();
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pwd = properties.getProperty("db.pwd");
        return DriverManager.getConnection(url, user, pwd);
    }
}
```

Na defesa, explicar: "Se mudar usuario, senha ou banco, nao precisa alterar codigo Java; alteramos apenas o `DataBase.properties`."

## Como Aplicar MVC Na Pratica

Hoje o projeto tem muitas chamadas assim:

```java
RepositorioDados.getInstance().adicionarCliente(cliente);
```

O objetivo e trocar para:

```text
Tela -> Controller -> DAO -> Banco
```

Exemplo:

```java
btnSalvar.addActionListener(e -> {
    controller.salvarCliente(
        txtNome.getText(),
        txtSobrenome.getText(),
        txtRg.getText(),
        txtCpf.getText(),
        txtEndereco.getText()
    );
});
```

O controller cria/valida o objeto e chama o DAO:

```java
public void salvarCliente(String nome, String sobrenome, String rg, String cpf, String endereco) {
    try {
        Cliente cliente = new Cliente(nome, sobrenome, rg, cpf, endereco);
        clienteDao.add(cliente);
        view.apresentaInfo("Cliente salvo com sucesso.");
        listarClientes();
    } catch (Exception e) {
        view.apresentaErro(e.getMessage());
    }
}
```

## Cronograma Por Modulos

### 04/06/2026 - Reuniao De Arquitetura E Divisao

Foco: todo mundo entender o sistema, o exemplo do professor e a divisao por modulos.

| Pessoa | O que fazer |
| --- | --- |
| Laura | Mapear tudo que envolve clientes: `TelaManterClientes`, `TelaCadastroCliente`, `Cliente`, `ClienteTableModel` |
| Nathalia | Mapear tudo que envolve contas: `TelaVincularConta`, `Conta`, `ContaCorrente`, `ContaInvestimento` |
| Dani | Mapear tudo que envolve operacoes: `TelaOperacoesConta`, saque, deposito, saldo e remuneracao |

Passos para quem nao sabe muito Java:

- Procurar `addActionListener`: ali esta o que acontece quando clica em um botao.
- Procurar `RepositorioDados`: ali estao os pontos que ainda usam memoria.
- Abrir o exemplo do professor em `aula07/mvc/.../contato`.
- Comparar `ContatoController` com o controller que vamos criar.
- Comparar `ContatoDaoSql` com os DAOs que vamos criar.

Entregavel do dia:

- Divisao por modulos aprovada.
- Banco definido com quatro tabelas principais.
- Lista de arquivos que cada pessoa vai mexer.

### 05/06/2026 - Banco, DAO Base E Properties

Foco: criar a fundacao comum para todos os modulos.

| Pessoa | O que fazer |
| --- | --- |
| Laura | Criar `model/dao`, `DataBase.properties`, `ConnectionFactory`, `Dao`, `DaoType`, `DaoFactory`; iniciar `ClienteDao` |
| Nathalia | Criar/ajustar `ContaI`; revisar `Conta`, `ContaCorrente`, `ContaInvestimento`; iniciar `ContaDao` |
| Dani | Criar `controller`; iniciar `ClienteController`, `ContaController` e, se fizer sentido, `OperacaoController` |

Como aplicar:

- Copiar a estrutura do exemplo do professor, nao inventar outro padrao.
- Primeiro compilar com classes vazias ou metodos simples.
- So depois colocar SQL.

Teste do dia:

- O projeto deve compilar.
- `ConnectionFactory.getConnection()` deve conseguir conectar no MySQL.
- O `banco.sql` deve criar as tabelas sem erro.

Entregavel do dia:

- `banco.sql` inicial.
- `DataBase.properties`.
- `ConnectionFactory`.
- Interfaces DAO criadas.
- Controllers criados.

### 06/06/2026 - Modulo Clientes: Inserir E Listar

Foco principal: Laura. Dani ajuda na ligacao com tela.

| Pessoa | O que fazer |
| --- | --- |
| Laura | Implementar `ClienteDaoSql.add()` e `ClienteDaoSql.getAll()` usando `PreparedStatement` e `ResultSet` |
| Dani | Alterar `TelaCadastroCliente` e `TelaManterClientes` para chamar `ClienteController` |
| Nathalia | Continuar regras de conta e revisar se `ContaI` bate exatamente com o enunciado |

Como implementar cliente:

1. `TelaCadastroCliente` pega os campos.
2. `ClienteController.salvarCliente(...)` cria o objeto `Cliente`.
3. `ClienteDaoSql.add(cliente)` faz `INSERT`.
4. `ClienteController.listarClientes()` chama `clienteDao.getAll()`.
5. A tela atualiza o `ClienteTableModel`.

Teste do dia:

- Cadastrar cliente.
- Fechar e abrir o sistema.
- Cliente deve continuar aparecendo.

Entregavel do dia:

- Inserir cliente no banco.
- Listar clientes do banco.

### 07/06/2026 - Modulo Clientes: Atualizar, Excluir, Buscar E Ordenar

Foco principal: Laura.

| Pessoa | O que fazer |
| --- | --- |
| Laura | Implementar `update`, `delete`, `getByCpf`, `getByRg`, `getByNome`, `getBySobrenome` |
| Dani | Ajustar botoes de editar, excluir e filtros para chamar controller |
| Nathalia | Testar exclusao de cliente com conta e verificar cascade quando ja houver conta criada |

Como aplicar filtros:

```java
public List<Cliente> getByNome(String nome) throws Exception {
    String sql = "SELECT * FROM cliente WHERE LOWER(nome) LIKE LOWER(?)";
    ...
    stmt.setString(1, "%" + nome + "%");
}
```

Como testar:

- Buscar por parte do nome.
- Buscar por sobrenome.
- Buscar por CPF.
- Buscar por RG.
- Atualizar endereco.
- Excluir cliente com confirmacao.

Entregavel do dia:

- Modulo Cliente praticamente completo.
- Tela de clientes integrada ao banco.

### 08/06/2026 - Modulo Contas: Criar Corrente E Investimento

Foco principal: Nathalia. Laura ajuda com SQL se precisar.

| Pessoa | O que fazer |
| --- | --- |
| Nathalia | Implementar criacao de `ContaCorrente` e `ContaInvestimento`; salvar em `conta` + tabela especifica |
| Laura | Ajudar com `INSERT` em duas tabelas e uso de `Statement.RETURN_GENERATED_KEYS` |
| Dani | Refatorar `TelaVincularConta` para chamar `ContaController` |

Como salvar conta corrente:

1. Inserir dados comuns em `conta`: numero, saldo, tipo, cliente_id.
2. Pegar o `id` gerado da conta.
3. Inserir limite em `conta_corrente` usando `conta_id`.

Como salvar conta investimento:

1. Inserir dados comuns em `conta`.
2. Pegar o `id` gerado.
3. Inserir montante minimo e deposito minimo em `conta_investimento`.

Teste do dia:

- Criar cliente.
- Vincular conta corrente.
- Criar outro cliente.
- Vincular conta investimento.
- Conferir no banco se preencheu `conta` e a tabela filha correta.

Entregavel do dia:

- Contas criadas no banco com modelagem de heranca.
- Cliente bloqueado para nao ter mais de uma conta.

### 09/06/2026 - Modulo Contas: Buscar Por CPF E Regras

Foco principal: Nathalia.

| Pessoa | O que fazer |
| --- | --- |
| Nathalia | Implementar busca de conta por CPF com `JOIN`; montar objeto `ContaCorrente` ou `ContaInvestimento` conforme `tipo` |
| Laura | Revisar SQL dos JOINs e constraints do banco |
| Dani | Preparar `TelaOperacoesConta` para receber a conta buscada pelo controller |

Exemplo de consulta:

```sql
SELECT c.*, cli.cpf, cc.limite, ci.montante_minimo, ci.deposito_minimo
FROM conta c
JOIN cliente cli ON cli.id = c.cliente_id
LEFT JOIN conta_corrente cc ON cc.conta_id = c.id
LEFT JOIN conta_investimento ci ON ci.conta_id = c.id
WHERE cli.cpf = ?;
```

Regras para revisar:

- `Conta.deposita(valor)`: valor deve ser positivo; se invalido, retornar `false`.
- `Conta.saca(valor)`: valor deve ser positivo; se invalido, retornar `false`.
- `ContaCorrente.saca(valor)`: saldo pode ficar negativo ate o limite.
- `ContaCorrente.remunera()`: aplicar 1%.
- `ContaInvestimento.deposita(valor)`: deposito deve ser maior ou igual ao deposito minimo.
- `ContaInvestimento.saca(valor)`: saldo final deve ser maior ou igual ao montante minimo.
- `ContaInvestimento.remunera()`: aplicar 2%.

Entregavel do dia:

- Buscar conta por CPF.
- Regras das contas funcionando.
- Objeto correto criado conforme o tipo da conta.

### 10/06/2026 - Modulo Operacoes E Integracao Geral

Foco principal: Dani. Todas ajudam a integrar.

| Pessoa | O que fazer |
| --- | --- |
| Dani | Implementar fluxo completo de `TelaOperacoesConta`: buscar CPF, sacar, depositar, ver saldo, remunerar e atualizar banco |
| Laura | Garantir que o saldo seja salvo com `UPDATE conta SET saldo=? WHERE id=?` |
| Nathalia | Validar se as regras das subclasses estao sendo chamadas por polimorfismo |

Fluxo correto de operacao:

```text
Tela pega CPF e valor
Controller busca conta pelo CPF
Controller chama conta.saca/deposita/remunera
Se a regra permitir, DAO atualiza saldo no banco
Tela mostra mensagem e saldo atualizado
```

Como explicar na defesa:

- A tela nao faz SQL.
- O controller coordena a operacao.
- A regra fica nas classes de conta.
- O DAO apenas persiste o novo saldo.

Entregavel do dia:

- Sistema integrado de ponta a ponta.
- Clientes, contas e operacoes persistindo no banco.

### 11/06/2026 - Testes, Bugs, Diagramas E README

Foco: simular a defesa e fechar artefatos.

| Pessoa | O que fazer |
| --- | --- |
| Laura | Revisar `banco.sql`, diagrama E-R e testes de cliente |
| Nathalia | Revisar diagrama de classes, heranca e testes de conta |
| Dani | Atualizar README, roteiro de defesa e testes de operacoes/integracao |

Roteiro de teste:

1. Criar banco do zero com `banco.sql`.
2. Configurar `DataBase.properties`.
3. Abrir sistema.
4. Cadastrar cliente.
5. Testar CPF/RG duplicado.
6. Listar e filtrar clientes.
7. Atualizar cliente.
8. Vincular conta corrente.
9. Sacar, depositar, ver saldo e remunerar.
10. Criar outro cliente.
11. Vincular conta investimento.
12. Testar deposito minimo e montante minimo.
13. Excluir cliente com conta.
14. Fechar e abrir o sistema para provar persistencia.

Perguntas que cada uma deve saber responder:

- Laura: Como o CRUD de clientes usa DAO e JDBC?
- Nathalia: Como a heranca de contas foi modelada em Java e no banco?
- Dani: Como as operacoes usam polimorfismo e persistem saldo?

Entregavel do dia:

- Bugs corrigidos.
- README atualizado.
- Diagramas prontos.
- Defesa simulada.

### 12/06/2026 - Fechamento E Entrega

Foco: empacotar e conferir tudo.

| Pessoa | O que fazer |
| --- | --- |
| Laura | Conferir SQL, `DataBase.properties` e demonstracao do modulo clientes |
| Nathalia | Conferir diagramas, contas e gerar/testar JAR |
| Dani | Conferir README, roteiro de defesa, integracao final e arquivos da entrega |

Checklist final:

- [ ] Codigo fonte completo.
- [ ] `banco.sql`.
- [ ] `DataBase.properties`.
- [ ] Diagrama de classes.
- [ ] Diagrama E-R.
- [ ] JAR executavel.
- [ ] README com instrucoes.
- [ ] Sistema testado com banco vazio.
- [ ] Sistema testado fechando e abrindo para provar persistencia.

## Checklist Tecnico Por Modulo

### Modulo Clientes - Laura

- [ ] `ClienteDao`
- [ ] `ClienteDaoSql`
- [ ] `ClienteController`
- [ ] Inserir cliente
- [ ] Atualizar cliente
- [ ] Excluir cliente
- [ ] Listar clientes
- [ ] Buscar por nome
- [ ] Buscar por sobrenome
- [ ] Buscar por RG
- [ ] Buscar por CPF
- [ ] Ordenar por nome
- [ ] Ordenar por sobrenome
- [ ] Ordenar por saldo/salario
- [ ] Excluir cliente removendo conta em cascata

### Modulo Contas - Nathalia

- [ ] `ContaI` conforme enunciado
- [ ] `Conta` implementa `ContaI`
- [ ] `ContaCorrente`
- [ ] `ContaInvestimento`
- [ ] `ContaDao`
- [ ] `ContaDaoSql`
- [ ] `ContaController`
- [ ] Criar conta corrente
- [ ] Criar conta investimento
- [ ] Salvar em `conta`
- [ ] Salvar em `conta_corrente`
- [ ] Salvar em `conta_investimento`
- [ ] Buscar conta por CPF com JOIN
- [ ] Bloquear mais de uma conta por cliente
- [ ] Regras de limite, deposito minimo, montante minimo e remuneracao

### Modulo Operacoes E Integracao - Dani

- [ ] Tela de operacoes busca conta por CPF
- [ ] Saque
- [ ] Deposito
- [ ] Ver saldo
- [ ] Remuneracao
- [ ] Atualizar saldo no banco
- [ ] Tratamento de erro com `JOptionPane`
- [ ] Integracao das telas
- [ ] README
- [ ] Roteiro de defesa

### DAO/JDBC Compartilhado

- [ ] `ConnectionFactory`
- [ ] `DataBase.properties`
- [ ] `Dao`
- [ ] `DaoType`
- [ ] `DaoFactory`
- [ ] `PreparedStatement`
- [ ] `ResultSet`
- [ ] try-with-resources
- [ ] Sem SQL direto nas telas
- [ ] Sem dependencia principal de `RepositorioDados`

## Prioridade Absoluta

Se o tempo apertar, priorizar nesta ordem:

1. Banco com `cliente`, `conta`, `conta_corrente`, `conta_investimento`.
2. `ConnectionFactory` lendo `DataBase.properties`.
3. CRUD de clientes com JDBC.
4. Criacao e busca de contas com JDBC.
5. Operacoes atualizando saldo no banco.
6. MVC visivel com controllers.
7. Script SQL e diagramas.
8. JAR executavel.
9. Opcional: tabela `movimentacao` e extrato.

