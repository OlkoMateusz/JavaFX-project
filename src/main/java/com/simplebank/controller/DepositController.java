package com.simplebank.controller;

import com.simplebank.model.BankAccount;
import com.simplebank.service.BankException;
import com.simplebank.service.BankService;
import com.simplebank.util.AlertUtil;
import com.simplebank.util.SessionManager;
import com.simplebank.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

/*
 * Kontroler okna wpłaty gotówki. Pozwala wybrać konto docelowe i podać kwotę,
 * a następnie zleca serwisowi bankowemu zaksięgowanie wpłaty.
 */
public class DepositController {

    @FXML private ComboBox<BankAccount> accountCombo;
    @FXML private TextField amountField;

    private final BankService bankService = new BankService();

    @FXML
    private void initialize() {
        int userId = SessionManager.getCurrentUser().getUserId();
        accountCombo.setItems(FXCollections.observableArrayList(bankService.getAccounts(userId)));
        if (!accountCombo.getItems().isEmpty()) {
            accountCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleConfirm() {
        BankAccount account = accountCombo.getValue();
        if (account == null) {
            AlertUtil.error("Wpłata", "Wybierz konto.");
            return;
        }

        BigDecimal kwota = ValidationUtil.parseAmount(amountField.getText());
        if (!ValidationUtil.isPositiveAmount(kwota)) {
            AlertUtil.error("Wpłata", "Podaj poprawną, dodatnią kwotę.");
            return;
        }

        try {
            bankService.deposit(account.getNrKonta(), kwota);
            AlertUtil.info("Wpłata", "Wpłata została zaksięgowana.");
            zamknij();
        } catch (BankException e) {
            AlertUtil.error("Wpłata", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        zamknij();
    }

    private void zamknij() {
        ((Stage) amountField.getScene().getWindow()).close();
    }
}
