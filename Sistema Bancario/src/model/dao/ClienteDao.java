package model.dao;

import model.Cliente;
import java.util.List;


public interface ClienteDao extends Dao<Cliente>{
    public void delete(List<Cliente> lista) throws Exception;

    Cliente getByCpf(String cpf) throws Exception;   
    
    Cliente getByRg(String rg) throws Exception;

    Cliente getByNome(String nome) throws Exception;

    Cliente getBySobrenome(String sobrenome) throws Exception;
}
