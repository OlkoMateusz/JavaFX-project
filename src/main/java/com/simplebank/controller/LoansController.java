package com.simplebank.controller;

import com.simplebank.model.Loan;
import com.simplebank.service.BankException;
import com.simplebank.service.LoanService;
import com.simplebank.util.AlertUtil;
import com.simplebank.util.SessionManager;
import com.simplebank.util.ValidationUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/*
 * Kontroler zakładki kredytów. Umożliwia złożenie wniosku kredytowego
 * (podanie kwoty i liczby miesięcy) oraz prezentuje listę kredytów
 * użytkownika wraz z ich statusem i kwotą spłaconą.
 */
public class LoansController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private TextField kwotaField;
    @FXML private TextField miesiaceField;

    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, String> colKwota;
    @FXML private TableColumn<Loan, String> colStopa;
    @FXML private TableColumn<Loan, String> colMiesiace;
    @FXML private TableColumn<Loan, String> colStatus;
    @FXML private TableColumn<Loan, String> colSplacone;
    @FXML private TableColumn<Loan, String> colDoSplaty;
    @FXML private TableColumn<Loan, String> colData;

    private final LoanService loanService = new LoanService();
    private int userId;

    @FXML
    private void initialize() {
        userId = SessionManager.getCurrentUser().getUserId();

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colMiesiace.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(String.valueOf(data.getValue().getMiesiace())));
        colKwota.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(formatKwota(data.getValue().getKwota())));
        colStopa.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getStopaKredytu() + " %"));
        colSplacone.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(formatKwota(data.getValue().getSplacone())));
        colDoSplaty.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(formatKwota(data.getValue().getCalkowitaKwota())));
        colData.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDataKredytu().format(DATE_FORMAT)));

        refresh();
    }

    @FXML
    private void handleApply() {
        BigDecimal kwota = ValidationUtil.parseAmount(kwotaField.getText());
        if (!ValidationUtil.isPositiveAmount(kwota)) {
            AlertUtil.error("Kredyt", "Podaj poprawną, dodatnią kwotę kredytu.");
            return;
        }

        int miesiace;
        try {
            miesiace = Integer.parseInt(miesiaceField.getText().trim());
        } catch (NumberFormatException e) {
            AlertUtil.error("Kredyt", "Liczba miesięcy musi być liczbą całkowitą.");
            return;
        }

        try {
            loanService.applyForLoan(userId, kwota, miesiace);
            AlertUtil.info("Kredyt", "Wniosek kredytowy został złożony (status: OCZEKUJACY).");
            kwotaField.clear();
            miesiaceField.clear();
            refresh();
        } catch (BankException e) {
            AlertUtil.error("Kredyt", e.getMessage());
        }
    }

    private void refresh() {
        loansTable.setItems(FXCollections.observableArrayList(loanService.getLoans(userId)));
    }

    private String formatKwota(BigDecimal kwota) {
        return String.format("%,.2f zł", kwota);
    }
}
