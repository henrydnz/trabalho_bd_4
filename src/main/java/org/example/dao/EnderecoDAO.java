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

    public int inserir(Endereco endereco) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return inserir(conn, endereco);
        }
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

    // --- Cascata: busca o ID se já existir; senão insere e retorna o novo ID ---

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

    // --- Buscas auxiliares por nome ---

    public int buscarIdEstadoPorNome(Connection conn, String nomeEstado) throws SQLException {
        String sql = "SELECT \"idEstado\" FROM \"Estado\" WHERE \"nomeEstado\" = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeEstado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idEstado");
            }
        }
        throw new SQLException("Estado não encontrado: " + nomeEstado);
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

    // --- Métodos individuais de inserção das tabelas auxiliares ---

    public int inserirEstado(String nomeEstado, String siglaUF) throws SQLException {
        String sql = """
            INSERT INTO "Estado" ("nomeEstado", "siglaUF")
            VALUES (?, ?)
            RETURNING "idEstado"
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeEstado);
            stmt.setString(2, siglaUF.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idEstado");
            }
        }
        throw new SQLException("Falha ao inserir estado: " + nomeEstado);
    }

    public int inserirCidade(String nomeCidade, String nomeEstado) throws SQLException {
        String sql = """
            INSERT INTO "Cidade" ("nomeCidade", "idEstado")
            VALUES (?, ?)
            RETURNING "idCidade"
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idEstado = buscarIdEstadoPorNome(conn, nomeEstado);

            stmt.setString(1, nomeCidade);
            stmt.setInt(2, idEstado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idCidade");
            }
        }
        throw new SQLException("Falha ao inserir cidade: " + nomeCidade);
    }

    public int inserirBairro(String nomeBairro) throws SQLException {
        String sql = """
            INSERT INTO "Bairro" ("nomeBairro")
            VALUES (?)
            RETURNING "idBairro"
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeBairro);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idBairro");
            }
        }
        throw new SQLException("Falha ao inserir bairro: " + nomeBairro);
    }

    public int inserirTipoLogradouro(String nomeTipoLogradouro) throws SQLException {
        String sql = """
            INSERT INTO "TipoLogradouro" ("nomeTipoLogradouro")
            VALUES (?)
            RETURNING "idTipoLogradouro"
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeTipoLogradouro);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idTipoLogradouro");
            }
        }
        throw new SQLException("Falha ao inserir tipo de logradouro: " + nomeTipoLogradouro);
    }

    public int inserirRua(String nomeRua, String nomeTipoLogradouro) throws SQLException {
        String sql = """
            INSERT INTO "Rua" ("nomeRua", "idTipoLogradouro")
            VALUES (?, ?)
            RETURNING "idRua"
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idTipoLogradouro = buscarIdTipoLogradouroPorNome(conn, nomeTipoLogradouro);

            stmt.setString(1, nomeRua);
            stmt.setInt(2, idTipoLogradouro);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idRua");
            }
        }
        throw new SQLException("Falha ao inserir rua: " + nomeRua);
    }
}