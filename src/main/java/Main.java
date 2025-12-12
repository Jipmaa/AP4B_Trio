package main;

import controller.NavigationController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new javafx.scene.layout.StackPane(), 1200, 800);
        
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.setScene(scene);
        stage.setTitle("Trio - Le Jeu de Mémoire");
        stage.setResizable(true);

        NavigationController nav = new NavigationController(stage);
        nav.showMainMenu();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}