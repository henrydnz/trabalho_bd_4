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
            SELECT est."siglaUF", tl."nomeTipoLogradouro", cid."nomeCidade",
                   r."nomeRua", b."nomeBairro"
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

    public int inserir(Endereco endereco, String cep) throws SQLException {
        String sql = """
            INSERT INTO "Endereco" ("idCidade", "idBairro", "idRua", "cep")
            VALUES (?, ?, ?, ?)
            RETURNING "idEndereco"
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idEstado         = buscarIdEstadoPorUF(conn, endereco.getEstado());
            int idCidade         = buscarIdCidadePorNome(conn, endereco.getCidade(), idEstado);
            int idBairro         = buscarIdBairroPorNome(conn, endereco.getBairro());
            int idTipoLogradouro = buscarIdTipoLogradouroPorNome(conn, endereco.getTipoLogradouro());
            int idRua            = buscarIdRuaPorNome(conn, endereco.getRua(), idTipoLogradouro);

            stmt.setInt(1, idCidade);
            stmt.setInt(2, idBairro);
            stmt.setInt(3, idRua);
            stmt.setString(4, cep);

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
                rs.getString("siglaUF"),
                rs.getString("nomeTipoLogradouro"),
                rs.getString("nomeCidade"),
                rs.getString("nomeRua"),
                rs.getString("nomeBairro")
        );
    }

    // --- Buscas auxiliares por nome / UF ---

    public int buscarIdEstadoPorUF(Connection conn, String siglaUF) throws SQLException {
        String sql = "SELECT \"idEstado\" FROM \"Estado\" WHERE \"siglaUF\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, siglaUF.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idEstado");
            }
        }
        throw new SQLException("Estado (UF) não encontrado: " + siglaUF);
    }

    public int buscarIdCidadePorNome(Connection conn, String nomeCidade, int idEstado) throws SQLException {
        String sql = "SELECT \"idCidade\" FROM \"Cidade\" WHERE \"nomeCidade\" = ? AND \"idEstado\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeCidade);
            stmt.setInt(2, idEstado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idCidade");
            }
        }
        throw new SQLException("Cidade não encontrada: " + nomeCidade);
    }

    public int buscarIdBairroPorNome(Connection conn, String nomeBairro) throws SQLException {
        String sql = "SELECT \"idBairro\" FROM \"Bairro\" WHERE \"nomeBairro\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeBairro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idBairro");
            }
        }
        throw new SQLException("Bairro não encontrado: " + nomeBairro);
    }

    public int buscarIdTipoLogradouroPorNome(Connection conn, String nomeTipo) throws SQLException {
        String sql = "SELECT \"idTipoLogradouro\" FROM \"TipoLogradouro\" WHERE \"nomeTipoLogradouro\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeTipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idTipoLogradouro");
            }
        }
        throw new SQLException("Tipo de logradouro não encontrado: " + nomeTipo);
    }

    public int buscarIdRuaPorNome(Connection conn, String nomeRua, int idTipoLogradouro) throws SQLException {
        String sql = "SELECT \"idRua\" FROM \"Rua\" WHERE \"nomeRua\" = ? AND \"idTipoLogradouro\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeRua);
            stmt.setInt(2, idTipoLogradouro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idRua");
            }
        }
        throw new SQLException("Rua não encontrada: " + nomeRua);
    }
}