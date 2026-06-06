package com.simplebank.service;

import com.simplebank.dao.LoanDAO;
import com.simplebank.model.Loan;

import java.math.BigDecimal;
import java.util.List;

/*
 * Serwis obsługujący kredyty. Pozwala pobrać listę kredytów użytkownika
 * oraz złożyć nowy wniosek kredytowy. Oprocentowanie jest naliczane w sposób
 * uproszczony i zależy od liczby miesięcy spłaty.
 */
public class LoanService {

    private static final BigDecimal STOPA_BAZOWA = new BigDecimal("8.00");
    private static final int MAKS_MIESIECY = 120;

    private final LoanDAO loanDAO = new LoanDAO();

    public List<Loan> getLoans(int userId) {
        return loanDAO.findByUserId(userId);
    }

    /*
     * Składa nowy wniosek kredytowy ze statusem "OCZEKUJACY".
     * Stopa kredytu jest wyliczana jako stopa bazowa powiększona o niewielki
     * narzut zależny od długości okresu kredytowania.
     */
    public Loan applyForLoan(int userId, BigDecimal kwota, int miesiace) {
        if (kwota == null || kwota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankException("Kwota kredytu musi być dodatnia.");
        }
        if (miesiace <= 0 || miesiace > MAKS_MIESIECY) {
            throw new BankException("Liczba miesięcy musi mieścić się w zakresie 1-" + MAKS_MIESIECY + ".");
        }

        BigDecimal stopa = STOPA_BAZOWA.add(new BigDecimal(miesiace).divide(new BigDecimal(24)));

        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setKwota(kwota);
        loan.setStopaKredytu(stopa.setScale(2, java.math.RoundingMode.HALF_UP));
        loan.setMiesiace(miesiace);
        loan.setStatus("OCZEKUJACY");
        loan.setSplacone(BigDecimal.ZERO);
        loanDAO.insert(loan);
        return loan;
    }
}
