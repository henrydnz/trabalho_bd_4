package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://ep-seu-endpoint.aws.neon.tech/neondb?sslmode=require";

    // Sem 'final' para permitir trocar em tempo de execução
    private static String user = "user_total";
    private static String password = "Bd_Trabalho4#2026";

    private DBConnection() {}

    // Metodo para trocar o perfil de acesso no T4C
    public static void setCredenciais(String novoUser, String novaPassword) {
        user = novoUser;
        password = novaPassword;
        System.out.println(">> Perfil alterado para: " + user);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, user, password);
    }
}