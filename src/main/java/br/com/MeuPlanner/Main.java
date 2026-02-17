package br.com.MeuPlanner;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.view.Menu;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = ConnectionFactory.getConnection()) {
            System.out.println("Conectado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao conectar no banco:");
            e.printStackTrace();
            return;
        }


        Menu.exibir();
    }
}



