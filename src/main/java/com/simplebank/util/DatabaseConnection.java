package com.simplebank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Klasa odpowiedzialna za nawiązywanie połączenia z lokalną bazą danych MySQL
 * uruchomioną w środowisku XAMPP.
 *
 * Dane połączenia są zgodne z domyślną konfiguracją XAMPP:
 *  - host: localhost, port: 3306
 *  - baza: bank_app
 *  - użytkownik: root, hasło: puste
 *
 * Jeżeli zmienisz dane dostępowe do bazy, popraw stałe poniżej.
 */
public final class DatabaseConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "bank_app";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static final String PARAMS =
            "?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=Europe/Warsaw&characterEncoding=UTF-8";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + PARAMS;

    private static final String SERVER_URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + PARAMS;

    private DatabaseConnection() {
    }

    public static String getDatabaseName() {
        return DATABASE;
    }

    /*
     * Tworzy i zwraca nowe połączenie z bazą danych.
     * Każde wywołanie zwraca świeże połączenie, które wywołujący powinien
     * zamknąć (najlepiej w bloku try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
     * Tworzy połączenie z serwerem MySQL bez wskazywania konkretnej bazy danych.
     * Wykorzystywane przy automatycznej inicjalizacji, gdy baza może jeszcze
     * nie istnieć (np. do wykonania polecenia CREATE DATABASE).
     */
    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
    }
}
