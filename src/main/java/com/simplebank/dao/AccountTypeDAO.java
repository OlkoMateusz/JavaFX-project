package com.simplebank.dao;

import com.simplebank.model.AccountType;
import com.simplebank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Obiekt dostępu do danych dla słownika typów kont (tabela typ_konta).
 */
public class AccountTypeDAO {

    public List<AccountType> findAll() {
        List<AccountType> types = new ArrayList<>();
        String sql = "SELECT * FROM typ_konta ORDER BY rodzaj_konta";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania typów kont", e);
        }
        return types;
    }

    public AccountType findById(int rodzajKonta) {
        String sql = "SELECT * FROM typ_konta WHERE rodzaj_konta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rodzajKonta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania typu konta", e);
        }
        return null;
    }

    private AccountType mapRow(ResultSet rs) throws SQLException {
        AccountType type = new AccountType();
        type.setRodzajKonta(rs.getInt("rodzaj_konta"));
        type.setNazwa(rs.getString("nazwa"));
        type.setOprocentowanie(rs.getBigDecimal("oprocentowanie"));
        type.setMozliwoscKredytu(rs.getBoolean("mozliwosc_kredytu"));
        return type;
    }
}
