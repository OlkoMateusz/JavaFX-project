package com.simplebank.controller;

import com.simplebank.model.BankAccount;
import com.simplebank.model.Card;
import com.simplebank.service.BankException;
import com.simplebank.service.BankService;
import com.simplebank.service.CardService;
import com.simplebank.util.AlertUtil;
import com.simplebank.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

/*
 * Kontroler zakładki kart płatniczych. Wyświetla karty przypisane do kont
 * użytkownika oraz umożliwia wygenerowanie nowej karty dla wybranego konta.
 */
public class CardsController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ComboBox<BankAccount> accountCombo;
    @FXML private TableView<Card> cardsTable;
    @FXML private TableColumn<Card, String> colNrKarty;
    @FXML private TableColumn<Card, String> colCvv;
    @FXML private TableColumn<Card, String> colWaznosc;
    @FXML private TableColumn<Card, String> colStatus;
    @FXML private TableColumn<Card, String> colKonto;

    private final CardService cardService = new CardService();
    private final BankService bankService = new BankService();
    private int userId;

    @FXML
    private void initialize() {
        userId = SessionManager.getCurrentUser().getUserId();

        colNrKarty.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getNrKartyFormatted()));
        colCvv.setCellValueFactory(new PropertyValueFactory<>("cvv"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colKonto.setCellValueFactory(new PropertyValueFactory<>("idKonta"));
        colWaznosc.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDataWaznosci().format(DATE_FORMAT)));

        accountCombo.setItems(FXCollections.observableArrayList(bankService.getAccounts(userId)));
        if (!accountCombo.getItems().isEmpty()) {
            accountCombo.getSelectionModel().selectFirst();
        }
        refresh();
    }

    @FXML
    private void handleGenerateCard() {
        BankAccount account = accountCombo.getValue();
        if (account == null) {
            AlertUtil.error("Karty", "Wybierz konto, do którego ma zostać wydana karta.");
            return;
        }
        try {
            Card card = cardService.generateCard(account.getNrKonta());
            AlertUtil.info("Nowa karta",
                    "Wygenerowano kartę:\n" + card.getNrKartyFormatted()
                            + "\nCVV: " + card.getCvv()
                            + "\nWażna do: " + card.getDataWaznosci().format(DATE_FORMAT));
            refresh();
        } catch (BankException e) {
            AlertUtil.error("Karty", e.getMessage());
        }
    }

    private void refresh() {
        cardsTable.setItems(FXCollections.observableArrayList(cardService.getCards(userId)));
    }
}
