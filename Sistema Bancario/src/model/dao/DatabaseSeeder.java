package model.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void inicializarBanco() {
        String caminhoSchema = "./Sistema Bancario/src/schema.sql";
        String caminhoData = "./Sistema Bancario/src/data.sql"; 


        try (Connection connection = ConnectionFactory.getConnection();
             Statement stmt = connection.createStatement()) {

            executarScriptSql(stmt, caminhoSchema);
            System.out.println("Tabelas criadas com sucesso.");

            executarScriptSql(stmt, caminhoData);
            System.out.println("Dados  populados com sucesso.");

        } catch (SQLException | IOException e) {
            System.err.println("Falha ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executarScriptSql(Statement stmt, String caminhoArquivo) throws IOException, SQLException {
        String conteudoCompleto = new String(Files.readAllBytes(Paths.get(caminhoArquivo)));

        String[] comandos = conteudoCompleto.split(";");

        for (String comando : comandos) {
            String comandoLimpo = comando.trim();
            if (!comandoLimpo.isEmpty()) {
                stmt.addBatch(comandoLimpo);
            }
        }
        stmt.executeBatch();
    }
}
