package com.simplebank.model;

import java.time.LocalDate;

/*
 * Model reprezentujący kartę płatniczą (tabela karty).
 * Karta jest powiązana z konkretnym kontem bankowym (idKonta).
 */
public class Card {

    private int idKarty;
    private String idKonta;
    private String nrKarty;
    private String cvv;
    private LocalDate dataWaznosci;
    private String status;

    public Card() {
    }

    public int getIdKarty() {
        return idKarty;
    }

    public void setIdKarty(int idKarty) {
        this.idKarty = idKarty;
    }

    public String getIdKonta() {
        return idKonta;
    }

    public void setIdKonta(String idKonta) {
        this.idKonta = idKonta;
    }

    public String getNrKarty() {
        return nrKarty;
    }

    public void setNrKarty(String nrKarty) {
        this.nrKarty = nrKarty;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDate getDataWaznosci() {
        return dataWaznosci;
    }

    public void setDataWaznosci(LocalDate dataWaznosci) {
        this.dataWaznosci = dataWaznosci;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*
     * Zwraca numer karty w czytelnym formacie z grupowaniem po cztery cyfry.
     */
    public String getNrKartyFormatted() {
        if (nrKarty == null || nrKarty.length() != 16) {
            return nrKarty;
        }
        return nrKarty.replaceAll("(.{4})", "$1 ").trim();
    }
}
