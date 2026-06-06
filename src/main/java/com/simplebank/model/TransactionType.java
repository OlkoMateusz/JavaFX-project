package com.simplebank.model;

import java.math.BigDecimal;

/*
 * Model reprezentujący typ transakcji (tabela typy_transakcji).
 * Zawiera nazwę operacji, czas realizacji oraz ewentualną opłatę.
 */
public class TransactionType {

    private int typTransakcji;
    private String nazwa;
    private int czasRealizacji;
    private BigDecimal oplata;

    public TransactionType() {
    }

    public int getTypTransakcji() {
        return typTransakcji;
    }

    public void setTypTransakcji(int typTransakcji) {
        this.typTransakcji = typTransakcji;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public int getCzasRealizacji() {
        return czasRealizacji;
    }

    public void setCzasRealizacji(int czasRealizacji) {
        this.czasRealizacji = czasRealizacji;
    }

    public BigDecimal getOplata() {
        return oplata;
    }

    public void setOplata(BigDecimal oplata) {
        this.oplata = oplata;
    }

    @Override
    public String toString() {
        return nazwa;
    }
}
