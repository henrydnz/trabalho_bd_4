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
        String sql = "SELECT \"email\" FROM \"Email\" WHERE \"idFornecedor\" = ?";
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

    private List<String> listarTelefones(Connection conn, int idFornecedor) throws SQLException {
        String sql = """
            SELECT d."ddd", t."numeroTelefone"
            FROM "Telefone" t
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