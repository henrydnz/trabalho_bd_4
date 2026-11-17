package org.example.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Utils {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    public static final String opcoesMenuPrincipal = """   
    ===== Menu Principal ====
    1  - Cadastrar Novo Fornecedor
    2  - Lancar Nova Fatura (Transacao OK)
    3  - Lancar Nova Fatura (Transacao Incompleta)
    4  - Lancar Nova Fatura (Transacao com Rollback)
    5  - Listar Fornecedores
    6  - Buscar Faturas por Fornecedor
    7  - Executar Comando SQL
    8  - Entrar com outro Usuario
    9  - Deletar Fatura
    10 - Consultar Despesas Totais por Periodo
    0  - Sair
    ==========================
    """;

    public static final Map<String, String> ESTADOS_PARA_UF = Map.ofEntries(
            Map.entry("ACRE", "AC"),
            Map.entry("ALAGOAS", "AL"),
            Map.entry("AMAPA", "AP"),
            Map.entry("AMAZONAS", "AM"),
            Map.entry("BAHIA", "BA"),
            Map.entry("CEARA", "CE"),
            Map.entry("DISTRITO FEDERAL", "DF"),
            Map.entry("ESPIRITO SANTO", "ES"),
            Map.entry("GOIAS", "GO"),
            Map.entry("MARANHAO", "MA"),
            Map.entry("MATO GROSSO", "MT"),
            Map.entry("MATO GROSSO DO SUL", "MS"),
            Map.entry("MINAS GERAIS", "MG"),
            Map.entry("PARA", "PA"),
            Map.entry("PARAIBA", "PB"),
            Map.entry("PARANA", "PR"),
            Map.entry("PERNAMBUCO", "PE"),
            Map.entry("PIAUI", "PI"),
            Map.entry("RIO DE JANEIRO", "RJ"),
            Map.entry("RIO GRANDE DO NORTE", "RN"),
            Map.entry("RIO GRANDE DO SUL", "RS"),
            Map.entry("RONDONIA", "RO"),
            Map.entry("RORAIMA", "RR"),
            Map.entry("SANTA CATARINA", "SC"),
            Map.entry("SAO PAULO", "SP"),
            Map.entry("SERGIPE", "SE"),
            Map.entry("TOCANTINS", "TO")
    );

    public static final List<String> TIPOS_LOGRADOURO = List.of(
            "RUA",
            "AVENIDA",
            "TRAVESSA",
            "ALAMEDA",
            "PRACA",
            "RODOVIA",
            "ESTRADA"
    );

    public static final List<String> MOTIVOS_LANCAMENTO_FATURA = List.of(
            "COMPRA DE PRODUTOS",
            "COMPROMISSO ENTREGA CONTRATO",
            "PRESTACAO DE SERVICOS",
            "LOCACAO DE EQUIPAMENTOS",
            "MANUTENCAO PREVENTIVA OU CORRETIVA"
    );

    public static String lerStr(Scanner sc, String prompt){
        System.out.println(prompt);
        return sc.nextLine();
    }

    public static int lerInt(Scanner sc, String prompt){
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    public static double lerDouble(Scanner sc, String prompt){
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }
}
