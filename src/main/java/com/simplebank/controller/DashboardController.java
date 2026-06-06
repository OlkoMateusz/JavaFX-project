package com.simplebank.controller;

import com.simplebank.model.BankAccount;
import com.simplebank.model.Transaction;
import com.simplebank.model.User;
import com.simplebank.service.BankService;
import com.simplebank.util.AlertUtil;
import com.simplebank.util.SceneManager;
import com.simplebank.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

/*
 * Kontroler głównego panelu klienta. Po zalogowaniu prezentuje dane
 * użytkownika, jego konta bankowe oraz historię transakcji. Udostępnia
 * przyciski uruchamiające operacje bankowe (przelew, wpłata, wypłata),
 * a po każdej operacji odświeża prezentowane dane.
 */
public class DashboardController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private Label lblWelcome;
    @FXML private Label lblImieNazwisko;
    @FXML private Label lblEmail;
    @FXML private Label lblPesel;

    @FXML private TableView<BankAccount> tableAccounts;
    @FXML private TableColumn<BankAccount, String> colNrKonta;
    @FXML private TableColumn<BankAccount, String> colTypKonta;
    @FXML private TableColumn<BankAccount, String> colSaldo;
    @FXML private TableColumn<BankAccount, String> colStatus;

    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, String> colData;
    @FXML private TableColumn<Transaction, String> colTyp;
    @FXML private TableColumn<Transaction, String> colKwota;
    @FXML private TableColumn<Transaction, String> colFrom;
    @FXML private TableColumn<Transaction, String> colTo;

    private final BankService bankService = new BankService();
    private User currentUser;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getCurrentUser();

        konfigurujKolumnyKont();
        konfigurujKolumnyTransakcji();

        lblWelcome.setText("Witaj, " + currentUser.getImie() + "!");
        lblImieNazwisko.setText(currentUser.getPelneImieNazwisko());
        lblEmail.setText(currentUser.getEmail());
        lblPesel.setText(currentUser.getPesel());

        refresh();
    }

    private void konfigurujKolumnyKont() {
        colNrKonta.setCellValueFactory(new PropertyValueFactory<>("nrKonta"));
        colTypKonta.setCellValueFactory(new PropertyValueFactory<>("nazwaTypu"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colSaldo.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(formatKwota(data.getValue().getSaldo())));
    }

    private void konfigurujKolumnyTransakcji() {
        colTyp.setCellValueFactory(new PropertyValueFactory<>("nazwaTypu"));
        colData.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDataTransakcji().format(DATE_FORMAT)));
        colKwota.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(formatKwota(data.getValue().getKwota())));
        colFrom.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(czyPuste(data.getValue().getFromNrKonta())));
        colTo.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(czyPuste(data.getValue().getToNrKonta())));
    }

    /*
     * Pobiera z bazy aktualne konta i transakcje użytkownika i wypełnia tabele.
     */
    public void refresh() {
        List<BankAccount> accounts = bankService.getAccounts(currentUser.getUserId());
        tableAccounts.setItems(FXCollections.observableArrayList(accounts));

        List<Transaction> transactions = bankService.getTransactions(currentUser.getUserId());
        tableTransactions.setItems(FXCollections.observableArrayList(transactions));
    }

    @FXML
    private void handleTransfer() {
        SceneManager.openDialog("transfer.fxml", "Przelew");
        refresh();
    }

    @FXML
    private void handleDeposit() {
        SceneManager.openDialog("deposit.fxml", "Wpłata gotówki");
        refresh();
    }

    @FXML
    private void handleWithdraw() {
        SceneManager.openDialog("withdraw.fxml", "Wypłata gotówki");
        refresh();
    }

    @FXML
    private void handleRefresh() {
        refresh();
    }

    @FXML
    private void handleLogout() {
        boolean confirmed = AlertUtil.confirm("Wylogowanie", "Czy na pewno chcesz się wylogować?");
        if (confirmed) {
            SessionManager.logout();
            SceneManager.switchScene("login.fxml", "Simple Bank - Logowanie");
        }
    }

    private String formatKwota(java.math.BigDecimal kwota) {
        return String.format("%,.2f zł", kwota);
    }

    private String czyPuste(String value) {
        return value == null ? "-" : value;
    }
}
