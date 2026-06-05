package view;

import javax.swing.*;

import model.dao.DatabaseSeeder;

public class Main {

    public static void main(String[] args) {
        /*
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaManterClientes telaPrincipal = new TelaManterClientes();
                telaPrincipal.setLocationRelativeTo(null);
                telaPrincipal.setVisible(true);
            }
        });
    }
    */

        /* rodar no MySQL Workbench o seguinte comando:
        CREATE DATABASE sistema_bancario_LPOOII; 
        INFELIZMENTE, eu criei o bd com o usuario e senha root 
        (no roadmap tava diferente, perdão) */ 

        DatabaseSeeder.inicializarBanco();
        
        System.out.println("Fim do teste de inicialização.");
    }
}