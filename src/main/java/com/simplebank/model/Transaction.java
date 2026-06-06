package com.simplebank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Model reprezentujący pojedynczą transakcję (tabela transakcje).
 * Konto nadawcy lub odbiorcy może być puste (null) w zależności od typu
 * operacji (wpłata, wypłata, przelew). Pole nazwaTypu jest uzupełniane
 * przy złączeniu ze słownikiem typów transakcji.
 */
public class Transaction {

    private int idTransakcji;
    private String fromNrKonta;
    private String toNrKonta;
    private BigDecimal kwota;
    private int typTransakcji;
    private LocalDateTime dataTransakcji;
    private String nazwaTypu;

    public Transaction() {
    }

    public int getIdTransakcji() {
        return idTransakcji;
    }

    public void setIdTransakcji(int idTransakcji) {
        this.idTransakcji = idTransakcji;
    }

    public String getFromNrKonta() {
        return fromNrKonta;
    }

    public void setFromNrKonta(String fromNrKonta) {
        this.fromNrKonta = fromNrKonta;
    }

    public String getToNrKonta() {
        return toNrKonta;
    }

    public void setToNrKonta(String toNrKonta) {
        this.toNrKonta = toNrKonta;
    }

    public BigDecimal getKwota() {
        return kwota;
    }

    public void setKwota(BigDecimal kwota) {
        this.kwota = kwota;
    }

    public int getTypTransakcji() {
        return typTransakcji;
    }

    public void setTypTransakcji(int typTransakcji) {
        this.typTransakcji = typTransakcji;
    }

    public LocalDateTime getDataTransakcji() {
        return dataTransakcji;
    }

    public void setDataTransakcji(LocalDateTime dataTransakcji) {
        this.dataTransakcji = dataTransakcji;
    }

    public String getNazwaTypu() {
        return nazwaTypu;
    }

    public void setNazwaTypu(String nazwaTypu) {
        this.nazwaTypu = nazwaTypu;
    }
}
