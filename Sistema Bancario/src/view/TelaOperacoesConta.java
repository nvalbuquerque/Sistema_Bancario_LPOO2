package view;

import model.Conta;
import model.ContaCorrente;
import controller.ContaController; 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class TelaOperacoesConta extends JDialog {

    private Conta conta;
    private ContaController contaController; 
    private JLabel lblSaldoAtual;
    private JTextField txtValor;
    private final DecimalFormat df = new DecimalFormat("R$ #,##0.00");

    public TelaOperacoesConta(Frame owner, Conta conta) {
        super(owner, true);
        this.conta = conta;
        this.contaController = new ContaController(); 

        setTitle("Operações da Conta: " + conta.getNumero() + " (" + conta.getDono().getNome() + ")");
        setBounds(250, 250, 400, 300);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel painelCentro = new JPanel(new GridLayout(3, 1, 5, 5));

        lblSaldoAtual = new JLabel();
        painelCentro.add(lblSaldoAtual);

        painelCentro.add(new JLabel("Valor da Operação (R$):"));
        txtValor = new JTextField(10);
        painelCentro.add(txtValor);

        atualizarSaldoVisual();
        add(painelCentro, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton btnSaque = new JButton("Sacar");
        JButton btnDeposito = new JButton("Depositar");
        JButton btnVerSaldo = new JButton("Ver Saldo");
        JButton btnRemunera = new JButton("Remunerar");

        btnSaque.addActionListener(e -> efetuarSaque());
        btnDeposito.addActionListener(e -> efetuarDeposito());
        btnVerSaldo.addActionListener(e -> mostrarSaldo());
        btnRemunera.addActionListener(e -> remunerarConta());

        painelBotoes.add(btnSaque);
        painelBotoes.add(btnDeposito);
        painelBotoes.add(btnVerSaldo);
        painelBotoes.add(btnRemunera);

        add(painelBotoes, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        add(btnFechar, BorderLayout.SOUTH);
    }

    private void atualizarSaldoVisual() {
        lblSaldoAtual.setText("Saldo Atual: " + df.format(conta.getSaldo()));
    }

    private void mostrarSaldo() {
        JOptionPane.showMessageDialog(this,
                "Saldo atual na conta " + conta.getNumero() + ":\n" + df.format(conta.getSaldo()),
                "Ver Saldo", JOptionPane.INFORMATION_MESSAGE);
    }

    private double getValorDigitado() {
        try {
            return Double.parseDouble(txtValor.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Insira um valor numérico válido.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void efetuarSaque() {
        double valor = getValorDigitado();
        if (valor == -1) return;

        try {
            boolean sucesso = contaController.sacar(conta, valor);

            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                        "Saque de " + df.format(valor) + " efetuado com sucesso!",
                        "Saque", JOptionPane.INFORMATION_MESSAGE);
                atualizarSaldoVisual();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Saque não permitido. Verifique as regras e os limites da sua conta.",
                        "Operação Negada",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro de comunicação com o banco de dados: " + ex.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE);
        }

        txtValor.setText("");
    }

    private void efetuarDeposito() {
        double valor = getValorDigitado();
        if (valor == -1) return;

        try {
            boolean sucesso = contaController.depositar(conta, valor);

            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                        "Depósito de " + df.format(valor) + " efetuado com sucesso!",
                        "Depósito", JOptionPane.INFORMATION_MESSAGE);
                atualizarSaldoVisual();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Depósito não permitido. Verifique as regras (ex: valor negativo ou depósito mínimo exigido).",
                        "Operação Negada",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro de comunicação com o banco de dados: " + ex.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE);
        }

        txtValor.setText("");
    }

    private void remunerarConta() {
        try {
            contaController.remunerar(conta);

            String tipoConta = (conta instanceof ContaCorrente)
                    ? "Conta Corrente (1%)"
                    : "Conta Investimento (2%)";

            JOptionPane.showMessageDialog(this,
                    "Remuneração aplicada com sucesso para: " + tipoConta,
                    "Remuneração", JOptionPane.INFORMATION_MESSAGE);

            atualizarSaldoVisual();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao aplicar remuneração no banco de dados: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}