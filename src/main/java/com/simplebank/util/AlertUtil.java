package com.simplebank.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/*
 * Klasa pomocnicza ułatwiająca wyświetlanie komunikatów dla użytkownika
 * przy pomocy okien dialogowych JavaFX (Alert). Ujednolica wygląd i sposób
 * prezentowania informacji, błędów oraz pytań potwierdzających.
 */
public final class AlertUtil {

    private AlertUtil() {
    }

    public static void info(String title, String message) {
        show(AlertType.INFORMATION, title, message);
    }

    public static void error(String title, String message) {
        show(AlertType.ERROR, title, message);
    }

    public static void warning(String title, String message) {
        show(AlertType.WARNING, title, message);
    }

    /*
     * Wyświetla okno z pytaniem i zwraca true, jeśli użytkownik potwierdził.
     */
    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void show(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
