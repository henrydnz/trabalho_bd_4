package org.example.dao;

import org.example.model.Fatura;
import org.example.model.Fornecedor;
import org.example.singleton.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FaturaDAO {

    public List<Fatura> listarPorFornecedor(Fornecedor fornecedor) throws SQLException {
        String sql = """
        SELECT f."numero", f."dataLancamento", f."dataVencimento", f."valorFatura"
        FROM "Fatura" f
        JOIN "MotivoFatura" m ON f."idMotivo" = m."idMotivo"
        WHERE f."idFornecedor" = ?
        ORDER BY f."dataVencimento"
        """;

        List<Fatura> faturas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fornecedor.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Fatura fatura = new Fatura();
                    fatura.setNumero(rs.getInt("nroFatura"));
                    fatura.setDataLancamento(rs.getDate("dataLancamento").toString());
                    fatura.setDataVencimento(rs.getDate("dataVencimento").toString());
                    fatura.setValor(rs.getDouble("valorFatura"));
                    fatura.setIdMotivo(rs.getInt("idMotivo"));

                    // Todas as faturas apontam para a mesma instância na memória
                    fatura.setFornecedor(fornecedor);

                    faturas.add(fatura);
                }
            }
        }
        return faturas;
    }

    public void inserir(Fatura fatura, int idMotivoFatura) throws SQLException {
        String sql = """
            INSERT INTO "Fatura" ("nroFatura", "idFornecedor", "idMotivo", "dataLancamento", "dataVencimento", "valor")
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, fatura.getNumero());
                stmt.setInt(2, fatura.getFornecedor().getId());
                stmt.setInt(3, idMotivoFatura);
                stmt.setDate(4, Date.valueOf(fatura.getDataLancamento())); // Formato esperado: "YYYY-MM-DD"
                stmt.setDate(5, Date.valueOf(fatura.getDataVencimento())); // Formato esperado: "YYYY-MM-DD"
                stmt.setDouble(6, fatura.getValor());

            stmt.executeUpdate();
        }
    }

    public void deletar(int numeroFatura) throws SQLException {
        String sql = "DELETE FROM \"Fatura\" WHERE \"nroFatura\" = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, numeroFatura);
                stmt.executeUpdate();
        }
    }

    // modo: 1 = OK (COMMIT) | 2 = Incompleta | 3 = Rollback intencional
    public void lancarFaturaComTransacao(Fatura fatura, int modo) throws SQLException {
        String sqlInsertFatura = """
            INSERT INTO "Fatura" ("nroFatura", "idFornecedor", "idMotivo", "dataLancamento", "dataVencimento", "valorFatura")
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String sqlUpdateSaldo = """
            UPDATE "Fornecedor"
            SET "saldoAPagar" = "saldoAPagar" + ?
            WHERE "idFornecedor" = ?
            """;

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertFatura)) {
                stmtInsert.setInt(1, fatura.getNumero());
                stmtInsert.setInt(2, fatura.getFornecedor().getId());
                stmtInsert.setInt(3, fatura.getIdMotivo());
                stmtInsert.setDate(4, Date.valueOf(fatura.getDataLancamento()));
                stmtInsert.setDate(5, Date.valueOf(fatura.getDataVencimento()));
                stmtInsert.setDouble(6, fatura.getValor());
                stmtInsert.executeUpdate();
            }

            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateSaldo)) {
                stmtUpdate.setDouble(1, fatura.getValor());
                stmtUpdate.setInt(2, fatura.getFornecedor().getId());
                stmtUpdate.executeUpdate();
            }

            if (modo == 1) {
                conn.commit();
                System.out.println("[T4D - OK] Transação confirmada com COMMIT.");
            } else if (modo == 2) {
                System.out.println("[T4D - INCOMPLETA] Encerrando programa sem COMMIT nem ROLLBACK...");
            } else if (modo == 3) {
                conn.rollback();
                System.out.println("[T4D - ROLLBACK] Transação abortada intencionalmente com ROLLBACK.");
            }

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
}