package model;

public class ContaInvestimento extends Conta {

    private double montanteMinimo;
    private double depositoMinimo;

    public ContaInvestimento(Cliente cliente, int numero, double depositoInicial,
                             double montanteMinimo, double depositoMinimo) {
        super(cliente, numero, depositoInicial);
        this.montanteMinimo = montanteMinimo;
        this.depositoMinimo = depositoMinimo;

        if (depositoInicial < montanteMinimo) {
            throw new IllegalArgumentException(
                "Depósito inicial deve ser >= montante mínimo de R$ " + montanteMinimo
            );
        }
    }

    @Override
    public boolean deposita(double valor) {

        if (valor < depositoMinimo) {
            System.out.println("Depósito invalido: O valor deve ser >= depósito mínimo.");
            return false;
        }

        return super.deposita(valor);
    }

    @Override
    public boolean saca(double valor) {

        if (valor <= 0) {
            System.out.println("Saque invalido: O valor deve ser positivo.");
            return false;
        }

        if ((saldo - valor) < montanteMinimo) {
            System.out.println("Saque invalido: Saldo insuficiente.");
            return false;
        }

        boolean deucerto = super.saca(valor);

        if (deucerto) {
            System.out.println("Saque realizado com sucesso na conta investimento.");
        }

        return deucerto;
    }

    @Override
    public void remunera() {
        saldo += saldo * 0.02;
    }

    public double getMontanteMinimo() { return montanteMinimo; }
    public void setMontanteMinimo(double montanteMinimo) { this.montanteMinimo = montanteMinimo; }

    public double getDepositoMinimo() { return depositoMinimo; }
    public void setDepositoMinimo(double depositoMinimo) { this.depositoMinimo = depositoMinimo; }
}
