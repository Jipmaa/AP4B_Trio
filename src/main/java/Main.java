package main;

import controller.NavigationController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new javafx.scene.layout.StackPane(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Trio");

        NavigationController nav = new NavigationController(stage);
        nav.startNormalGame();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
