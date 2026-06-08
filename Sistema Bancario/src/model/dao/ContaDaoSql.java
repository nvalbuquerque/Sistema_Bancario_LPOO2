package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLIntegrityConstraintViolationException;

import model.Conta;
import model.Cliente;
import model.ContaCorrente;
import model.ContaInvestimento;

public class ContaDaoSql implements ContaDao {

    private static ContaDaoSql dao;

    private final String insertConta =
    "INSERT INTO CONTA(numero, saldo, tipo, cliente_id) VALUES (?, ?, ?, ?)";

    private final String insertCorrente =
    "INSERT INTO CONTA_CORRENTE(conta_id, limite) VALUES (?, ?)";

    private final String insertInvestimento =
    "INSERT INTO CONTA_INVESTIMENTO(conta_id, montante_minimo, deposito_minimo) VALUES (?, ?, ?)";

    private ContaDaoSql() {
    }

    public static ContaDaoSql getContaDaoSql() {
        if (dao == null)
            dao = new ContaDaoSql();
        return dao;
    }

   @Override
    public void add(Conta conta) throws Exception {

    Connection conn = null;

    try {
        conn = ConnectionFactory.getConnection();
        conn.setAutoCommit(false);

        int contaId;
        try (
            PreparedStatement stmtConta =
                conn.prepareStatement(
                    insertConta,
                    Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmtConta.setInt(1, conta.getNumero());
            stmtConta.setDouble(2, conta.getSaldo());
            if (conta instanceof ContaCorrente) {
                stmtConta.setString(3, "CORRENTE");
            } else {
                stmtConta.setString(3, "INVESTIMENTO");
            }

            stmtConta.setInt(4, conta.getDono().getId());
            stmtConta.executeUpdate();

            try (ResultSet rs = stmtConta.getGeneratedKeys()) {
                rs.next();
                contaId = rs.getInt(1);
            }
        }

        if (conta instanceof ContaCorrente cc) {
            try (
                PreparedStatement stmtCorrente =
                    conn.prepareStatement(insertCorrente)
            ) {
                stmtCorrente.setInt(1, contaId);
                stmtCorrente.setDouble(2, cc.getLimite());
                stmtCorrente.executeUpdate();
            }
        }

        if (conta instanceof ContaInvestimento ci) {
            try (
                PreparedStatement stmtInvestimento =
                    conn.prepareStatement(insertInvestimento)
            ) {
                stmtInvestimento.setInt(1, contaId);
                stmtInvestimento.setDouble(
                    2,
                    ci.getMontanteMinimo()
                );
                stmtInvestimento.setDouble(
                    3,
                    ci.getDepositoMinimo()
                );
                stmtInvestimento.executeUpdate();
            }
        }
        conn.commit();
    } catch (SQLIntegrityConstraintViolationException e) {

        if (conn != null) {
            conn.rollback();
        }

        throw new Exception(
            "Número de conta já existe. Tente novamente.",
            e
        );
    } catch (Exception e) {

        if (conn != null) {
            conn.rollback();
        }
        throw e;
    } finally {

        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}

    @Override
    public List<Conta> getAll() throws Exception {

    List<Conta> contas = new ArrayList<>();

    String sql =
        "SELECT c.*, cli.*, cc.limite, " +
        "ci.montante_minimo, ci.deposito_minimo " +
        "FROM CONTA c " +
        "JOIN CLIENTE cli ON cli.id = c.cliente_id " +
        "LEFT JOIN CONTA_CORRENTE cc ON cc.conta_id = c.id " +
        "LEFT JOIN CONTA_INVESTIMENTO ci ON ci.conta_id = c.id";

    try (
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {
        while (rs.next()) {
            Cliente cliente =
                new Cliente(
                    rs.getInt("cliente_id"),
                    rs.getString("nome"),
                    rs.getString("sobrenome"),
                    rs.getString("rg"),
                    rs.getString("cpf"),
                    rs.getString("endereco")
                );

            String tipo = rs.getString("tipo");

            if ("CORRENTE".equalsIgnoreCase(tipo)) {
                contas.add(criarContaCorrente(cliente, rs));
            }
            else if ("INVESTIMENTO".equalsIgnoreCase(tipo)) {
                contas.add(criarContaInvestimento(cliente, rs));
            }
        }
    }
    return contas;
}

    @Override
    public Conta getById(int id) throws Exception {

    String sql =
        "SELECT c.*, cli.*, cc.limite, " +
        "ci.montante_minimo, ci.deposito_minimo " +
        "FROM CONTA c " +
        "JOIN CLIENTE cli ON cli.id = c.cliente_id " +
        "LEFT JOIN CONTA_CORRENTE cc ON cc.conta_id = c.id " +
        "LEFT JOIN CONTA_INVESTIMENTO ci ON ci.conta_id = c.id " +
        "WHERE c.id = ?";

    try (
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
        stmt.setInt(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            Cliente cliente =
                new Cliente(
                    rs.getInt("cliente_id"),
                    rs.getString("nome"),
                    rs.getString("sobrenome"),
                    rs.getString("rg"),
                    rs.getString("cpf"),
                    rs.getString("endereco")
                );

            String tipo = rs.getString("tipo");

            if ("CORRENTE".equalsIgnoreCase(tipo)) {
                return criarContaCorrente(cliente, rs);
            }
            return criarContaInvestimento(cliente, rs);
        }
    }
}

    @Override
    public void update(Conta conta) throws Exception {

    String sqlConta =
        "UPDATE CONTA " +
        "SET numero = ?, saldo = ? " +
        "WHERE numero = ?";

    try (
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt =
            conn.prepareStatement(sqlConta)
    ) {
        stmt.setInt(1, conta.getNumero());
        stmt.setDouble(2, conta.getSaldo());
        stmt.setInt(3, conta.getNumero());
        stmt.executeUpdate();
    }
    if (conta instanceof ContaCorrente cc) {

        String sqlCorrente =
            "UPDATE CONTA_CORRENTE cc " +
            "JOIN CONTA c ON c.id = cc.conta_id " +
            "SET cc.limite = ? " +
            "WHERE c.numero = ?";

        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt =
                conn.prepareStatement(sqlCorrente)
        ) {
            stmt.setDouble(1, cc.getLimite());
            stmt.setInt(2, cc.getNumero());
            stmt.executeUpdate();
        }
    }

    if (conta instanceof ContaInvestimento ci) {

        String sqlInvestimento =
            "UPDATE CONTA_INVESTIMENTO ci " +
            "JOIN CONTA c ON c.id = ci.conta_id " +
            "SET ci.montante_minimo = ?, " +
            "ci.deposito_minimo = ? " +
            "WHERE c.numero = ?";

        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt =
                conn.prepareStatement(sqlInvestimento)
        ) {
            stmt.setDouble(1,
                ci.getMontanteMinimo());
            stmt.setDouble(2,
                ci.getDepositoMinimo());
            stmt.setInt(3,
                ci.getNumero());
            stmt.executeUpdate();
        }
    }
}

    @Override
    public void delete(Conta conta) throws Exception {

    String sql =
        "DELETE FROM CONTA WHERE numero = ?";

    try (
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt =
            conn.prepareStatement(sql)
    ) {
        stmt.setInt(
            1,
            conta.getNumero()
        );
        stmt.executeUpdate();
    }
}

    @Override
    public void deleteAll() throws Exception {

        String sql =
        "DELETE FROM CONTA";

    try (
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt =
            conn.prepareStatement(sql)
    ) {
        stmt.executeUpdate();
    }
}

    @Override
    public Conta getByCpf(String cpf) throws Exception {

    String sql =
        "SELECT c.*, cli.*, cc.limite, " +
        "ci.montante_minimo, ci.deposito_minimo " +
        "FROM CONTA c " +
        "JOIN CLIENTE cli ON cli.id = c.cliente_id " +
        "LEFT JOIN CONTA_CORRENTE cc ON cc.conta_id = c.id " +
        "LEFT JOIN CONTA_INVESTIMENTO ci ON ci.conta_id = c.id " +
        "WHERE cli.cpf = ?";

    try (
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setString(1, cpf);

        try (ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) {
                return null;
            }

            Cliente cliente =
                new Cliente(
                    rs.getInt("cliente_id"),
                    rs.getString("nome"),
                    rs.getString("sobrenome"),
                    rs.getString("rg"),
                    rs.getString("cpf"),
                    rs.getString("endereco")
                );

            String tipo = rs.getString("tipo");

            if ("CORRENTE".equalsIgnoreCase(tipo)) {

                return criarContaCorrente(cliente, rs);
            }
            if ("INVESTIMENTO".equalsIgnoreCase(tipo)) {

                return criarContaInvestimento(cliente, rs);
            }
        }
    }
    return null;
}

