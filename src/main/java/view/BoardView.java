package view;

import controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Card;
import model.Game;
import model.Player;
import model.Team;

public class BoardView extends VBox {

    private Game game;
    private GameController controller;
    private GridPane centerGrid;
    private VBox playersHandsBox;

    public BoardView(Game game, GameController controller) {
        this.game = game;
        this.controller = controller;

        setSpacing(30);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));

        // Section pour les mains des joueurs
        playersHandsBox = new VBox(20);
        playersHandsBox.setAlignment(Pos.CENTER);

        // Section pour les cartes au centre (mode solo uniquement)
        centerGrid = new GridPane();
        centerGrid.setPadding(new Insets(20));
        centerGrid.setHgap(15);
        centerGrid.setVgap(15);
        centerGrid.setAlignment(Pos.CENTER);

        refresh();

        if (game.getMode() != Game.Mode.TEAM) {
            Label centerLabel = new Label("CARTES AU CENTRE");
            centerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            centerLabel.setTextFill(Color.web("#FFD700"));

            VBox centerBox = new VBox(10);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.getChildren().addAll(centerLabel, centerGrid);

            getChildren().addAll(playersHandsBox, centerBox);
        } else {
            getChildren().add(playersHandsBox);
        }
    }

    // Dans view/BoardView.java

    private void updateExchangeUI(VBox playerBox, Player player) {
        if (game.getMode() == Game.Mode.TEAM && game.getCurrentPlayer().equals(player)) {
            Team team = player.getTeam();
            if (team != null && team.canExchange()) {
                Button btnExchange = new Button("Échanger une carte");
                btnExchange.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");

                btnExchange.setOnAction(e -> {
                    // On informe l'utilisateur qu'il doit cliquer sur une de SES cartes
                    btnExchange.setText("Cliquez sur une de vos cartes...");
                    btnExchange.setDisable(true);
                    // On peut ajouter un flag dans le controller pour intercepter le prochain clic
                    controller.setExchangeMode(true);
                });
                playerBox.getChildren().add(btnExchange);
            }
        }
    }

    public void refresh() {
        playersHandsBox.getChildren().clear();
        centerGrid.getChildren().clear();

        // Afficher les mains des joueurs
        for (Player player : game.getPlayers()) {
            VBox playerHandBox = createPlayerHandBox(player);
            playersHandsBox.getChildren().add(playerHandBox);
        }

        // Afficher les cartes au centre (mode solo uniquement)
        if (game.getMode() != Game.Mode.TEAM) {
            int col = 0;
            int row = 0;

            for (Card card : game.getBoard().getCenterCards()) {
                CardView cv = new CardView(card);
                cv.updateImage();

                // Les cartes du plateau sont toujours cliquables (sauf si animation en cours)
                boolean canClick = !controller.isProcessing() && !game.getRevealedCards().contains(card);
                cv.setClickable(canClick);

                cv.setOnCardClick(() -> {
                    controller.handleCardClick(card);
                });

                centerGrid.add(cv, col, row);

                col++;
                if (col >= 6) {
                    col = 0;
                    row++;
                }
            }
        }
    }

    private VBox createPlayerHandBox(Player player) {
        VBox playerBox = new VBox(10);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(15));

        // Style différent pour le joueur actuel
        boolean isCurrentPlayer = game.getCurrentPlayer().equals(player);
        if (isCurrentPlayer) {
            playerBox.setStyle(
                    "-fx-background-color: rgba(76, 175, 80, 0.3);" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: #4CAF50;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 15;"
            );
        } else {
            playerBox.setStyle(
                    "-fx-background-color: rgba(50, 55, 65, 0.7);" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: #757575;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 15;"
            );
        }

        // Nom du joueur
        Label nameLabel = new Label(player.getName() + (isCurrentPlayer ? " (À TON TOUR)" : ""));
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(isCurrentPlayer ? Color.web("#4CAF50") : Color.WHITE);

        // Cartes du joueur
        HBox cardsBox = new HBox(10);
        cardsBox.setAlignment(Pos.CENTER);

        for (Card card : player.getHand()) {
            CardView cv = new CardView(card);

            // On indique à la vue si elle doit forcer l'affichage de la face
            // même si la carte n'est pas "retournée" au sens du jeu.
            if (isCurrentPlayer) {
                cv.setForceVisible(true);
            }


            cv.updateImage();

            // Déterminer si la carte est cliquable
            // On peut cliquer sur les cartes de N'IMPORTE QUEL joueur si elles sont aux extrémités
            boolean canClick =
                    !controller.isProcessing()
                            && player.canFlipCard(card);

            cv.setClickable(canClick);

            // Ajouter un indicateur visuel si la carte peut être retournée
            if (canClick) {
                cv.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 3; -fx-border-radius: 6; -fx-background-radius: 6;");
            }

            cv.setOnCardClick(() -> {
                controller.handleCardClick(card);
            });

            cardsBox.getChildren().add(cv);
        }

        playerBox.getChildren().addAll(nameLabel, cardsBox);
        return playerBox;
    }
}