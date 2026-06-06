package model;

public class ContaCorrente extends Conta {

    private double limite;

    public ContaCorrente(Cliente cliente, int numero, double depositoInicial, double limite) {

        super(cliente, numero, depositoInicial);

        if (limite < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }

        this.limite = limite;
    }

    @Override
    public boolean saca(double valor) {

        if (valor <= 0) {
            System.out.println("Saque inválido: valor deve ser positivo.");
            return false;
        }

        if (saldo - valor < -limite) {
            System.out.println("Saque negado: ultrapassa o limite da conta corrente.");
            return false;
        }

        saldo -= valor;
        System.out.println("Saque realizado com sucesso. Saldo atual: " + saldo);
        return true;
    }

    @Override
    public void remunera() {

        if (saldo > 0) {
            saldo += saldo * 0.01;
            System.out.println("Remuneração de 1% aplicada. Saldo atual: " + saldo);
        } else {
            System.out.println("Remuneração não aplicada: saldo não positivo.");
        }
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {

        if (limite < 0) {
            System.out.println("Limite inválido.");
            return;
        }

        this.limite = limite;
    }
}