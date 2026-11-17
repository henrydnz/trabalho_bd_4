package org.example;

import org.example.dao.*;
import org.example.model.*;
import org.example.utils.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class CLI {
    private static final FaturaDAO faturaDao = new FaturaDAO();
    private static final FornecedorDAO fornecedorDao = new FornecedorDAO();

    private static final Scanner s = new Scanner(System.in);

    private static void login(){
        System.out.println("\n===== Login =====");

        String user;
        String password;

        while(true) {
            try {
                user = Utils.lerStr(s, "Usuario: ").trim();

                if("xyz".equals(user)){
                    DBConnection.setCredenciais("user_total", "Bd_Trabalho4#2026");
                    return;
                }

                password = Utils.lerStr(s, "Senha: ");

                DBConnection.testConnection(user, password);
                DBConnection.setCredenciais(user, password);
                return;
            } catch (SQLInvalidAuthorizationSpecException e) {
                System.out.println("Usuario ou senha incorretos.\n");
            } catch (SQLException e) {
                System.out.println("Erro ao testar login: " + e.getMessage() + "\n");
            }
        }
    }

    public static void menuPrincipal(){
        login();
        boolean running = true;
        while(running){
            System.out.println("\nUsuario ativo: " + DBConnection.getUser());
            System.out.print(Utils.opcoesMenuPrincipal);
            int opcao = Utils.lerInt(s, ">> ");
            System.out.println();

            switch (opcao) {
                case 0 -> running = false;
                case 1 -> cadastrarFornecedor();
                case 2 -> lancarFatura(1);
                case 3 -> lancarFatura(2);
                case 4 -> lancarFatura(3);
                case 5 -> listarFornecedores();
                case 6 -> buscarFatura();
                case 7 -> executarSQL();
                case 8 -> login();
                case 9 -> deletarFatura();
                case 10 -> consultarTotaisPorPeriodo();
                default -> System.out.println("Opcao invalida.");
            }
        }
        System.out.println("Fechando programa.");
    }

    private static void executarSQL(){
        System.out.println("Escreva o comando SQL (termine com ;):");

        StringBuilder sb = new StringBuilder();
        while (true) {
            String linha = s.nextLine();
            sb.append(linha).append(" ");
            if (linha.trim().endsWith(";")) break;
        }
        String sql = sb.toString().trim();

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            System.out.println("SQL executado.");
            if(stmt.execute()){
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for(int i=1;i<=colCount;i++) System.out.printf("%-22s", meta.getColumnName(i));
                System.out.println();
                while(rs.next()){
                    for(int i=1;i<=colCount;i++) System.out.printf("%-22s", rs.getString(i));
                    System.out.println();
                }
            } else {
                int updateCount = stmt.getUpdateCount();
                if(updateCount > -1) System.out.printf("%d atualizacoes.\n", updateCount);
                else System.out.println("Sem atualizacoes ou resultados.");
            }
        } catch (SQLException e){
            if ("42501".equals(e.getSQLState())){
                System.out.printf("Usuario nao tem permissao para esse comando (%s).\n", DBConnection.getUser());
            } else {
                System.out.println("Erro ao executar SQL: " + e.getMessage());
            }
        }
    }

    private static String[] formularioLogradouroCompleto(){
        while(true){
            String logradouroCompleto = Utils.lerStr(s,
                    "Logradouros reconhecidos: " + String.join(", ", Utils.TIPOS_LOGRADOURO) +
                            "\nTipo e nome do logradouro (ex: RUA BRASIL): "
            );

            String[] partes = logradouroCompleto.trim().split("\\s+", 2);

            String tipoLogr = partes[0].toUpperCase();
            String logr = partes.length > 1 ? partes[1].trim() : "";

            if(!Utils.TIPOS_LOGRADOURO.contains(tipoLogr)){
                System.out.println("Erro: O tipo de logradouro '" + partes[0] + "' nao foi reconhecido.");
            } else if(logr.isEmpty()){
                System.out.println("Erro: Nome do logradouro vazio.");
            } else {
                return new String[] {tipoLogr, logr};
            }
        }
    }

    private static Endereco formularioEnderecoFornecedor(){
        System.out.println("-- Endereco --");

        String cep = Utils.lerStr(s, "CEP (8 digitos): ").trim();

        String estado;
        while(true){
            estado = Utils.lerStr(s, "Estado: ").trim().toUpperCase();
            if(!Utils.ESTADOS_PARA_UF.containsKey(estado))
                System.out.println("Estado nao existe. Digite sem acentos.");
            else
                break;
        }

        String UF = Utils.ESTADOS_PARA_UF.get(estado);

        String cidade = Utils.lerStr(s, "Cidade: ").trim();
        String bairro = Utils.lerStr(s, "Bairro: ").trim();

        String[] logradouroCompleto = formularioLogradouroCompleto();
        String tipoLogradouro = logradouroCompleto[0];
        String logradouro = logradouroCompleto[1];

        return new Endereco (
                estado,
                UF,
                tipoLogradouro,
                cidade,
                logradouro,
                bairro,
                cep
        );
    }

    private static List<String> formularioEmailFornecedor(){
        System.out.println("Lista de Emails (pressione Enter vazio para terminar):");
        List<String> emails = new ArrayList<>();
        while(true){
            String email = Utils.lerStr(s, "Email: ").trim();
            if(email.isEmpty()) break;
            emails.add(email);
        }
        return emails;
    }

    private static List<String> formularioNumeroFornecedor(){
        System.out.println("Lista de Telefones com DDD (pressione Enter vazio para terminar):");
        List<String> telefones = new ArrayList<>();
        while(true){
            String num = Utils.lerStr(s, "Telefone (ex: 45999998888): ").trim();
            if(num.isEmpty()) break;
            telefones.add(num);
        }
        return telefones;
    }

    private static Fornecedor formularioFornecedor(){
        System.out.println("-- Informacoes --");

        String nome = Utils.lerStr(s, "Nome: ").trim();
        String cnpj = Utils.lerStr(s, "CNPJ (14 digitos): ").trim();

        List<String> emails = formularioEmailFornecedor();
        List<String> telefones = formularioNumeroFornecedor();
        Endereco endereco = formularioEnderecoFornecedor();

        int numeroEndereco = Utils.lerInt(s, "Numero: ");
        String complementoEndereco = Utils.lerStr(s, "Complemento: ").trim();

        return new Fornecedor (
                telefones,
                emails,
                0.0,
                complementoEndereco,
                numeroEndereco,
                endereco,
                cnpj,
                nome
        );
    }

    private static void cadastrarFornecedor(){
        System.out.println("===== Cadastrar Fornecedor =====");

        Fornecedor f = formularioFornecedor();

        try {
            fornecedorDao.inserir(f);
            System.out.println("Fornecedor cadastrado com sucesso!");
        } catch (SQLException e) {
            if ("42501".equals(e.getSQLState())){
                System.out.printf("Usuario nao tem permissao para cadastro (%s).\n", DBConnection.getUser());
            } else {
                System.out.println("Erro ao cadastrar fornecedor: " + e.getMessage());
            }
        }
    }

    private static int formularioNumeroFaturaCadastro(){
        try {
            while (true) {
                int numeroFatura = Utils.lerInt(s, "Digite o numero da fatura para cadastro: ");
                if (faturaDao.buscarPorId(numeroFatura) == null) return numeroFatura;
                System.out.println("Esse numero de fatura ja existe!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar fatura por id: " + e.getMessage());
            return -1;
        }
    }

    private static Fornecedor formularioFornecedorExistente(){
        try {
            while(true){
                int idFornecedor = Utils.lerInt(s, "Digite o ID de um fornecedor existente: ");

                Fornecedor fornecedor = fornecedorDao.buscarPorId(idFornecedor);
                if (fornecedor != null) return fornecedor;

                System.out.println("Nao existe fornecedor com id " + idFornecedor + ".");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar fornecedor por id: " + e.getMessage());
        }
        return null;
    }

    private static String formularioMotivoFatura(){
        System.out.println("Escolha um motivo para o lancamento da fatura:");
        for(int i = 0; i < Utils.MOTIVOS_LANCAMENTO_FATURA.size(); i++){
            System.out.println((i + 1) + " - " + Utils.MOTIVOS_LANCAMENTO_FATURA.get(i));
        }

        while(true){
            int motivoFaturaIdx = Utils.lerInt(s, ">> ") - 1;

            if(motivoFaturaIdx >= 0 && motivoFaturaIdx < Utils.MOTIVOS_LANCAMENTO_FATURA.size())
                return Utils.MOTIVOS_LANCAMENTO_FATURA.get(motivoFaturaIdx);

            System.out.println("Opcao invalida.");
        }
    }

    private static LocalDate[] formularioDatas(){
        LocalDate dataLancamento;
        LocalDate dataVencimento;

        while(true) {
            String dataLancamentoInput = Utils.lerStr(s, "Digite a data de lancamento (dd/mm/yyyy): ").trim();
            try{
                dataLancamento = LocalDate.parse(dataLancamentoInput, Utils.FORMATO_DATA);
                break;
            } catch (DateTimeParseException | NullPointerException e) {
                System.out.println("Formato de data invalido. Use dd/mm/yyyy.");
            }
        }

        while(true){
            String dataVencimentoInput = Utils.lerStr(s, "Digite a data de vencimento (dd/mm/yyyy): ").trim();
            try{
                dataVencimento = LocalDate.parse(dataVencimentoInput, Utils.FORMATO_DATA);

                if(!dataVencimento.isBefore(dataLancamento))
                    return new LocalDate[] {dataLancamento, dataVencimento};

                System.out.println("Data de vencimento nao pode ser antes da data de lancamento.");
            } catch (DateTimeParseException | NullPointerException e) {
                System.out.println("Formato de data invalido. Use dd/mm/yyyy.");
            }
        }
    }

    private static Double formularioValorFatura(){
        while(true){
            double valorFatura = Utils.lerDouble(s, "Digite o valor da fatura (R$): ");

            if(valorFatura > 0.0) return valorFatura;
            System.out.println("Valor precisa ser maior que zero.");
        }
    }

    private static Fatura formularioFatura(){
        Integer numeroFatura = formularioNumeroFaturaCadastro();
        Fornecedor fornecedor = formularioFornecedorExistente();
        String motivo = formularioMotivoFatura();
        LocalDate[] datas = formularioDatas();
        LocalDate dataLancamento = datas[0];
        LocalDate dataVencimento = datas[1];
        Double valorFatura = formularioValorFatura();

        return new Fatura(
                numeroFatura,
                fornecedor,
                motivo,
                dataLancamento,
                dataVencimento,
                valorFatura
        );
    }

    private static void lancarFatura(int modo) {
        System.out.println("===== Lancamento de Fatura =====");

        String modoStr = "";
        if(modo == 1) modoStr = "Transacao OK";
        else if(modo == 2) modoStr = "Transacao Incompleta";
        else if(modo == 3 ) modoStr = "Transacao Rollback (terminar execucao)";

        System.out.println("\nModo de transacao: " + modoStr);

        Fatura fatura = formularioFatura();

        try {
            faturaDao.lancarFaturaComTransacao(fatura, modo);
        } catch (SQLException e) {
            if ("42501".equals(e.getSQLState())){
                System.out.printf("Usuario nao tem permissao para cadastro (%s).\n", DBConnection.getUser());
            } else {
                System.out.println("Erro ao lancar fatura: " + e.getMessage());
            }
        }
    }

    private static void listarFornecedores(){
        try{
            List<Fornecedor> fornecedores = fornecedorDao.listarTodos();

            System.out.println("===== Lista de Fornecedores =====");
            System.out.printf("%-5s %-33s %-16s %-15s\n", "ID", "Nome", "CNPJ", "Saldo a Pagar");
            for(Fornecedor fornecedor : fornecedores)
                System.out.printf("%-5d %-33s %-16s R$ %-12.2f\n",
                        fornecedor.getId(), fornecedor.getNome(), fornecedor.getCnpj(), fornecedor.getSaldoAPagar());
        } catch (SQLException e) {
            System.out.println("Erro ao listar fornecedores: " + e.getMessage());
        }
    }

    private static void buscarFatura(){
        System.out.println("===== Consulta das Faturas a Pagar =====");

        Fornecedor fornecedor = formularioFornecedorExistente();
        if (fornecedor == null) return;

        System.out.println("\nFornecedor: " + fornecedor.getNome() + "  |  CNPJ: " + fornecedor.getCnpj());
        System.out.printf("Saldo a pagar: R$ %.2f\n", fornecedor.getSaldoAPagar());

        List<String> emails = fornecedor.getListaEmails();
        List<String> telefones = fornecedor.getListaTelefones();
        int rowNum = Integer.max(emails.size(), telefones.size());
        if(rowNum > 0) {
            System.out.printf("%-30s %s\n", "Emails", "Telefones");
            for (int i = 0; i < rowNum; i++){
                String email = i < emails.size() ? emails.get(i) : "";
                String tel = i < telefones.size() ? telefones.get(i) : "";
                System.out.printf("%-30s %s\n", email, tel);
            }
        }

        System.out.println("\n-- Faturas --");

        try {
            List<Fatura> faturas = faturaDao.listarPorFornecedor(fornecedor);

            System.out.printf("%-8s %-36s %-12s %-14s %-14s\n", "Nro.", "Motivo Fatura", "Vencimento", "Valor", "Saldo");
            double saldoAcumulado = 0.0;
            for(Fatura fatura : faturas) {
                saldoAcumulado += fatura.getValor();
                System.out.printf("%-8d %-36s %-12s R$ %-11.2f R$ %-11.2f\n",
                        fatura.getNumero(),
                        fatura.getNomeMotivo(),
                        fatura.getDataVencimento().format(Utils.FORMATO_DATA),
                        fatura.getValor(),
                        saldoAcumulado);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar faturas: " + e.getMessage());
        }
    }

    private static void consultarTotaisPorPeriodo(){
        System.out.println("===== Consulta de Totais por Tipo de Despesa no Periodo =====");

        LocalDate dataInicio;
        LocalDate dataFim;

        while(true) {
            String inputInicio = Utils.lerStr(s, "Digite a data inicial (dd/mm/yyyy): ").trim();
            try {
                dataInicio = LocalDate.parse(inputInicio, Utils.FORMATO_DATA);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data invalido. Use dd/mm/yyyy.");
            }
        }

        while(true) {
            String inputFim = Utils.lerStr(s, "Digite a data final (dd/mm/yyyy): ").trim();
            try {
                dataFim = LocalDate.parse(inputFim, Utils.FORMATO_DATA);
                if (!dataFim.isBefore(dataInicio)) break;
                System.out.println("Data final nao pode ser anterior a data inicial.");
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data invalido. Use dd/mm/yyyy.");
            }
        }

        try {
            List<String[]> totais = faturaDao.consultarTotaisPorMotivoNoPeriodo(dataInicio, dataFim);

            System.out.printf("\nTotais por Motivo de Fatura (%s a %s):\n",
                    dataInicio.format(Utils.FORMATO_DATA), dataFim.format(Utils.FORMATO_DATA));

            if (totais.isEmpty()) {
                System.out.println("Nenhuma fatura encontrada nesse periodo.");
                return;
            }

            System.out.printf("%-38s %-12s %-15s\n", "Motivo da Despesa", "Qtd. Faturas", "Total");
            for (String[] linha : totais) {
                System.out.printf("%-38s %-12s %-15s\n", linha[0], linha[1], linha[2]);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar totais por periodo: " + e.getMessage());
        }
    }

    private static int formularioNumeroFaturaDeletar(){
        try {
            while (true) {
                int numeroFatura = Utils.lerInt(s, "Digite o numero da fatura para deletar: ");
                if (faturaDao.buscarPorId(numeroFatura) != null) return numeroFatura;
                System.out.println("Esse numero de fatura nao existe!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar fatura por id: " + e.getMessage());
            return -1;
        }
    }

    private static void deletarFatura(){
        System.out.println("===== Deletar Fatura =====");
        int numeroFatura = formularioNumeroFaturaDeletar();
        if (numeroFatura == -1) return;

        try {
            faturaDao.deletar(numeroFatura);
            System.out.println("Fatura " + numeroFatura + " deletada com sucesso.");
        } catch (SQLException e) {
            if ("42501".equals(e.getSQLState())){
                System.out.printf("Usuario nao tem permissao para deletar (%s).\n", DBConnection.getUser());
            } else {
                System.out.println("Erro ao deletar fatura: " + e.getMessage());
            }
        }
    }
}