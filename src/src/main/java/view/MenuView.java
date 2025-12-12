package view;

import controller.NavigationController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuView {
    private NavigationController navController;
    private Stage stage;

    public MenuView(NavigationController navController, Stage stage) {
        this.navController = navController;
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Button newGameButton = new Button("Nouvelle partie");
        newGameButton.setOnAction(e -> navController.showGameSetup());

        Button quitButton = new Button("Quitter");
        quitButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(newGameButton, quitButton);

        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Menu Principal");
        stage.show();
    }
}
