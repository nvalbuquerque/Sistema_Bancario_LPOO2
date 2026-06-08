# Sistema Bancario - Java Swing, JDBC e MVC

Sistema desktop em Java Swing para gerenciamento de clientes e contas bancarias. O projeto aplica programacao orientada a objetos, heranca, polimorfismo, JDBC, DAO e separacao em camadas MVC.

## Funcionalidades

### Clientes

- Incluir, atualizar, excluir e listar clientes.
- Listagem com `AbstractTableModel`.
- Busca por nome, sobrenome, CPF e RG.
- Ordenacao na tabela por nome, sobrenome, RG, CPF, endereco, saldo e tipo de conta.
- Exclusao com confirmacao, avisando que contas vinculadas tambem serao removidas.
- Persistencia dos clientes em banco MySQL via `ClienteDaoSql`.

### Vinculo de Conta

- Vincular um cliente a uma unica conta.
- Tipos disponiveis:
  - Conta Corrente
  - Conta Investimento
- Campos da Conta Corrente:
  - Deposito inicial
  - Limite
- Campos da Conta Investimento:
  - Montante minimo
  - Deposito minimo
  - Deposito inicial
- Numero da conta gerado automaticamente de forma sequencial.
- Persistencia das contas em banco MySQL via `ContaDaoSql`.

### Operacoes de Conta

- Buscar conta pelo CPF do cliente.
- Sacar.
- Depositar.
- Ver saldo.
- Remunerar conta.
- Regras de remuneracao:
  - Conta Corrente: 1%
  - Conta Investimento: 2%
- As operacoes atualizam o saldo e persistem no banco.

## Estrutura

```text
Sistema Bancario/
  lib/
    mysql-connector-j-9.7.0.jar
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
      ContaInterface.java
      ClienteTableModel.java
      ClienteSalarioComparator.java
      dao/
        ClienteDao.java
        ClienteDaoSql.java
        ContaDao.java
        ContaDaoSql.java
        ConnectionFactory.java
        DataFactory.java
        DatabaseSeeder.java
        DataBase.properties
        Dao.java
        DaoType.java
    view/
      Main.java
      TelaManterClientes.java
      TelaCadastroCliente.java
      TelaVincularConta.java
      TelaOperacoesConta.java
    Utils/
      ButtonColumn.java
    schema.sql
    data.sql
```

## MVC

- `model`: classes de dominio, regras de negocio, `TableModel` e DAOs.
- `view`: telas Swing.
- `controller`: camada entre telas e DAOs/regras de operacao.

As telas chamam controllers em vez de acessar diretamente o banco. Exemplos:

- `TelaCadastroCliente` usa `ClienteController`.
- `TelaManterClientes` usa `ClienteController`, `ContaController` e `OperacaoController`.
- `TelaVincularConta` recebe `ContaController` pelo construtor.
- `TelaOperacoesConta` recebe `ContaController` e `OperacaoController`.

## Banco de Dados

O projeto usa MySQL. Antes de executar, crie o banco:

```sql
CREATE DATABASE sistema_bancario_LPOOII;
```

Configure o arquivo:

```text
Sistema Bancario/src/model/dao/DataBase.properties
```

Exemplo:

```properties
db.url=jdbc:mysql://localhost:3306/sistema_bancario_LPOOII
db.user=root
db.pwd=sua_senha
```

Ao iniciar, o sistema executa `schema.sql` e `data.sql` por meio de `DatabaseSeeder`.

Caso o banco tenha sido criado com uma versao antiga do schema, recrie as tabelas:

```sql
USE sistema_bancario_LPOOII;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS CONTA_INVESTIMENTO;
DROP TABLE IF EXISTS CONTA_CORRENTE;
DROP TABLE IF EXISTS CONTA;
DROP TABLE IF EXISTS CLIENTE;

SET FOREIGN_KEY_CHECKS = 1;
```

Depois rode o sistema novamente para recriar as tabelas.

## Como Compilar

No PowerShell, dentro da pasta `Sistema Bancario`:

```powershell
javac -encoding UTF-8 -d out src\model\*.java src\model\dao\*.java src\Utils\*.java src\view\*.java src\controller\*.java
```

## Como Executar

Ainda dentro de `Sistema Bancario`:

```powershell
java -cp "out;lib\mysql-connector-j-9.7.0.jar;src" view.Main
```

No Linux/macOS, use `:` no classpath:

```bash
java -cp "out:lib/mysql-connector-j-9.7.0.jar:src" view.Main
```

## Testes Manuais Sugeridos

### Clientes

- Cadastrar cliente.
- Editar nome, sobrenome, RG e endereco.
- Buscar por nome, sobrenome, CPF e RG.
- Excluir cliente e confirmar que contas vinculadas foram apagadas.

### Contas

- Vincular Conta Corrente a um cliente sem conta.
- Vincular Conta Investimento a outro cliente sem conta.
- Tentar vincular uma segunda conta ao mesmo cliente.
- Fechar e abrir o sistema para verificar persistencia.

### Operacoes

- Buscar CPF sem conta.
- Buscar CPF com Conta Corrente.
- Buscar CPF com Conta Investimento.
- Deposito valido.
- Deposito invalido/negativo.
- Saque valido.
- Saque acima do limite.
- Remuneracao de Conta Corrente.
- Remuneracao de Conta Investimento.
- Fechar e abrir para conferir se o saldo persistiu.

## Itens de Entrega

- Codigo fonte Java.
- Script de criacao do banco: `Sistema Bancario/src/schema.sql`.
- Script de dados iniciais: `Sistema Bancario/src/data.sql`.
- Diagrama de classes:
  - `Diagrama de Classes.asta`
  - `Diagrama de Classes - Sistema Bancario.pdf`
- Arquivo `.jar` executavel: pendente de gerar.
- Diagrama E-R: verificar se sera entregue em arquivo separado.

## Integrantes

- Daniela Tamy Yuki
- Laura Klemba Cordeiro
- Nathalia Lyra Varela de Albuquerque
