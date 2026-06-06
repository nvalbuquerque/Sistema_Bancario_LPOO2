package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Conta;
import model.Cliente;
import model.ContaCorrente;
import model.ContaInvestimento;

public class ContaDaoSql implements ContaDao {

    private static ContaDaoSql dao;

    private final String insertConta =
    "INSERT INTO conta(numero, saldo, tipo, cliente_id) VALUES (?, ?, ?, ?)";

    private final String insertCorrente =
    "INSERT INTO conta_corrente(conta_id, limite) VALUES (?, ?)";

    private final String insertInvestimento =
    "INSERT INTO conta_investimento(conta_id, montante_minimo, deposito_minimo) VALUES (?, ?, ?)";

    private ContaDaoSql() {
    }

    public static ContaDaoSql getContaDaoSql() {
        if (dao == null)
            dao = new ContaDaoSql();
        return dao;
    }

   @Override
    public void add(Conta conta) throws Exception {

    try (
        Connection conn = ConnectionFactory.getConnection();

        PreparedStatement stmtConta =
            conn.prepareStatement(
                insertConta,
                Statement.RETURN_GENERATED_KEYS
            );
    ) {
        stmtConta.setInt(1, conta.getNumero());
        stmtConta.setDouble(2, conta.getSaldo());

        if (conta instanceof ContaCorrente) {
            stmtConta.setString(3, "CORRENTE");
        } else {
            stmtConta.setString(3, "INVESTIMENTO");
        }
        stmtConta.setInt(
            4,
            conta.getDono().getId()
        );
        stmtConta.executeUpdate();

        int contaId;
        try (ResultSet rs =
                 stmtConta.getGeneratedKeys()) {

            rs.next();
            contaId = rs.getInt(1);
        }
        if (conta instanceof ContaCorrente cc) {
            try (
                PreparedStatement stmtCorrente =
                    conn.prepareStatement(
                        insertCorrente
                    )
            ) {
                stmtCorrente.setInt(1, contaId);
                stmtCorrente.setDouble(
                    2,
                    cc.getLimite()
                );
                stmtCorrente.executeUpdate();
            }
        }
        if (conta instanceof ContaInvestimento ci) {
            try (
                PreparedStatement stmtInvestimento =
                    conn.prepareStatement(
                        insertInvestimento
                    )
            ) {
                stmtInvestimento.setInt(
                    1,
                    contaId
                );
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
    }
}

    @Override
    public List<Conta> getAll() throws Exception {

    List<Conta> contas = new ArrayList<>();

    String sql =
        "SELECT c.*, cli.*, cc.limite, " +
        "ci.montante_minimo, ci.deposito_minimo " +
        "FROM conta c " +
        "JOIN cliente cli ON cli.id = c.cliente_id " +
        "LEFT JOIN conta_corrente cc ON cc.conta_id = c.id " +
        "LEFT JOIN conta_investimento ci ON ci.conta_id = c.id";

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
                contas.add(
                    new ContaCorrente(
                        cliente,
                        rs.getInt("numero"),
                        rs.getDouble("saldo"),
                        rs.getDouble("limite")
                    )
                );
            }
            else if ("INVESTIMENTO".equalsIgnoreCase(tipo)) {
                contas.add(
                    new ContaInvestimento(
                        cliente,
                        rs.getInt("numero"),
                        rs.getDouble("saldo"),
                        rs.getDouble("montante_minimo"),
                        rs.getDouble("deposito_minimo")
                    )
                );
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
        "FROM conta c " +
        "JOIN cliente cli ON cli.id = c.cliente_id " +
        "LEFT JOIN conta_corrente cc ON cc.conta_id = c.id " +
        "LEFT JOIN conta_investimento ci ON ci.conta_id = c.id " +
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
                return new ContaCorrente(
                    cliente,
                    rs.getInt("numero"),
                    rs.getDouble("saldo"),
                    rs.getDouble("limite")
                );
            }
            return new ContaInvestimento(
                cliente,
                rs.getInt("numero"),
                rs.getDouble("saldo"),
                rs.getDouble("montante_minimo"),
                rs.getDouble("deposito_minimo")
            );
        }
    }
}

    @Override
    public void update(Conta conta) throws Exception {

    String sqlConta =
        "UPDATE conta " +
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
            "UPDATE conta_corrente cc " +
            "JOIN conta c ON c.id = cc.conta_id " +
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
            "UPDATE conta_investimento ci " +
            "JOIN conta c ON c.id = ci.conta_id " +
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
        "DELETE FROM conta WHERE numero = ?";

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
        "DELETE FROM conta";

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
        "FROM conta c " +
        "JOIN cliente cli ON cli.id = c.cliente_id " +
        "LEFT JOIN conta_corrente cc ON cc.conta_id = c.id " +
        "LEFT JOIN conta_investimento ci ON ci.conta_id = c.id " +
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

                return new ContaCorrente(
                    cliente,
                    rs.getInt("numero"),
                    rs.getDouble("saldo"),
                    rs.getDouble("limite")
                );
            }
            if ("INVESTIMENTO".equalsIgnoreCase(tipo)) {

                return new ContaInvestimento(
                    cliente,
                    rs.getInt("numero"),
                    rs.getDouble("saldo"),
                    rs.getDouble("montante_minimo"),
                    rs.getDouble("deposito_minimo")
                );
            }
        }
    }
    return null;
}

    @Override
    public void updateSaldo(Conta conta) throws Exception {

        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";
        
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
        "SELECT MAX(CAST(numero AS UNSIGNED)) AS ultimo FROM conta";

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
}