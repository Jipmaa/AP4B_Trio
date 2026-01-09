package view;

import controller.NavigationController;
import javafx.stage.Stage;

public class MenuView {
    private NavigationController navController;
    private Stage stage;

    public MenuView(NavigationController navController, Stage stage) {
        this.navController = navController;
        this.stage = stage;
    }

    public void show() {
        // Afficher directement la configuration de jeu en plein écran
        stage.setMaximized(true);
        navController.showGameSetup();
    }
}