package com.simplebank.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/*
 * Klasa zarządzająca przełączaniem widoków aplikacji. Przechowuje referencję
 * do głównego okna (Stage) i pozwala wczytywać kolejne widoki FXML, a także
 * otwierać okna dialogowe (np. przelew, wpłata) w trybie modalnym.
 *
 * Wszystkie sceny otrzymują automatycznie wspólny arkusz stylów CSS.
 */
public final class SceneManager {

    private static final String VIEW_PATH = "/com/simplebank/view/";
    private static final String CSS_PATH = "/com/simplebank/css/styles.css";

    private static Stage primaryStage;

    private SceneManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /*
     * Wczytuje wskazany widok FXML i ustawia go jako bieżącą scenę głównego okna.
     */
    public static void switchScene(String fxmlName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(VIEW_PATH + fxmlName));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            applyStyles(scene);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            AlertUtil.error("Błąd widoku", "Nie udało się wczytać widoku: " + fxmlName
                    + "\n" + e.getMessage());
        }
    }

    /*
     * Otwiera widok jako modalne okno dialogowe i czeka na jego zamknięcie.
     * Wykorzystywane dla operacji bankowych uruchamianych z panelu klienta.
     */
    public static void openDialog(String fxmlName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(VIEW_PATH + fxmlName));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            applyStyles(scene);

            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (IOException e) {
            AlertUtil.error("Błąd okna", "Nie udało się otworzyć okna: " + fxmlName
                    + "\n" + e.getMessage());
        }
    }

    private static void applyStyles(Scene scene) {
        scene.getStylesheets().add(
                Objects.requireNonNull(SceneManager.class.getResource(CSS_PATH)).toExternalForm());
    }
}
