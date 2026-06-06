package com.simplebank.model;

import java.time.LocalDateTime;

/*
 * Model reprezentujący użytkownika aplikacji (tabela uzytkownicy).
 * Przechowuje dane osobowe oraz zahashowane hasło wraz z solą.
 */
public class User {

    private int userId;
    private String imie;
    private String nazwisko;
    private String pesel;
    private String email;
    private String hasloHash;
    private String hasloSol;
    private LocalDateTime dataUtworzenia;

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHasloHash() {
        return hasloHash;
    }

    public void setHasloHash(String hasloHash) {
        this.hasloHash = hasloHash;
    }

    public String getHasloSol() {
        return hasloSol;
    }

    public void setHasloSol(String hasloSol) {
        this.hasloSol = hasloSol;
    }

    public LocalDateTime getDataUtworzenia() {
        return dataUtworzenia;
    }

    public void setDataUtworzenia(LocalDateTime dataUtworzenia) {
        this.dataUtworzenia = dataUtworzenia;
    }

    public String getPelneImieNazwisko() {
        return imie + " " + nazwisko;
    }
}
