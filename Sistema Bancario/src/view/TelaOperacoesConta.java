package view;

import controller.ContaController;
import controller.OperacaoController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import model.Conta;
import model.ContaCorrente;

public class TelaOperacoesConta extends JDialog {

    private JTextField txtCpf;
    private JButton btnBuscarConta;
    private Conta contaSelecionada;
    private ContaController contaController;
    private OperacaoController operacaoController;
    private JLabel lblSaldoAtual;
    private JTextField txtValor;
    private JButton btnSaque;
    private JButton btnDeposito;
    private JButton btnVerSaldo;
    private JButton btnRemunera;
    private final DecimalFormat df = new DecimalFormat("R$ #,##0.00");

    public TelaOperacoesConta(Frame owner, Conta conta) {
        this(owner, conta, new ContaController(), new OperacaoController());
    }

    public TelaOperacoesConta(Frame owner, ContaController contaController,
            OperacaoController operacaoController) {
        this(owner, null, contaController, operacaoController);
    }

    public TelaOperacoesConta(Frame owner, Conta conta,
            ContaController contaController, OperacaoController operacaoController) {
        super(owner, true);
        this.contaSelecionada = conta;
        this.contaController = contaController;
        this.operacaoController = operacaoController;

        setTitle("Operacoes de Conta");
        setBounds(250, 250, 470, 320);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("CPF:"));
        txtCpf = new JTextField(14);
        painelBusca.add(txtCpf);

        btnBuscarConta = new JButton("Buscar Conta");
        btnBuscarConta.addActionListener(e -> buscarConta());
        painelBusca.add(btnBuscarConta);
        add(painelBusca, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new GridLayout(3, 1, 5, 5));
        lblSaldoAtual = new JLabel();
        painelCentro.add(lblSaldoAtual);
        painelCentro.add(new JLabel("Valor da Operacao (R$):"));
        txtValor = new JTextField(10);
        painelCentro.add(txtValor);
        add(painelCentro, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridLayout(3, 2, 10, 10));
        btnSaque = new JButton("Sacar");
        btnDeposito = new JButton("Depositar");
        btnVerSaldo = new JButton("Ver Saldo");
        btnRemunera = new JButton("Remunerar");
        JButton btnFechar = new JButton("Fechar");

        btnSaque.addActionListener(e -> efetuarSaque());
        btnDeposito.addActionListener(e -> efetuarDeposito());
        btnVerSaldo.addActionListener(e -> mostrarSaldo());
        btnRemunera.addActionListener(e -> remunerarConta());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnSaque);
        painelBotoes.add(btnDeposito);
        painelBotoes.add(btnVerSaldo);
        painelBotoes.add(btnRemunera);
        painelBotoes.add(btnFechar);
        add(painelBotoes, BorderLayout.SOUTH);

        if (contaSelecionada != null) {
            txtCpf.setText(contaSelecionada.getDono().getCpf());
            atualizarSaldoVisual();
            habilitarOperacoes(true);
        } else {
            lblSaldoAtual.setText("Nenhuma conta selecionada.");
            habilitarOperacoes(false);
        }
    }

    private void buscarConta() {
        try {
            String cpf = txtCpf.getText().trim().replaceAll("\\D", "");
            contaSelecionada = contaController.buscarContaPorCpf(cpf);

            if (contaSelecionada == null) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma conta encontrada para este CPF.",
                        "Conta nao encontrada",
                        JOptionPane.WARNING_MESSAGE);
                lblSaldoAtual.setText("Nenhuma conta selecionada.");
                habilitarOperacoes(false);
                return;
            }

            atualizarSaldoVisual();
            habilitarOperacoes(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar conta: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void habilitarOperacoes(boolean habilitar) {
        txtValor.setEnabled(habilitar);
        btnSaque.setEnabled(habilitar);
        btnDeposito.setEnabled(habilitar);
        btnVerSaldo.setEnabled(habilitar);
        btnRemunera.setEnabled(habilitar);
    }

    private void atualizarSaldoVisual() {
        lblSaldoAtual.setText("Saldo Atual: " + df.format(contaSelecionada.getSaldo()));
        setTitle("Operacoes da Conta: " + contaSelecionada.getNumero()
                + " (" + contaSelecionada.getDono().getNome() + ")");
    }

    private void mostrarSaldo() {
        JOptionPane.showMessageDialog(this,
                "Saldo atual na conta " + contaSelecionada.getNumero()
                        + ":\n" + df.format(contaSelecionada.getSaldo()),
                "Ver Saldo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private double getValorDigitado() {
        try {
            return Double.parseDouble(txtValor.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Insira um valor numerico valido.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void efetuarSaque() {
        double valor = getValorDigitado();
        if (valor == -1) {
            return;
        }

        try {
            boolean sucesso = operacaoController.sacar(contaSelecionada, valor);

            if (sucesso) {
                contaController.atualizarSaldo(contaSelecionada);
                JOptionPane.showMessageDialog(this,
                        "Saque de " + df.format(valor) + " efetuado com sucesso!",
                        "Saque",
                        JOptionPane.INFORMATION_MESSAGE);
                atualizarSaldoVisual();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Saque nao permitido. Verifique as regras e os limites da sua conta.",
                        "Operacao Negada",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro de comunicacao com o banco de dados: " + ex.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE);
        }

        txtValor.setText("");
    }

    private void efetuarDeposito() {
        double valor = getValorDigitado();
        if (valor == -1) {
            return;
        }

        try {
            boolean sucesso = operacaoController.depositar(contaSelecionada, valor);

            if (sucesso) {
                contaController.atualizarSaldo(contaSelecionada);
                JOptionPane.showMessageDialog(this,
                        "Deposito de " + df.format(valor) + " efetuado com sucesso!",
                        "Deposito",
                        JOptionPane.INFORMATION_MESSAGE);
                atualizarSaldoVisual();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Deposito nao permitido. Verifique as regras da sua conta.",
                        "Operacao Negada",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro de comunicacao com o banco de dados: " + ex.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE);
        }

        txtValor.setText("");
    }

    private void remunerarConta() {
        try {
            if (contaSelecionada instanceof ContaCorrente && contaSelecionada.getSaldo() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Remuneracao nao aplicada: saldo nao positivo.",
                        "Remuneracao",
                        JOptionPane.WARNING_MESSAGE);
                atualizarSaldoVisual();
                return;
            }

            operacaoController.remunerar(contaSelecionada);
            contaController.atualizarSaldo(contaSelecionada);

            String tipoConta = (contaSelecionada instanceof ContaCorrente)
                    ? "Conta Corrente (1%)"
                    : "Conta Investimento (2%)";

            JOptionPane.showMessageDialog(this,
                    "Remuneracao aplicada com sucesso para: " + tipoConta,
                    "Remuneracao",
                    JOptionPane.INFORMATION_MESSAGE);

            atualizarSaldoVisual();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao aplicar remuneracao no banco de dados: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
