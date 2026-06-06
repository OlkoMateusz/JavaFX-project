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
 * Kontroler okna przelewu. Pozwala wybrać konto źródłowe, podać numer konta
 * odbiorcy oraz kwotę, a następnie zleca wykonanie przelewu w serwisie
 * bankowym (operacja transakcyjna z commit/rollback).
 */
public class TransferController {

    @FXML private ComboBox<BankAccount> accountCombo;
    @FXML private TextField recipientField;
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
        BankAccount source = accountCombo.getValue();
        if (source == null) {
            AlertUtil.error("Przelew", "Wybierz konto źródłowe.");
            return;
        }

        BigDecimal kwota = ValidationUtil.parseAmount(amountField.getText());
        if (!ValidationUtil.isPositiveAmount(kwota)) {
            AlertUtil.error("Przelew", "Podaj poprawną, dodatnią kwotę.");
            return;
        }

        try {
            bankService.transfer(source.getNrKonta(), recipientField.getText(), kwota);
            AlertUtil.info("Przelew", "Przelew został wykonany pomyślnie.");
            zamknij();
        } catch (BankException e) {
            AlertUtil.error("Przelew", e.getMessage());
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
