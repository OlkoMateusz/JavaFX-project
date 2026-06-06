package com.simplebank.dao;

import com.simplebank.model.Transaction;
import com.simplebank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Obiekt dostępu do danych dla tabeli transakcje.
 *
 * Metoda insert przyjmująca połączenie jest używana wewnątrz transakcji SQL
 * przelewu, dzięki czemu zapis transakcji i aktualizacja sald są zatwierdzane
 * lub wycofywane razem.
 */
public class TransactionDAO {

    private static final String SELECT_WITH_TYPE =
            "SELECT tr.*, tt.nazwa AS nazwa_typu "
                    + "FROM transakcje tr JOIN typy_transakcji tt "
                    + "ON tr.typ_transakcji = tt.typ_transakcji ";

    /*
     * Zapisuje transakcję w ramach istniejącego połączenia (transakcji SQL).
     */
    public void insert(Connection conn, Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transakcje (from_nr_konta, to_nr_konta, kwota, typ_transakcji) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaction.getFromNrKonta());
            ps.setString(2, transaction.getToNrKonta());
            ps.setBigDecimal(3, transaction.getKwota());
            ps.setInt(4, transaction.getTypTransakcji());
            ps.executeUpdate();
        }
    }

    /*
     * Pobiera wszystkie transakcje powiązane z kontami danego użytkownika
     * (jako nadawca lub odbiorca), posortowane od najnowszej.
     */
    public List<Transaction> findByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = SELECT_WITH_TYPE
                + "WHERE tr.from_nr_konta IN (SELECT nr_konta FROM konta_bankowe WHERE user_id = ?) "
                + "OR tr.to_nr_konta IN (SELECT nr_konta FROM konta_bankowe WHERE user_id = ?) "
                + "ORDER BY tr.data_transakcji DESC, tr.id_transakcji DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania historii transakcji", e);
        }
        return transactions;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setIdTransakcji(rs.getInt("id_transakcji"));
        t.setFromNrKonta(rs.getString("from_nr_konta"));
        t.setToNrKonta(rs.getString("to_nr_konta"));
        t.setKwota(rs.getBigDecimal("kwota"));
        t.setTypTransakcji(rs.getInt("typ_transakcji"));
        t.setDataTransakcji(rs.getTimestamp("data_transakcji").toLocalDateTime());
        t.setNazwaTypu(rs.getString("nazwa_typu"));
        return t;
    }
}
