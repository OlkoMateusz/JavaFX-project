package com.simplebank.service;

/*
 * Wyjątek sygnalizujący naruszenie reguły biznesowej (np. zbyt niskie saldo,
 * nieistniejące konto odbiorcy, błędne hasło). Komunikat tego wyjątku jest
 * przeznaczony do bezpośredniego wyświetlenia użytkownikowi w oknie Alert.
 */
public class BankException extends RuntimeException {

    public BankException(String message) {
        super(message);
    }
}
