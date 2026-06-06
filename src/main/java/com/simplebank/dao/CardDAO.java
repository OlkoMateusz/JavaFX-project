package com.simplebank.dao;

import com.simplebank.model.Card;
import com.simplebank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Obiekt dostępu do danych dla tabeli karty.
 * Karty są pobierane na podstawie kont należących do użytkownika.
 */
public class CardDAO {

    public List<Card> findByUserId(int userId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT ka.* FROM karty ka "
                + "JOIN konta_bankowe ko ON ka.id_konta = ko.nr_konta "
                + "WHERE ko.user_id = ? ORDER BY ka.id_karty";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania kart", e);
        }
        return cards;
    }

    public boolean cardNumberExists(String nrKarty) {
        String sql = "SELECT 1 FROM karty WHERE nr_karty = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nrKarty);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania numeru karty", e);
        }
    }

    public void insert(Card card) {
        String sql = "INSERT INTO karty (id_konta, nr_karty, cvv, data_waznosci, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, card.getIdKonta());
            ps.setString(2, card.getNrKarty());
            ps.setString(3, card.getCvv());
            ps.setDate(4, Date.valueOf(card.getDataWaznosci()));
            ps.setString(5, card.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu karty", e);
        }
    }

    private Card mapRow(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setIdKarty(rs.getInt("id_karty"));
        card.setIdKonta(rs.getString("id_konta"));
        card.setNrKarty(rs.getString("nr_karty"));
        card.setCvv(rs.getString("cvv"));
        card.setDataWaznosci(rs.getDate("data_waznosci").toLocalDate());
        card.setStatus(rs.getString("status"));
        return card;
    }
}
