package com.simplebank.model;

import java.math.BigDecimal;

/*
 * Model reprezentujący typ konta bankowego (tabela typ_konta).
 * Określa nazwę produktu, oprocentowanie oraz dostępność kredytu.
 */
public class AccountType {

    private int rodzajKonta;
    private String nazwa;
    private BigDecimal oprocentowanie;
    private boolean mozliwoscKredytu;

    public AccountType() {
    }

    public int getRodzajKonta() {
        return rodzajKonta;
    }

    public void setRodzajKonta(int rodzajKonta) {
        this.rodzajKonta = rodzajKonta;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public BigDecimal getOprocentowanie() {
        return oprocentowanie;
    }

    public void setOprocentowanie(BigDecimal oprocentowanie) {
        this.oprocentowanie = oprocentowanie;
    }

    public boolean isMozliwoscKredytu() {
        return mozliwoscKredytu;
    }

    public void setMozliwoscKredytu(boolean mozliwoscKredytu) {
        this.mozliwoscKredytu = mozliwoscKredytu;
    }

    @Override
    public String toString() {
        return nazwa;
    }
}
