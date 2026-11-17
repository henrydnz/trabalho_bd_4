package org.example.dao;

import org.example.model.Endereco;
import org.example.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderecoDAO {

    public Endereco buscarPorId(int idEndereco) throws SQLException {
        String sql = """
            SELECT est."nomeEstado", est."siglaUF", tl."nomeTipoLogradouro", cid."nomeCidade",
                   r."nomeRua", b."nomeBairro", e."cep"
            FROM "Endereco" e
            JOIN "Cidade" cid ON e."idCidade" = cid."idCidade"
            JOIN "Estado" est ON cid."idEstado" = est."idEstado"
            JOIN "Bairro" b ON e."idBairro" = b."idBairro"
            JOIN "Rua" r ON e."idRua" = r."idRua"
            JOIN "TipoLogradouro" tl ON r."idTipoLogradouro" = tl."idTipoLogradouro"
            WHERE e."idEndereco" = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEndereco);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarEndereco(rs);
                }
            }
        }
        return null;
    }

    // Sobrecarga usada pela transação do FornecedorDAO (inserção em cascata)
    public int inserir(Connection conn, Endereco endereco) throws SQLException {
        String sql = """
            INSERT INTO "Endereco" ("idCidade", "idBairro", "idRua", "cep")
            VALUES (?, ?, ?, ?)
            RETURNING "idEndereco"
            """;

        int idEstado         = obterOuInserirEstado(conn, endereco.getEstado(), endereco.getSiglaUF());
        int idCidade         = obterOuInserirCidade(conn, endereco.getCidade(), idEstado);
        int idBairro         = obterOuInserirBairro(conn, endereco.getBairro());
        int idTipoLogradouro = obterOuInserirTipoLogradouro(conn, endereco.getTipoLogradouro());
        int idRua            = obterOuInserirRua(conn, endereco.getRua(), idTipoLogradouro);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCidade);
            stmt.setInt(2, idBairro);
            stmt.setInt(3, idRua);
            stmt.setString(4, endereco.getCep());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idEndereco");
                }
            }
        }
        throw new SQLException("Falha ao inserir endereço.");
    }

    private Endereco montarEndereco(ResultSet rs) throws SQLException {
        return new Endereco(
                rs.getString("nomeEstado"),
                rs.getString("siglaUF"),
                rs.getString("nomeTipoLogradouro"),
                rs.getString("nomeCidade"),
                rs.getString("nomeRua"),
                rs.getString("nomeBairro"),
                rs.getString("cep")
        );
    }

    private int obterOuInserirEstado(Connection conn, String nomeEstado, String siglaUF) throws SQLException {
        String sqlSelect = "SELECT \"idEstado\" FROM \"Estado\" WHERE \"nomeEstado\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
            stmt.setString(1, nomeEstado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idEstado");
            }
        }

        String sqlInsert = "INSERT INTO \"Estado\" (\"nomeEstado\", \"siglaUF\") VALUES (?, ?) RETURNING \"idEstado\"";
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setString(1, nomeEstado);
            stmt.setString(2, siglaUF);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idEstado");
            }
        }
        throw new SQLException("Falha ao obter/inserir estado: " + nomeEstado);
    }

    private int obterOuInserirCidade(Connection conn, String nomeCidade, int idEstado) throws SQLException {
        String sqlSelect = "SELECT \"idCidade\" FROM \"Cidade\" WHERE \"nomeCidade\" = ? AND \"idEstado\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
            stmt.setString(1, nomeCidade);
            stmt.setInt(2, idEstado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idCidade");
            }
        }

        String sqlInsert = "INSERT INTO \"Cidade\" (\"nomeCidade\", \"idEstado\") VALUES (?, ?) RETURNING \"idCidade\"";
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setString(1, nomeCidade);
            stmt.setInt(2, idEstado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idCidade");
            }
        }
        throw new SQLException("Falha ao obter/inserir cidade: " + nomeCidade);
    }

    private int obterOuInserirBairro(Connection conn, String nomeBairro) throws SQLException {
        String sqlSelect = "SELECT \"idBairro\" FROM \"Bairro\" WHERE \"nomeBairro\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
            stmt.setString(1, nomeBairro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idBairro");
            }
        }

        String sqlInsert = "INSERT INTO \"Bairro\" (\"nomeBairro\") VALUES (?) RETURNING \"idBairro\"";
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setString(1, nomeBairro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idBairro");
            }
        }
        throw new SQLException("Falha ao obter/inserir bairro: " + nomeBairro);
    }

    private int obterOuInserirTipoLogradouro(Connection conn, String nomeTipo) throws SQLException {
        String sqlSelect = "SELECT \"idTipoLogradouro\" FROM \"TipoLogradouro\" WHERE \"nomeTipoLogradouro\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
            stmt.setString(1, nomeTipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idTipoLogradouro");
            }
        }

        String sqlInsert = "INSERT INTO \"TipoLogradouro\" (\"nomeTipoLogradouro\") VALUES (?) RETURNING \"idTipoLogradouro\"";
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setString(1, nomeTipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idTipoLogradouro");
            }
        }
        throw new SQLException("Falha ao obter/inserir tipo de logradouro: " + nomeTipo);
    }

    private int obterOuInserirRua(Connection conn, String nomeRua, int idTipoLogradouro) throws SQLException {
        String sqlSelect = "SELECT \"idRua\" FROM \"Rua\" WHERE \"nomeRua\" = ? AND \"idTipoLogradouro\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
            stmt.setString(1, nomeRua);
            stmt.setInt(2, idTipoLogradouro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idRua");
            }
        }

        String sqlInsert = "INSERT INTO \"Rua\" (\"nomeRua\", \"idTipoLogradouro\") VALUES (?, ?) RETURNING \"idRua\"";
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setString(1, nomeRua);
            stmt.setInt(2, idTipoLogradouro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idRua");
            }
        }
        throw new SQLException("Falha ao obter/inserir rua: " + nomeRua);
    }
}