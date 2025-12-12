package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Player;

public class PlayerView extends VBox {

    private Player player;
    private Label nameLabel;
    private Label scoreLabel;
    private Circle statusCircle;

    public PlayerView(Player player) {
        this.player = player;

        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(12));
        setSpacing(6);
        setStyle(
                "-fx-background-color: rgba(50, 55, 65, 0.9);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #4CAF50;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;"
        );

        // Indicateur de statut (joueur actif)
        statusCircle = new Circle(6);
        statusCircle.setFill(Color.web("#4CAF50"));
        statusCircle.setVisible(false);

        // Nom du joueur
        HBox nameBox = new HBox(8);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.WHITE);
        nameBox.getChildren().addAll(statusCircle, nameLabel);

        // Score
        scoreLabel = new Label("Score: " + player.getScore());
        scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        scoreLabel.setTextFill(Color.web("#B0B0B0"));

        getChildren().addAll(nameBox, scoreLabel);
    }

    public void setActive(boolean active) {
        statusCircle.setVisible(active);
        if (active) {
            setStyle(
                    "-fx-background-color: rgba(76, 175, 80, 0.3);" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #4CAF50;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 10;"
            );
        } else {
            setStyle(
                    "-fx-background-color: rgba(50, 55, 65, 0.9);" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #4CAF50;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;"
            );
        }
    }

    public void refresh() {
        scoreLabel.setText("Score: " + player.getScore());
    }

    public Player getPlayer() {
        return player;
    }
}