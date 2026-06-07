package view;

import model.*;
import controller.ContaController; 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaVincularConta extends JDialog {

    private Cliente cliente;
    private ContaController contaController; 

    private JComboBox<String> cmbTipoConta;
    private JPanel painelCartoes;
    private CardLayout cardLayout;

    private JTextField txtDepIniCC;
    private JTextField txtLimiteCC;

    private JTextField txtMontanteMinCI;
    private JTextField txtDepMinCI;
    private JTextField txtDepIniCI;

    public TelaVincularConta(Frame owner, Cliente cliente) {
        super(owner, true);
        this.cliente = cliente;
        this.contaController = new ContaController(); 

        boolean possuiContaObjeto = cliente.getConta() != null;
        boolean possuiContaBanco = false;
        
        try {
            Conta contaBanco = contaController.buscarContaPorCpf(cliente.getCpf());
            possuiContaBanco = (contaBanco != null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao verificar contas existentes no banco: " + ex.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        if (possuiContaObjeto || possuiContaBanco) {
            JOptionPane.showMessageDialog(this,
                    "O cliente " + cliente.getNome() + " já possui uma conta vinculada.",
                    "Conta Existente",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        setTitle("Vincular Conta ao Cliente: " + cliente.getNome());
        setBounds(200, 200, 500, 400);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelSuperior.add(new JLabel("Tipo de Conta:"));
        cmbTipoConta = new JComboBox<>(new String[]{
                "Selecione o Tipo...", "Conta Corrente", "Conta Investimento"});
        painelSuperior.add(cmbTipoConta);
        add(painelSuperior, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        painelCartoes = new JPanel(cardLayout);
        add(painelCartoes, BorderLayout.CENTER);

        criarPainelContaCorrente();
        criarPainelContaInvestimento();

        painelCartoes.add(new JPanel(), "Vazio");
        cardLayout.show(painelCartoes, "Vazio");

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Vincular e Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvarConta());
        btnCancelar.addActionListener(e -> dispose());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        cmbTipoConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipo = (String) cmbTipoConta.getSelectedItem();
                if ("Conta Corrente".equals(tipo)) {
                    cardLayout.show(painelCartoes, "Corrente");
                } else if ("Conta Investimento".equals(tipo)) {
                    cardLayout.show(painelCartoes, "Investimento");
                } else {
                    cardLayout.show(painelCartoes, "Vazio");
                }
            }
        });
    }

    private void criarPainelContaCorrente() {
        JPanel painelCC = new JPanel(new GridLayout(2, 2, 5, 5));
        painelCC.setBorder(BorderFactory.createTitledBorder("Dados Conta Corrente"));

        painelCC.add(new JLabel("Depósito Inicial (R$):"));
        txtDepIniCC = new JTextField();
        painelCC.add(txtDepIniCC);

        painelCC.add(new JLabel("Limite (R$):"));
        txtLimiteCC = new JTextField();
        painelCC.add(txtLimiteCC);

        painelCartoes.add(painelCC, "Corrente");
    }

    private void criarPainelContaInvestimento() {
        JPanel painelCI = new JPanel(new GridLayout(3, 2, 5, 5));
        painelCI.setBorder(BorderFactory.createTitledBorder("Dados Conta Investimento"));

        painelCI.add(new JLabel("Montante Mínimo:"));
        txtMontanteMinCI = new JTextField();
        painelCI.add(txtMontanteMinCI);

        painelCI.add(new JLabel("Depósito Mínimo:"));
        txtDepMinCI = new JTextField();
        painelCI.add(txtDepMinCI);

        painelCI.add(new JLabel("Depósito Inicial:"));
        txtDepIniCI = new JTextField();
        painelCI.add(txtDepIniCI);

        painelCartoes.add(painelCI, "Investimento");
    }

    private void salvarConta() {
        String tipo = (String) cmbTipoConta.getSelectedItem();

        try {
            if ("Conta Corrente".equals(tipo)) {
                double depInicial = Double.parseDouble(txtDepIniCC.getText());
                double limite = Double.parseDouble(txtLimiteCC.getText());

                if (depInicial < 0 || limite < 0)
                    throw new NumberFormatException("Valores não podem ser negativos.");

                contaController.criarContaCorrente(cliente, depInicial, limite);

            } else if ("Conta Investimento".equals(tipo)) {
                double montanteMin = Double.parseDouble(txtMontanteMinCI.getText());
                double depMin = Double.parseDouble(txtDepMinCI.getText());
                double depInicial = Double.parseDouble(txtDepIniCI.getText());

                if (montanteMin < 0 || depMin < 0 || depInicial < 0)
                    throw new NumberFormatException("Valores não podem ser negativos.");

                contaController.criarContaInvestimento(cliente, depInicial, montanteMin, depMin);

            } else {
                JOptionPane.showMessageDialog(this,
                        "Selecione um tipo de conta válido.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Conta vinculada ao cliente " + cliente.getNome() + " com sucesso!");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Valores inválidos. Verifique se todos os campos numéricos foram preenchidos corretamente.",
                    "Erro de Dados",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Erro de Regra de Negócio",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar no banco de dados: " + ex.getMessage(),
                    "Erro de Conexão",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}