    @Override
    public void updateSaldo(Conta conta) throws Exception {

        String sql = "UPDATE CONTA SET saldo = ? WHERE numero = ?";
        
        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setDouble(1, conta.getSaldo());
            stmt.setInt(2, conta.getNumero());
            stmt.executeUpdate();
        }

    }

    @Override
    public int gerarProximoNumero() throws Exception {

    String sql =
        "SELECT MAX(CAST(numero AS UNSIGNED)) AS ultimo FROM CONTA";

    try (
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {

        if (rs.next()) {
            return rs.getInt("ultimo") + 1;
        }

        return 1;
        }
    }

    private ContaCorrente criarContaCorrente(Cliente cliente, ResultSet rs) throws Exception {
        double saldo = rs.getDouble("saldo");
        ContaCorrente conta = new ContaCorrente(
            cliente,
            rs.getInt("numero"),
            Math.max(0, saldo),
            rs.getDouble("limite")
        );
        conta.setSaldo(saldo);
        return conta;
    }

    private ContaInvestimento criarContaInvestimento(Cliente cliente, ResultSet rs) throws Exception {
        return new ContaInvestimento(
            cliente,
            rs.getInt("numero"),
            rs.getDouble("saldo"),
            rs.getDouble("montante_minimo"),
            rs.getDouble("deposito_minimo")
        );
    }
}
