package view;

import controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Game;
import model.Player;
import model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameView extends BorderPane {

    private BoardView boardView;
    private Label turnsLabel;
    private Label modeLabel;
    private List<PlayerView> playerViews;
    private VBox scoreboardBox;
    private Game game;
    private GameController controller;

    public GameView(Game game, GameController controller) {
        this.game = game;
        this.controller = controller;
        this.boardView = new BoardView(game.getBoard(), controller);
        this.playerViews = new ArrayList<>();

        // Background
        try {
            Image bgImg = new Image(getClass().getResourceAsStream("/images/franck.jpg"));
            BackgroundImage background = new BackgroundImage(
                    bgImg,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            setBackground(new Background(background));
        } catch (Exception e) {
            setBackground(new Background(new BackgroundFill(
                    Color.rgb(20, 25, 35), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        // Overlay
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        StackPane root = new StackPane(overlay, createMainLayout());
        setCenter(root);
    }

    private BorderPane createMainLayout() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        // Top bar
        HBox topBar = createTopBar();
        layout.setTop(topBar);

        // Center: Board
        StackPane centerPane = new StackPane(boardView);
        centerPane.setPadding(new Insets(10));
        layout.setCenter(centerPane);

        // Right: Scoreboard
        scoreboardBox = createScoreboard();
        layout.setRight(scoreboardBox);

        return layout;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(30);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle(
                "-fx-background-color: rgba(30, 35, 45, 0.95);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;"
        );
        topBar.setEffect(new DropShadow(10, Color.BLACK));

        // Mode
        modeLabel = new Label(getModeText());
        modeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        modeLabel.setTextFill(Color.web("#FFD700"));

        // Tour actuel
        turnsLabel = new Label("Tour: " + game.getCurrentPlayer().getName());
        turnsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        turnsLabel.setTextFill(Color.WHITE);

        // Cartes révélées
        Label revealedLabel = new Label("Cartes: " + game.getRevealedCards().size() + "/3");
        revealedLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        revealedLabel.setTextFill(Color.web("#4CAF50"));

        // Boutons
        Button endTurnBtn = createTopButton("Passer le Tour", "#FF9800");
        Button menuBtn = createTopButton("Menu", "#757575");

        endTurnBtn.setOnAction(e -> controller.endTurn());
        menuBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Retour au menu");
            alert.setContentText("Êtes-vous sûr de vouloir quitter la partie ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.returnToMenu();
            }
        });

        topBar.getChildren().addAll(
                modeLabel,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                turnsLabel,
                revealedLabel,
                new Region(), // Spacer
                endTurnBtn,
                menuBtn
        );

        HBox.setHgrow(topBar.getChildren().get(topBar.getChildren().size() - 3), Priority.ALWAYS);

        return topBar;
    }

    private Button createTopButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setPrefHeight(35);
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setScaleX(1.05));
        btn.setOnMouseExited(e -> btn.setScaleX(1.0));
        return btn;
    }

    private VBox createScoreboard() {
        VBox scoreboard = new VBox(15);
        scoreboard.setPadding(new Insets(20));
        scoreboard.setAlignment(Pos.TOP_CENTER);
        scoreboard.setPrefWidth(280);
        scoreboard.setStyle(
                "-fx-background-color: rgba(30, 35, 45, 0.95);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;"
        );
        scoreboard.setEffect(new DropShadow(10, Color.BLACK));

        Label title = new Label("🏆 SCORES 🏆");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#FFD700"));

        scoreboard.getChildren().add(title);
        scoreboard.getChildren().add(new Separator());

        if (game.getMode() == Game.Mode.TEAM) {
            // Affichage par équipe
            for (Team team : game.getTeams()) {
                VBox teamBox = createTeamScoreBox(team);
                scoreboard.getChildren().add(teamBox);
            }
        } else {
            // Affichage par joueur
            for (Player player : game.getPlayers()) {
                PlayerView pv = new PlayerView(player);
                playerViews.add(pv);
                scoreboard.getChildren().add(pv);
            }
        }

        return scoreboard;
    }

    private VBox createTeamScoreBox(Team team) {
        VBox teamBox = new VBox(8);
        teamBox.setPadding(new Insets(12));
        teamBox.setStyle(
                "-fx-background-color: rgba(50, 55, 65, 0.9);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #4CAF50;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;"
        );

        Label teamLabel = new Label(team.getName());
        teamLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        teamLabel.setTextFill(Color.web("#4CAF50"));

        Label teamScore = new Label("Score: " + team.getScore());
        teamScore.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        teamScore.setTextFill(Color.WHITE);

        teamBox.getChildren().addAll(teamLabel, teamScore);

        // Ajouter les joueurs de l'équipe
        for (Player player : team.getPlayers()) {
            Label playerLabel = new Label("  • " + player.getName() + ": " + player.getScore());
            playerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            playerLabel.setTextFill(Color.web("#B0B0B0"));
            teamBox.getChildren().add(playerLabel);
        }

        return teamBox;
    }

    private String getModeText() {
        String base = game.getMode() == Game.Mode.TEAM ? "MODE ÉQUIPES" : "MODE SOLO";
        if (game.getMode() == Game.Mode.PICANTE) {
            base += " 🌶️ PICANTE";
        }
        return base;
    }

    public void refresh(Game game) {
        this.game = game;
        turnsLabel.setText("Tour: " + game.getCurrentPlayer().getName());

        // Update revealed cards count
        ((Label) ((HBox) getTop()).getChildren().get(4)).setText(
                "Cartes: " + game.getRevealedCards().size() + "/3"
        );

        boardView.refresh();

        // Refresh scoreboard
        BorderPane layout = (BorderPane) ((StackPane) getCenter()).getChildren().get(1);
        layout.setRight(createScoreboard());
    }

    public void showGameOverDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partie Terminée !");
        alert.setHeaderText("🎉 FIN DE LA PARTIE 🎉");

        StringBuilder content = new StringBuilder();

        if (game.getMode() == Game.Mode.TEAM) {
            content.append("Résultats par équipe:\n\n");
            for (Team team : game.getTeams()) {
                content.append(team.getName()).append(": ")
                        .append(team.getScore()).append(" points\n");
            }

            Team winner = game.getTeams().stream()
                    .max((t1, t2) -> Integer.compare(t1.getScore(), t2.getScore()))
                    .orElse(null);

            if (winner != null) {
                content.append("\n🏆 Équipe gagnante: ").append(winner.getName());
            }
        } else {
            content.append("Classement final:\n\n");
            game.getPlayers().stream()
                    .sorted((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()))
                    .forEach(p -> content.append(p.getName()).append(": ")
                            .append(p.getScore()).append(" points\n"));
        }

        alert.setContentText(content.toString());

        ButtonType replayBtn = new ButtonType("Rejouer");
        ButtonType menuBtn = new ButtonType("Menu");
        alert.getButtonTypes().setAll(replayBtn, menuBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == menuBtn) {
                controller.returnToMenu();
            }
            // Pour rejouer, on retourne au menu pour le moment
            else {
                controller.returnToMenu();
            }
        }
    }
}