package com.simplebank.dao;

import com.simplebank.model.Loan;
import com.simplebank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Obiekt dostępu do danych dla tabeli kredyty.
 */
public class LoanDAO {

    public List<Loan> findByUserId(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM kredyty WHERE user_id = ? ORDER BY data_kredytu DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania kredytów", e);
        }
        return loans;
    }

    public void insert(Loan loan) {
        String sql = "INSERT INTO kredyty (user_id, kwota, stopa_kredytu, miesiace, status, splacone) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loan.getUserId());
            ps.setBigDecimal(2, loan.getKwota());
            ps.setBigDecimal(3, loan.getStopaKredytu());
            ps.setInt(4, loan.getMiesiace());
            ps.setString(5, loan.getStatus());
            ps.setBigDecimal(6, loan.getSplacone());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu kredytu", e);
        }
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setIdKredytu(rs.getInt("id_kredytu"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setKwota(rs.getBigDecimal("kwota"));
        loan.setStopaKredytu(rs.getBigDecimal("stopa_kredytu"));
        loan.setMiesiace(rs.getInt("miesiace"));
        loan.setStatus(rs.getString("status"));
        loan.setSplacone(rs.getBigDecimal("splacone"));
        loan.setDataKredytu(rs.getTimestamp("data_kredytu").toLocalDateTime());
        return loan;
    }
}
