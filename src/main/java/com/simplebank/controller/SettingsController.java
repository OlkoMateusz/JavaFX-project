package com.simplebank.controller;

import com.simplebank.model.User;
import com.simplebank.service.BankException;
import com.simplebank.service.UserService;
import com.simplebank.util.AlertUtil;
import com.simplebank.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/*
 * Kontroler zakładki ustawień konta. Pozwala zmienić adres e-mail oraz hasło
 * zalogowanego użytkownika. Zmiana hasła wymaga podania dotychczasowego hasła,
 * które jest weryfikowane przed zapisaniem nowego (zahashowanego) hasła.
 */
public class SettingsController {

    @FXML private Label lblCurrentEmail;
    @FXML private TextField newEmailField;

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField repeatPasswordField;

    private final UserService userService = new UserService();
    private User currentUser;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getCurrentUser();
        lblCurrentEmail.setText(currentUser.getEmail());
    }

    @FXML
    private void handleChangeEmail() {
        try {
            userService.changeEmail(currentUser, newEmailField.getText());
            lblCurrentEmail.setText(currentUser.getEmail());
            newEmailField.clear();
            AlertUtil.info("Ustawienia", "Adres e-mail został zmieniony.");
        } catch (BankException e) {
            AlertUtil.error("Zmiana e-maila", e.getMessage());
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            userService.changePassword(currentUser,
                    oldPasswordField.getText(),
                    newPasswordField.getText(),
                    repeatPasswordField.getText());
            oldPasswordField.clear();
            newPasswordField.clear();
            repeatPasswordField.clear();
            AlertUtil.info("Ustawienia", "Hasło zostało zmienione.");
        } catch (BankException e) {
            AlertUtil.error("Zmiana hasła", e.getMessage());
        }
    }
}
