package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Cliente;

public class ClienteDaoSql implements ClienteDao {

    private final String insert = "INSERT INTO CLIENTE (nome, sobrenome, rg, cpf, endereco, data_cadastro) VALUES (?, ?, ?, ?, ?, ?)";
    private final String select = "SELECT * FROM CLIENTE";
    private final String update = "UPDATE CLIENTE SET nome=?, sobrenome=?, rg=?, cpf=?, endereco=? WHERE id=?";
    private final String delete = "DELETE FROM CLIENTE WHERE id=?";

    private ClienteDaoSql(){
    }

    private static ClienteDaoSql dao;

    public static ClienteDaoSql getClienteDaoSql(){
        if(dao==null)
            return dao = new ClienteDaoSql();
        else
            return dao;
    }  
    
    @Override
    public void add(Cliente cliente) throws Exception {        
        try (Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmtAdiciona = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            
            stmtAdiciona.setString(1, cliente.getNome());
            stmtAdiciona.setString(2, cliente.getSobrenome());
            stmtAdiciona.setString(3, cliente.getRg());
            stmtAdiciona.setString(4, cliente.getCpf());
            stmtAdiciona.setString(5, cliente.getEndereco());
            stmtAdiciona.setTimestamp(6, Timestamp.valueOf(cliente.getDataCadastro()));
        
            stmtAdiciona.execute();
            
            try (ResultSet rsChavesGeradas = stmtAdiciona.getGeneratedKeys()) {
                if (rsChavesGeradas.next()) {
                    int idGerado = rsChavesGeradas.getInt(1);
                    cliente.setId(idGerado);
                }
            }
        }
    }

    @Override
    public List<Cliente> getAll() throws Exception{
        try (Connection connection=ConnectionFactory.getConnection();
             PreparedStatement stmtLista = connection.prepareStatement(select);
             ResultSet rs = stmtLista.executeQuery();   
            ){
            List<Cliente> cliente = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String sobrenome = rs.getString("sobrenome");
                String rg = rs.getString("rg");
                String cpf = rs.getString("cpf");
                String endereco = rs.getString("endereco");

                cliente.add(new Cliente(id, nome, sobrenome, rg, cpf, endereco));
            }  
            return cliente;
        }
    }

    @Override
    public void update(Cliente cliente) throws Exception{
        try(Connection connection=ConnectionFactory.getConnection();
            PreparedStatement stmtAtualiza = connection.prepareStatement(update);
            ){

            stmtAtualiza.setString(1, cliente.getNome());
            stmtAtualiza.setString(2, cliente.getSobrenome());
            stmtAtualiza.setString(3, cliente.getRg());
            stmtAtualiza.setString(4, cliente.getCpf());
            stmtAtualiza.setString(5, cliente.getEndereco());
            stmtAtualiza.executeUpdate();
        } 
    }

    @Override
    public void delete(Cliente cliente) throws Exception {
        
        try (Connection connection=ConnectionFactory.getConnection();
             PreparedStatement stmtExcluir = connection.prepareStatement(delete);
            ){
            stmtExcluir.setInt(1, cliente.getId());
            stmtExcluir.executeUpdate();
        }
    }  
    
    @Override
    public void delete(List<Cliente> lista) throws Exception {
        for (Cliente cliente : lista) {
            delete(cliente); 
        }
    }

    @Override
    public void deleteAll() throws Exception {
        String sqlDeleteAll = "DELETE FROM CLIENTE";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlDeleteAll)) {
            stmt.executeUpdate();
        }
    }

    @Override
    public Cliente getById(int id) throws Exception { 
        String sqlPorId = "SELECT * FROM CLIENTE WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sqlPorId)) {
            
            stmt.setLong(1, id); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Cliente getByCpf(String cpf) throws Exception { 
        String sqlPorCpf = "SELECT * FROM CLIENTE WHERE cpf = ?";
        try (Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sqlPorCpf)) {
            
            stmt.setString(1, cpf); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Cliente getByRg(String rg) throws Exception { 
        String sqlPorRg = "SELECT * FROM CLIENTE WHERE rg = ?";
        try (Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sqlPorRg)) {
            
            stmt.setString(1, rg); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Cliente getByNome(String nome) throws Exception { 
        String sqlPorNome = "SELECT * FROM CLIENTE WHERE nome = ?";
        try (Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sqlPorNome)) {
            
            stmt.setString(1, nome); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Cliente getBySobrenome(String sobrenome) throws Exception { 
        String sqlPorSobrenome = "SELECT * FROM CLIENTE WHERE sobrenome = ?";
        try (Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sqlPorSobrenome)) {
            
            stmt.setString(1, sobrenome); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                    );
                }
            }
        }
        return null;
    }

}
