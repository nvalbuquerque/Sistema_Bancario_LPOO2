package model.dao;

import model.Cliente;

public class DataFactory {
    private DataFactory(){
    }
    
    public static Dao<Cliente> getClienteDao(DaoType type){
        switch(type){
            case SQL: 
                return ClienteDaoSql.getClienteDaoSql();
            default:
                throw new RuntimeException("Tipo não existe:"+type);
        }
    }
}
