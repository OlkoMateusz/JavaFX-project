package com.simplebank.dao;

import com.simplebank.model.User;
import com.simplebank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Obiekt dostępu do danych dla tabeli uzytkownicy. Udostępnia operacje
 * wyszukiwania użytkowników, sprawdzania unikalności e-maila oraz zapisu
 * i aktualizacji danych logowania.
 */
public class UserDAO {

    public User findByEmail(String email) {
        String sql = "SELECT * FROM uzytkownicy WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania użytkownika po e-mailu", e);
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT * FROM uzytkownicy WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania użytkownika po ID", e);
        }
        return null;
    }

    /*
     * Sprawdza, czy podany e-mail jest już używany przez dowolnego użytkownika.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM uzytkownicy WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania e-maila", e);
        }
    }

    /*
     * Sprawdza, czy e-mail jest używany przez innego użytkownika niż wskazany.
     * Wykorzystywane przy zmianie adresu e-mail w ustawieniach.
     */
    public boolean emailExistsForOtherUser(String email, int userId) {
        String sql = "SELECT 1 FROM uzytkownicy WHERE email = ? AND user_id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania e-maila", e);
        }
    }

    public boolean peselExists(String pesel) {
        String sql = "SELECT 1 FROM uzytkownicy WHERE pesel = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pesel);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania numeru PESEL", e);
        }
    }

    /*
     * Zapisuje nowego użytkownika i ustawia w obiekcie wygenerowane ID.
     */
    public void insert(User user) {
        String sql = "INSERT INTO uzytkownicy (imie, nazwisko, pesel, email, haslo_hash, haslo_sol) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getImie());
            ps.setString(2, user.getNazwisko());
            ps.setString(3, user.getPesel());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getHasloHash());
            ps.setString(6, user.getHasloSol());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu użytkownika", e);
        }
    }

    public void updateEmail(int userId, String newEmail) {
        String sql = "UPDATE uzytkownicy SET email = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd aktualizacji e-maila", e);
        }
    }

    public void updatePassword(int userId, String newHash, String newSalt) {
        String sql = "UPDATE uzytkownicy SET haslo_hash = ?, haslo_sol = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, newSalt);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd aktualizacji hasła", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setImie(rs.getString("imie"));
        user.setNazwisko(rs.getString("nazwisko"));
        user.setPesel(rs.getString("pesel"));
        user.setEmail(rs.getString("email"));
        user.setHasloHash(rs.getString("haslo_hash"));
        user.setHasloSol(rs.getString("haslo_sol"));
        user.setDataUtworzenia(rs.getTimestamp("data_utworzenia").toLocalDateTime());
        return user;
    }
}
