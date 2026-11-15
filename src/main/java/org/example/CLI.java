package org.example;

import org.example.dao.EnderecoDAO;
import org.example.singleton.DBConnection;

import java.sql.Connection;

public class CLI {
    private EnderecoDAO enderecoDao = new EnderecoDAO();

    private String opcoesMenuPrincipal = """
    ===== Menu Principal =====
    1 - Cadastrar Novo Fornecedor
    2 - Lancar Nova Fatura
    3 - Buscar Faturas por Fornecedor
    4 - Executar Comando SQL
    0 - Sair
    ==========================
    """;

    private static void login(){
        String user;
        String password;


    }

    private static void cadastrarFornecedor(){

    }

    private static void lancarFatura() {

    }
}
