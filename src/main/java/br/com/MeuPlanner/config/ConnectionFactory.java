package br.com.MeuPlanner.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:sqlite:MeuPlanner.sqlite";

    private ConnectionFactory(){

    }

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(URL);
        }catch (SQLException e){
            throw new RuntimeException("Erro ao conectar", e);
        }
    }





}
