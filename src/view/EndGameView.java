package view;

import controller.NavigationController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EndGameView {

    private final NavigationController navigationController;
    private final VBox view;

    public EndGameView(String winnerName, NavigationController navigationController) {
        this.navigationController = navigationController;
        this.view = new VBox(20);
        initializeView(winnerName);
    }

    private void initializeView(String winnerName) {
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #336699;"); // Same background as setup

        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gameOverLabel.setTextFill(Color.RED);

        Label winnerLabel = new Label("Winner: " + winnerName + "!");
        winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        winnerLabel.setTextFill(Color.YELLOW);

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-font-size: 24px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        playAgainButton.setOnAction(e -> navigationController.mainView.showSetupScene()); // Go back to setup

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 24px; -fx-background-color: #F44336; -fx-text-fill: white;");
        exitButton.setOnAction(e -> System.exit(0));

        view.getChildren().addAll(gameOverLabel, winnerLabel, playAgainButton, exitButton);
    }

    public VBox getView() {
        return view;
    }
}
