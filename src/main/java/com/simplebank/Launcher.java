package com.simplebank;

/*
 * Pomocnicza klasa startowa aplikacji.
 *
 * Uruchamianie bezpośrednio klasy dziedziczącej po javafx.application.Application
 * (czyli App) powoduje błąd "JavaFX runtime components are missing", gdy moduły
 * JavaFX znajdują się na classpath, a nie na module-path. Ta klasa nie dziedziczy
 * po Application, dzięki czemu maszyna wirtualna nie wymusza tej kontroli i może
 * poprawnie wystartować aplikację JavaFX z poziomu IDE.
 *
 * W IntelliJ uruchom metodę main TEJ klasy (Launcher), a nie klasy App.
 */
public class Launcher {

    public static void main(String[] args) {
        App.main(args);
    }
}
