package com.simplebank.service;

import com.simplebank.dao.CardDAO;
import com.simplebank.model.Card;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

/*
 * Serwis obsługujący karty płatnicze: pobieranie listy kart użytkownika
 * oraz generowanie nowej karty dla wskazanego konta. Numer karty oraz kod CVV
 * są losowane, a numer karty jest unikalny w skali całego banku.
 */
public class CardService {

    private static final int LATA_WAZNOSCI = 4;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CardDAO cardDAO = new CardDAO();

    public List<Card> getCards(int userId) {
        return cardDAO.findByUserId(userId);
    }

    /*
     * Tworzy i zapisuje nową kartę dla wskazanego konta.
     */
    public Card generateCard(String nrKonta) {
        if (nrKonta == null || nrKonta.trim().isEmpty()) {
            throw new BankException("Brak konta, do którego można wydać kartę.");
        }

        Card card = new Card();
        card.setIdKonta(nrKonta);
        card.setNrKarty(wygenerujNumerKarty());
        card.setCvv(String.format("%03d", RANDOM.nextInt(1000)));
        card.setDataWaznosci(LocalDate.now().plusYears(LATA_WAZNOSCI));
        card.setStatus("AKTYWNA");
        cardDAO.insert(card);
        return card;
    }

    private String wygenerujNumerKarty() {
        String numer;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(RANDOM.nextInt(10));
            }
            numer = sb.toString();
        } while (cardDAO.cardNumberExists(numer));
        return numer;
    }
}
