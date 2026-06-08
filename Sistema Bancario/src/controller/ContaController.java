package controller;


import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaInvestimento;
import model.dao.ContaDao;
import model.dao.ContaDaoSql;

public class ContaController {
    private ContaDao contaDao;

    public ContaController() {
        this.contaDao = ContaDaoSql.getContaDaoSql();
    }

    public ContaCorrente criarContaCorrente(Cliente cliente, double depositoInicial, double limite) throws Exception {
        int numero = contaDao.gerarProximoNumero();
        ContaCorrente conta = new ContaCorrente(cliente, numero, depositoInicial, limite);
        contaDao.add(conta);
        cliente.setConta(conta);
        return conta;
    }

    public ContaInvestimento criarContaInvestimento(Cliente cliente, double depositoInicial,
        double montanteMinimo, double depositoMinimo) throws Exception {
        int numero = contaDao.gerarProximoNumero();
        ContaInvestimento conta = new ContaInvestimento(cliente, numero, depositoInicial, montanteMinimo, depositoMinimo);
        contaDao.add(conta);
        cliente.setConta(conta);
        return conta;
    }

    public Conta buscarContaPorCpf(String cpf) throws Exception {
        return contaDao.getByCpf(cpf);
    }

    public boolean sacar(Conta conta, double valor) throws Exception {
        if (conta.saca(valor)) {
            contaDao.updateSaldo(conta);
            return true;
        }
        return false;
    }

    public boolean depositar(Conta conta, double valor) throws Exception {
        if (conta.deposita(valor)) {
            contaDao.updateSaldo(conta);
            return true;
        }
        return false;
    }

    public void remunerar(Conta conta) throws Exception {
        conta.remunera();
        contaDao.updateSaldo(conta);
    }

    public void atualizarSaldo(Conta conta) throws Exception {
        contaDao.updateSaldo(conta);
    }
}
