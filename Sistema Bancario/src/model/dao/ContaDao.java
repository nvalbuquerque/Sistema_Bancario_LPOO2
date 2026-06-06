package model.dao;

import model.Conta;

public interface ContaDao extends Dao<Conta> {

    Conta getByCpf(String cpf) throws Exception;

    void updateSaldo(Conta conta) throws Exception;

    int gerarProximoNumero() throws Exception;
    
}
