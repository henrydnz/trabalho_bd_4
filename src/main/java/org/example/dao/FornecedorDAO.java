package org.example.dao;

import org.example.model.Endereco;
import org.example.model.Fornecedor;
import org.example.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    private final EnderecoDAO enderecoDAO = new EnderecoDAO();

    public Fornecedor buscarPorId(int idFornecedor) throws SQLException {
        String sql = """
        SELECT "idFornecedor", "nomeFornecedor", "CNPJ", "idEndereco",
               "numero", "complemento", "saldoAPagar"
        FROM "Fornecedor"
        WHERE "idFornecedor" = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFornecedor);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarFornecedor(conn, rs);
                }
            }
        }
        return null;
    }

    public Fornecedor buscarPorNome(String nomeFornecedor) throws SQLException {
        String sql = """
            SELECT "idFornecedor", "nomeFornecedor", "CNPJ", "idEndereco",
                   "numero", "complemento", "saldoAPagar"
            FROM "Fornecedor"
            WHERE "nomeFornecedor" = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeFornecedor);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarFornecedor(conn, rs);
                }
            }
        }
        return null;
    }

    public List<Fornecedor> listarTodos() throws SQLException {
        String sql = """
            SELECT "idFornecedor", "nomeFornecedor", "CNPJ", "idEndereco",
                   "numero", "complemento", "saldoAPagar"
            FROM "Fornecedor"
            ORDER BY "nomeFornecedor"
            """;

        List<Fornecedor> fornecedores = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fornecedores.add(montarFornecedor(conn, rs));
            }
        }
        return fornecedores;
    }

    public int buscarIdFornecedorPorNome(Connection conn, String nomeFornecedor) throws SQLException {
        String sql = "SELECT \"idFornecedor\" FROM \"Fornecedor\" WHERE \"nomeFornecedor\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idFornecedor");
            }
        }
        throw new SQLException("Fornecedor não encontrado: " + nomeFornecedor);
    }

    public int buscarIdDDDPorNumero(Connection conn, String ddd) throws SQLException {
        String sql = "SELECT \"idDDD\" FROM \"DDD\" WHERE \"ddd\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ddd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idDDD");
            }
        }
        throw new SQLException("DDD não encontrado: " + ddd);
    }

    private Fornecedor montarFornecedor(Connection conn, ResultSet rs) throws SQLException {
        int idFornecedor = rs.getInt("idFornecedor");
        int idEndereco   = rs.getInt("idEndereco");

        Endereco endereco      = enderecoDAO.buscarPorId(idEndereco);
        List<String> emails    = listarEmails(conn, idFornecedor);
        List<String> telefones = listarTelefones(conn, idFornecedor);

        return new Fornecedor(
                telefones,
                emails,
                rs.getDouble("saldoAPagar"),
                rs.getString("complemento"),
                rs.getInt("numero"),
                endereco,
                rs.getString("CNPJ"),
                rs.getString("nomeFornecedor")
        );
    }

    private List<String> listarEmails(Connection conn, int idFornecedor) throws SQLException {
        String sql = "SELECT \"email\" FROM \"EmailFornecedor\" WHERE \"idFornecedor\" = ?";
        List<String> emails = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(rs.getString("email"));
                }
            }
        }
        return emails;
    }

    public void inserir(Fornecedor fornecedor) throws SQLException {
        String sqlFornecedor = """
        INSERT INTO "Fornecedor" ("nomeFornecedor", "CNPJ", "idEndereco", "numero", "complemento", "saldoAPagar")
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING "idFornecedor"
        """;

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            int idEndereco = enderecoDAO.inserir(fornecedor.getEndereco());
            int idFornecedor;
            try (PreparedStatement stmt = conn.prepareStatement(sqlFornecedor)) {
                stmt.setString(1, fornecedor.getNome());
                stmt.setString(2, fornecedor.getCnpj());
                stmt.setInt(3, idEndereco);
                stmt.setInt(4, fornecedor.getNumeroEndereco());
                stmt.setString(5, fornecedor.getComplementoEndereco());
                stmt.setDouble(6, fornecedor.getSaldoAPagar());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idFornecedor = rs.getInt("idFornecedor");
                    } else {
                        throw new SQLException("Falha ao obter ID do fornecedor inserido.");
                    }
                }
            }

            inserirEmails(conn, idFornecedor, fornecedor.getListaEmails());
            inserirTelefones(conn, idFornecedor, fornecedor.getListaTelefones());

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private void inserirEmails(Connection conn, int idFornecedor, List<String> emails) throws SQLException {
        if (emails == null || emails.isEmpty()) return;

        String sql = "INSERT INTO \"EmailFornecedor\" (\"email\", \"idFornecedor\") VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String email : emails) {
                stmt.setString(1, email);
                stmt.setInt(2, idFornecedor);
                stmt.executeUpdate();
            }
        }
    }

    private void inserirTelefones(Connection conn, int idFornecedor, List<String> telefones) throws SQLException {
        if (telefones == null || telefones.isEmpty()) return;

        String sql = "INSERT INTO \"TelefoneFornecedor\" (\"idDDD\", \"idFornecedor\", \"numeroTelefone\") VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String tel : telefones) {
                // Remove parênteses, espaços e traços (aceita "(45) 99999-9999" ou "45999999999")
                String apenasNumeros = tel.replaceAll("\\D", "");
                String ddd = apenasNumeros.substring(0, 2);
                String numero = apenasNumeros.substring(2);

                int idDDD = buscarIdDDDPorNumero(conn, ddd);

                stmt.setInt(1, idDDD);
                stmt.setInt(2, idFornecedor);
                stmt.setString(3, numero);
                stmt.executeUpdate();
            }
        }
    }

    private List<String> listarTelefones(Connection conn, int idFornecedor) throws SQLException {
        String sql = """
            SELECT d."ddd", t."numeroTelefone"
            FROM "TelefoneFornecedor" t
            JOIN "DDD" d ON t."idDDD" = d."idDDD"
            WHERE t."idFornecedor" = ?
            """;
        List<String> telefones = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    telefones.add("(" + rs.getString("ddd") + ") " + rs.getString("numeroTelefone"));
                }
            }
        }
        return telefones;
    }
}