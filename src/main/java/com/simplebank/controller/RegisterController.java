package com.simplebank.controller;

import com.simplebank.service.AuthService;
import com.simplebank.service.BankException;
import com.simplebank.util.AlertUtil;
import com.simplebank.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/*
 * Kontroler ekranu rejestracji. Zbiera dane nowego użytkownika, przekazuje je
 * do serwisu rejestracji (który wykonuje walidację oraz zakłada konto bankowe)
 * i po sukcesie wraca do ekranu logowania.
 */
public class RegisterController {

    @FXML private TextField imieField;
    @FXML private TextField nazwiskoField;
    @FXML private TextField peselField;
    @FXML private TextField emailField;
    @FXML private PasswordField hasloField;
    @FXML private PasswordField powtorzHasloField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister() {
        try {
            authService.register(
                    imieField.getText(),
                    nazwiskoField.getText(),
                    peselField.getText(),
                    emailField.getText(),
                    hasloField.getText(),
                    powtorzHasloField.getText());
            AlertUtil.info("Rejestracja zakończona",
                    "Konto zostało utworzone. Możesz się teraz zalogować.");
            SceneManager.switchScene("login.fxml", "Simple Bank - Logowanie");
        } catch (BankException e) {
            AlertUtil.error("Rejestracja nie powiodła się", e.getMessage());
        } catch (RuntimeException e) {
            AlertUtil.error("Błąd", "Wystąpił nieoczekiwany błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        SceneManager.switchScene("login.fxml", "Simple Bank - Logowanie");
    }
}
