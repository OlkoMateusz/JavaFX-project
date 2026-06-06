package com.simplebank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Model reprezentujący konto bankowe (tabela konta_bankowe).
 * Numer konta (nrKonta) jednoznacznie identyfikuje konto w systemie.
 * Pole nazwaTypu jest uzupełniane przy złączeniu z tabelą typ_konta i
 * służy wyłącznie do prezentacji w interfejsie.
 */
public class BankAccount {

    private String nrKonta;
    private int rodzajKonta;
    private BigDecimal saldo;
    private int userId;
    private String status;
    private LocalDateTime dataUtworzenia;
    private String nazwaTypu;

    public BankAccount() {
    }

    public String getNrKonta() {
        return nrKonta;
    }

    public void setNrKonta(String nrKonta) {
        this.nrKonta = nrKonta;
    }

    public int getRodzajKonta() {
        return rodzajKonta;
    }

    public void setRodzajKonta(int rodzajKonta) {
        this.rodzajKonta = rodzajKonta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataUtworzenia() {
        return dataUtworzenia;
    }

    public void setDataUtworzenia(LocalDateTime dataUtworzenia) {
        this.dataUtworzenia = dataUtworzenia;
    }

    public String getNazwaTypu() {
        return nazwaTypu;
    }

    public void setNazwaTypu(String nazwaTypu) {
        this.nazwaTypu = nazwaTypu;
    }

    @Override
    public String toString() {
        return nrKonta + " (" + nazwaTypu + ")";
    }
}
