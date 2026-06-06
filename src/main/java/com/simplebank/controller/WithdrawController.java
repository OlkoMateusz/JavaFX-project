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
 * Kontroler okna wypłaty gotówki. Pozwala wybrać konto i podać kwotę, a
 * następnie zleca serwisowi bankowemu wypłatę po sprawdzeniu salda.
 */
public class WithdrawController {

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
            AlertUtil.error("Wypłata", "Wybierz konto.");
            return;
        }

        BigDecimal kwota = ValidationUtil.parseAmount(amountField.getText());
        if (!ValidationUtil.isPositiveAmount(kwota)) {
            AlertUtil.error("Wypłata", "Podaj poprawną, dodatnią kwotę.");
            return;
        }

        try {
            bankService.withdraw(account.getNrKonta(), kwota);
            AlertUtil.info("Wypłata", "Wypłata została zrealizowana.");
            zamknij();
        } catch (BankException e) {
            AlertUtil.error("Wypłata", e.getMessage());
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
