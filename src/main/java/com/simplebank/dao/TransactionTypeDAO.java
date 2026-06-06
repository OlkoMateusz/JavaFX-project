package com.simplebank.dao;

import com.simplebank.model.TransactionType;
import com.simplebank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Obiekt dostępu do danych dla słownika typów transakcji
 * (tabela typy_transakcji).
 */
public class TransactionTypeDAO {

    public List<TransactionType> findAll() {
        List<TransactionType> types = new ArrayList<>();
        String sql = "SELECT * FROM typy_transakcji ORDER BY typ_transakcji";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania typów transakcji", e);
        }
        return types;
    }

    private TransactionType mapRow(ResultSet rs) throws SQLException {
        TransactionType type = new TransactionType();
        type.setTypTransakcji(rs.getInt("typ_transakcji"));
        type.setNazwa(rs.getString("nazwa"));
        type.setCzasRealizacji(rs.getInt("czas_realizacji"));
        type.setOplata(rs.getBigDecimal("oplata"));
        return type;
    }
}
