package com.simplebank.dao;

import com.simplebank.model.BankAccount;
import com.simplebank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Obiekt dostępu do danych dla tabeli konta_bankowe.
 *
 * Część metod przyjmuje istniejące połączenie (Connection) jako argument.
 * Są one wykorzystywane przez operacje wymagające transakcji SQL (np. przelew),
 * gdzie wiele zapytań musi działać w obrębie jednej, wspólnej transakcji.
 */
public class BankAccountDAO {

    private static final String SELECT_WITH_TYPE =
            "SELECT k.*, t.nazwa AS nazwa_typu "
                    + "FROM konta_bankowe k JOIN typ_konta t ON k.rodzaj_konta = t.rodzaj_konta ";

    public List<BankAccount> findByUserId(int userId) {
        List<BankAccount> accounts = new ArrayList<>();
        String sql = SELECT_WITH_TYPE + "WHERE k.user_id = ? ORDER BY k.nr_konta";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania kont użytkownika", e);
        }
        return accounts;
    }

    public BankAccount findByNrKonta(String nrKonta) {
        String sql = SELECT_WITH_TYPE + "WHERE k.nr_konta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nrKonta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania konta", e);
        }
        return null;
    }

    public boolean exists(String nrKonta) {
        String sql = "SELECT 1 FROM konta_bankowe WHERE nr_konta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nrKonta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania konta", e);
        }
    }

    public void insert(BankAccount account) {
        String sql = "INSERT INTO konta_bankowe (nr_konta, rodzaj_konta, saldo, user_id, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getNrKonta());
            ps.setInt(2, account.getRodzajKonta());
            ps.setBigDecimal(3, account.getSaldo());
            ps.setInt(4, account.getUserId());
            ps.setString(5, account.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu konta", e);
        }
    }

    /*
     * Pobiera saldo konta w ramach istniejącej transakcji, blokując wiersz
     * (SELECT ... FOR UPDATE) do czasu zatwierdzenia lub wycofania transakcji.
     * Zwraca null, gdy konto nie istnieje.
     */
    public BigDecimal getSaldoForUpdate(Connection conn, String nrKonta) throws SQLException {
        String sql = "SELECT saldo FROM konta_bankowe WHERE nr_konta = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nrKonta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("saldo");
                }
            }
        }
        return null;
    }

    /*
     * Aktualizuje saldo konta w ramach istniejącej transakcji.
     */
    public void updateSaldo(Connection conn, String nrKonta, BigDecimal noweSaldo) throws SQLException {
        String sql = "UPDATE konta_bankowe SET saldo = ? WHERE nr_konta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, noweSaldo);
            ps.setString(2, nrKonta);
            ps.executeUpdate();
        }
    }

    private BankAccount mapRow(ResultSet rs) throws SQLException {
        BankAccount account = new BankAccount();
        account.setNrKonta(rs.getString("nr_konta"));
        account.setRodzajKonta(rs.getInt("rodzaj_konta"));
        account.setSaldo(rs.getBigDecimal("saldo"));
        account.setUserId(rs.getInt("user_id"));
        account.setStatus(rs.getString("status"));
        account.setDataUtworzenia(rs.getTimestamp("data_utworzenia").toLocalDateTime());
        account.setNazwaTypu(rs.getString("nazwa_typu"));
        return account;
    }
}
