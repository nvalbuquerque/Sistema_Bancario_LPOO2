package model;

public abstract class Conta implements ContaInterface {

    protected Cliente dono;
    protected int numero;
    protected double saldo;

    public Conta(Cliente dono, int numero, double depositoInicial) {
        if (depositoInicial < 0) {
            throw new IllegalArgumentException("Depósito inicial não pode ser negativo."); 
        }
        this.dono = dono;
        this.numero = numero;
        this.saldo = depositoInicial;
    }

    @Override
    public boolean deposita(double valor) {
        if (valor <= 0) {
            System.out.println("Depósito inválido: O valor deve ser positivo.");
            return false; 
        }
        saldo += valor;
        System.out.println("Depósito realizado com sucesso. Novo saldo: R$ " + saldo);
        return true;
    }

    @Override
    public boolean saca(double valor) {
        if (valor <= 0) {
            System.out.println("Saque inválido: O valor deve ser positivo.");
            return false; 
        }

        if (saldo < valor) {
            System.out.println("Saque inválido: Saldo insuficiente.");
            return false;
        }

        saldo -= valor;
        System.out.println("Saque realizado com sucesso. Novo saldo: R$ " + saldo);
        return true;
    }

    @Override
    public Cliente getDono() {
        return dono;
    }

    @Override
    public int getNumero() {
        return numero;
    }

    @Override
    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) { 
    this.saldo = saldo;
    }
}
