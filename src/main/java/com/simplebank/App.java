package com.simplebank;

import com.simplebank.util.AlertUtil;
import com.simplebank.util.DatabaseInitializer;
import com.simplebank.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/*
 * Główna klasa aplikacji Simple Bank. Przy starcie automatycznie sprawdza i w
 * razie potrzeby inicjalizuje bazę danych (migracja oraz dane testowe), a
 * następnie wyświetla ekran logowania jako widok startowy.
 *
 * Jeżeli połączenie z serwerem MySQL nie powiedzie się, użytkownik otrzymuje
 * czytelny komunikat, a aplikacja zostaje zamknięta.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseInitializer.initialize();
        } catch (RuntimeException e) {
            AlertUtil.error("Błąd bazy danych", e.getMessage());
            Platform.exit();
            return;
        }

        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchScene("login.fxml", "Simple Bank - Logowanie");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
