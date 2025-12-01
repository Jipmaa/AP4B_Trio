package view;

import controller.NavigationController;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainView {

    private final Stage primaryStage;
    private final NavigationController navigationController;

    public MainView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.navigationController = new NavigationController(this);
        primaryStage.setTitle("Trio Game - UTBM Edition");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
    }

    public void showSetupScene() {
        SetupView setupView = new SetupView(navigationController);
        Scene setupScene = new Scene(setupView.getView(), primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(setupScene);
        primaryStage.show();
    }

    public void showGameScene() {
        // This will be implemented later, after SetupView passes game configuration
        // For now, just a placeholder
        System.out.println("Showing Game Scene (placeholder)");
    }

    public void showEndGameScene() {
        // This will be implemented later
        System.out.println("Showing End Game Scene (placeholder)");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
