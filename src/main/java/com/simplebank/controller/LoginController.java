package com.simplebank.controller;

import com.simplebank.model.User;
import com.simplebank.service.AuthService;
import com.simplebank.service.BankException;
import com.simplebank.util.AlertUtil;
import com.simplebank.util.SceneManager;
import com.simplebank.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/*
 * Kontroler ekranu logowania. Pobiera dane z formularza, przekazuje je do
 * serwisu uwierzytelniania i w razie powodzenia przenosi użytkownika do
 * panelu klienta. Błędy logowania prezentowane są w oknie Alert.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        try {
            User user = authService.login(emailField.getText(), passwordField.getText());
            SessionManager.login(user);
            SceneManager.switchScene("dashboard.fxml", "Simple Bank - Panel klienta");
        } catch (BankException e) {
            AlertUtil.error("Logowanie nie powiodło się", e.getMessage());
        } catch (RuntimeException e) {
            AlertUtil.error("Błąd", "Wystąpił nieoczekiwany błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToRegister() {
        SceneManager.switchScene("register.fxml", "Simple Bank - Rejestracja");
    }
}
