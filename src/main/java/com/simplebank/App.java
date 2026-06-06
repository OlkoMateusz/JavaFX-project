package com.simplebank;

import com.simplebank.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/*
 * Główna klasa aplikacji Simple Bank. Inicjalizuje okno aplikacji, przekazuje
 * je do menedżera scen i wyświetla ekran logowania jako widok startowy.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchScene("login.fxml", "Simple Bank - Logowanie");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
