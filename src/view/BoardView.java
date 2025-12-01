package view;

import controller.NavigationController;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoardView {

    private final Game game;
    private final NavigationController navigationController;
    private final BorderPane view;

    private GridPane centerBoardGrid;
    private VBox playerInfoBox; // To display current player info and actions
    private Label gameMessageLabel;
    // private HBox playerAreas; // Container for all player displays - removed

    private final double CARD_WIDTH = 70;
    private final double CARD_HEIGHT = 100;

    private Player selectedTargetPlayer; // For revealing cards from another player's hand

    // Map to keep track of CardView objects for cards on the board
    private Map<Card, CardView> boardCardViews;
    // Map to keep track of CardView objects for cards in player hands (for current player)
    private Map<Card, CardView> currentPlayerHandCardViews;


    public BoardView(Game game, NavigationController navigationController) {
        this.game = game;
        this.navigationController = navigationController;
        this.view = new BorderPane();
        this.boardCardViews = new HashMap<>();
        this.currentPlayerHandCardViews = new HashMap<>();
        initializeView();
        updateBoard();
        updatePlayerInfo();
        updateGameMessage("Game started! " + game.getCurrentPlayer().getName() + "'s turn.");
    }

    private void initializeView() {
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: #228B22;"); // Green felt background

        // Top section for game messages
        gameMessageLabel = new Label("Welcome to Trio!");
        gameMessageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gameMessageLabel.setTextFill(Color.WHITE);
        BorderPane.setAlignment(gameMessageLabel, Pos.CENTER);
        view.setTop(gameMessageLabel);

        // Center for the main board cards
        centerBoardGrid = new GridPane();
        centerBoardGrid.setHgap(10);
        centerBoardGrid.setVgap(10);
        centerBoardGrid.setAlignment(Pos.CENTER);
        view.setCenter(centerBoardGrid);

        // Player info and actions on the bottom
        playerInfoBox = new VBox(10);
        playerInfoBox.setAlignment(Pos.CENTER);
        playerInfoBox.setPadding(new Insets(10));
        playerInfoBox.setBackground(new Background(new BackgroundFill(Color.web("#333333", 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        view.setBottom(playerInfoBox);

        // Player areas around the board
        // These will be dynamically populated in updatePlayerInfo()
        // For now, just set placeholders for BorderPane regions
        view.setLeft(new VBox());
        view.setRight(new VBox());
        view.setTop(new HBox()); // Top players
        // view.setBottom(playerInfoBox); // Current player info and actions - already set
    }

    public void updateBoard() {
        centerBoardGrid.getChildren().clear();
        boardCardViews.clear(); // Clear map as board is being redrawn

        List<Card> centerCards = game.getBoard().getCenterCards();
        int cols = 3; // Example layout, can be dynamic
        for (int i = 0; i < centerCards.size(); i++) {
            Card card = centerCards.get(i);
            CardView cardView = new CardView(card, CARD_WIDTH, CARD_HEIGHT);
            cardView.setFaceUp(false); // Cards on board are face down
            final int cardIndex = i;
            cardView.setOnMouseClicked(e -> {
                if (game.getGameState() == Game.GameState.IN_PROGRESS && game.getCurrentPlayer().equals(navigationController.getGame().getCurrentPlayer())) {
                    game.revealCardFromCenter(cardIndex);
                    updateViewAfterAction();
                }
            });
            centerBoardGrid.add(cardView, i % cols, i / cols);
            boardCardViews.put(card, cardView); // Store CardView
        }

        // Display revealed cards temporarily in a central box
        List<Card> revealedCards = game.getRevealedCards();
        HBox revealedCardsBox = new HBox(10);
        revealedCardsBox.setAlignment(Pos.CENTER);
        revealedCardsBox.setPadding(new Insets(10));
        revealedCardsBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        if (!revealedCards.isEmpty()) {
            for (Card rc : revealedCards) {
                CardView revealedCardView = new CardView(rc, CARD_WIDTH, CARD_HEIGHT);
                revealedCardView.setFaceUp(true);
                revealedCardsBox.getChildren().add(revealedCardView);
            }
            view.setTop(revealedCardsBox); // Show revealed cards at the top
        } else {
            // If no cards are revealed, ensure player areas are correctly displayed
            updatePlayerInfo(); // Re-populate all player areas
        }
    }

    public void updatePlayerInfo() {
        playerInfoBox.getChildren().clear();
        Player currentPlayer = game.getCurrentPlayer();

        // --- Handle Card Exchange Phase ---
        if (game.getGameState() == Game.GameState.CARD_EXCHANGE && game.getPlayMode() == Game.PlayMode.TEAM) {
            handleCardExchangeUI(currentPlayer);
            return; // Exit after handling exchange UI
        }

        // --- Current Player Info and Actions (Bottom) ---
        VBox currentPlayerActionsBox = new VBox(10);
        currentPlayerActionsBox.setAlignment(Pos.CENTER);
        currentPlayerActionsBox.setPadding(new Insets(10));
        currentPlayerActionsBox.setBackground(new Background(new BackgroundFill(Color.web("#333333", 0.8), CornerRadii.EMPTY, Insets.EMPTY)));

        Label currentPlayerLabel = new Label("Current Player: " + currentPlayer.getName());
        currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        currentPlayerLabel.setTextFill(Color.YELLOW);

        Label currentTrios = new Label("Your Trios: " + currentPlayer.getScore());
        currentTrios.setTextFill(Color.WHITE);

        // Display current player's hand
        HBox currentPlayerHandBox = new HBox(5);
        currentPlayerHandBox.setAlignment(Pos.CENTER);
        currentPlayerHandBox.setPadding(new Insets(5));
        currentPlayerHandBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-border-color: white;");
        currentPlayerHandCardViews.clear();
        for (Card card : currentPlayer.getHand()) {
            CardView cardView = new CardView(card, CARD_WIDTH * 0.8, CARD_HEIGHT * 0.8); // Smaller cards for hand
            cardView.setFaceUp(true); // Current player's hand is visible
            currentPlayerHandBox.getChildren().add(cardView);
            currentPlayerHandCardViews.put(card, cardView);
        }

        // Action buttons for current player
        Button revealSmallestButton = new Button("Reveal Smallest from Hand");
        revealSmallestButton.setOnAction(e -> {
            game.revealCardFromPlayerHand(currentPlayer, true);
            updateViewAfterAction();
        });

        Button revealLargestButton = new Button("Reveal Largest from Hand");
        revealLargestButton.setOnAction(e -> {
            game.revealCardFromPlayerHand(currentPlayer, false);
            updateViewAfterAction();
        });

        // Player selection for revealing cards from other players
        Label targetPlayerLabel = new Label("Target Player:");
        targetPlayerLabel.setTextFill(Color.WHITE);
        ComboBox<Player> targetPlayerComboBox = new ComboBox<>();
        targetPlayerComboBox.getItems().addAll(game.getPlayers().stream()
                .filter(p -> !p.equals(currentPlayer))
                .collect(Collectors.toList()));
        targetPlayerComboBox.setPromptText("Select Player");
        targetPlayerComboBox.valueProperty().addListener((obs, oldVal, newVal) -> selectedTargetPlayer = newVal);

        Button revealSmallestFromTargetButton = new Button("Reveal Smallest from Target");
        revealSmallestFromTargetButton.setOnAction(e -> {
            if (selectedTargetPlayer != null) {
                game.revealCardFromPlayerHand(selectedTargetPlayer, true);
                updateViewAfterAction();
            } else {
                updateGameMessage("Please select a target player.");
            }
        });

        Button revealLargestFromTargetButton = new Button("Reveal Largest from Target");
        revealLargestFromTargetButton.setOnAction(e -> {
            if (selectedTargetPlayer != null) {
                game.revealCardFromPlayerHand(selectedTargetPlayer, false);
                updateViewAfterAction();
            } else {
                updateGameMessage("Please select a target player.");
            }
        });

        currentPlayerActionsBox.getChildren().addAll(currentPlayerLabel, currentTrios, currentPlayerHandBox,
                revealSmallestButton, revealLargestButton,
                targetPlayerLabel, targetPlayerComboBox, revealSmallestFromTargetButton, revealLargestFromTargetButton);
        view.setBottom(currentPlayerActionsBox); // Set current player info to bottom

        // --- Other Players Info (Top, Left, Right) ---
        // Clear previous player displays
        ((HBox) view.getTop()).getChildren().clear();
        ((VBox) view.getLeft()).getChildren().clear();
        ((VBox) view.getRight()).getChildren().clear();

        List<Player> otherPlayers = game.getPlayers().stream()
                .filter(p -> !p.equals(currentPlayer))
                .collect(Collectors.toList());

        // Distribute other players around the board
        // This is a simplified distribution for up to 5 other players
        // Player 1 (current) is at bottom
        // Player 2 at top
        // Player 3 at left
        // Player 4 at right
        // Player 5 at top-left (if 5 players)
        // Player 6 at top-right (if 6 players)

        if (!otherPlayers.isEmpty()) {
            // Player 2 (top)
            VBox player2Box = createOtherPlayerDisplay(otherPlayers.get(0));
            ((HBox) view.getTop()).getChildren().add(player2Box);
            ((HBox) view.getTop()).setAlignment(Pos.CENTER);
        }
        if (otherPlayers.size() > 1) {
            // Player 3 (left)
            VBox player3Box = createOtherPlayerDisplay(otherPlayers.get(1));
            view.setLeft(player3Box);
            BorderPane.setAlignment(player3Box, Pos.CENTER_LEFT);
        }
        if (otherPlayers.size() > 2) {
            // Player 4 (right)
            VBox player4Box = createOtherPlayerDisplay(otherPlayers.get(2));
            view.setRight(player4Box);
            BorderPane.setAlignment(player4Box, Pos.CENTER_RIGHT);
        }
        if (otherPlayers.size() > 3) {
            // Player 5 (top-left, or just another top player)
            VBox player5Box = createOtherPlayerDisplay(otherPlayers.get(3));
            ((HBox) view.getTop()).getChildren().add(0, player5Box); // Add to left of player 2
        }
        if (otherPlayers.size() > 4) {
            // Player 6 (top-right, or just another top player)
            VBox player6Box = createOtherPlayerDisplay(otherPlayers.get(4));
            ((HBox) view.getTop()).getChildren().add(player6Box); // Add to right of player 2
        }
    }

    private VBox createOtherPlayerDisplay(Player player) {
        VBox playerBox = new VBox(5);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(5));
        playerBox.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        playerBox.setBackground(Background.EMPTY); // No highlight for others

        Label nameLabel = new Label(player.getName());
        nameLabel.setTextFill(Color.WHITE);
        Label handSizeLabel = new Label("Cards: " + player.getHand().size());
        handSizeLabel.setTextFill(Color.WHITE);
        Label triosLabel = new Label("Trios: " + player.getScore());
        triosLabel.setTextFill(Color.WHITE);

        HBox handDisplay = new HBox(2); // Display face-down cards
        for (int i = 0; i < player.getHand().size(); i++) {
            CardView cardView = new CardView(new Card(0), CARD_WIDTH * 0.5, CARD_HEIGHT * 0.5); // Dummy card for face-down
            cardView.setFaceUp(false);
            handDisplay.getChildren().add(cardView);
        }

        playerBox.getChildren().addAll(nameLabel, handSizeLabel, triosLabel, handDisplay);
        return playerBox;
    }

    private void handleCardExchangeUI(Player currentPlayer) {
        playerInfoBox.getChildren().clear();
        updateGameMessage("Team Card Exchange: " + currentPlayer.getName() + ", select a card to exchange with your partner.");

        Label currentPlayerLabel = new Label("Current Player: " + currentPlayer.getName());
        currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        currentPlayerLabel.setTextFill(Color.YELLOW);

        Label selectCardLabel = new Label("Select card to exchange:");
        selectCardLabel.setTextFill(Color.WHITE);

        HBox currentPlayerHandBox = new HBox(5);
        currentPlayerHandBox.setAlignment(Pos.CENTER);
        currentPlayerHandBox.setPadding(new Insets(5));
        currentPlayerHandBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-border-color: white;");
        currentPlayerHandCardViews.clear();

        final Card[] selectedCardForExchange = {null}; // Use array for mutable reference in lambda

        for (Card card : currentPlayer.getHand()) {
            CardView cardView = new CardView(card, CARD_WIDTH * 0.8, CARD_HEIGHT * 0.8);
            cardView.setFaceUp(true);
            cardView.setOnMouseClicked(e -> {
                if (selectedCardForExchange[0] != null) {
                    // Deselect previous card
                    currentPlayerHandCardViews.get(selectedCardForExchange[0]).setStyle("");
                }
                selectedCardForExchange[0] = card;
                cardView.setStyle("-fx-border-color: yellow; -fx-border-width: 3;"); // Highlight selected card
            });
            currentPlayerHandBox.getChildren().add(cardView);
            currentPlayerHandCardViews.put(card, cardView);
        }

        Button proposeButton = new Button("Propose Exchange");
        proposeButton.setOnAction(e -> {
            if (selectedCardForExchange[0] != null) {
                game.proposeCardForExchange(currentPlayer, selectedCardForExchange[0]);
                updateGameMessage(currentPlayer.getName() + " proposed a card. Waiting for partner to confirm.");
                // Disable further interaction for this player until partner acts
                proposeButton.setDisable(true);
                currentPlayerHandBox.setDisable(true);
            } else {
                updateGameMessage("Please select a card to exchange.");
            }
        });

        Button confirmButton = new Button("Confirm Exchange");
        confirmButton.setOnAction(e -> {
            game.confirmExchange(currentPlayer);
            updateViewAfterAction(); // This will check if exchange phase is complete and move to IN_PROGRESS
        });

        playerInfoBox.getChildren().addAll(currentPlayerLabel, selectCardLabel, currentPlayerHandBox, proposeButton, confirmButton);
        view.setBottom(playerInfoBox); // Ensure it's set to bottom
    }

    private void updateViewAfterAction() {
        updateBoard();
        updatePlayerInfo();
        if (game.getGameState() == Game.GameState.GAME_OVER) {
            navigationController.endGame(game.determineWinner()); // Trigger end game view
        } else {
            updateGameMessage(game.getCurrentPlayer().getName() + "'s turn.");
        }
    }

    public void updateGameMessage(String message) {
        gameMessageLabel.setText(message);
    }

    public BorderPane getView() {
        return view;
    }
}


