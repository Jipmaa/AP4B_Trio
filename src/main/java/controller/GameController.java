package controller;

import javafx.stage.Stage;
import model.Card;
import model.Game;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import view.PicanteRoulette;

import java.util.function.Consumer;

public class GameController {

    private Game game;
    private NavigationController navController;
    private Runnable onGameStateChanged;
    private Stage primaryStage;
    private boolean isProcessing = false; // Bloquer les clics pendant les animations
    private Consumer<String> onLetterSelected;

    public GameController(Game game, NavigationController navController) {
        this.game = game;
        this.navController = navController;

        this.onLetterSelected = letter -> {
            System.out.println("Lettre reçue : " + letter);
            game.applyPicanteReward(letter);
            notifyGameStateChanged();
        };

        game.setOnPicanteTrio(() -> {
            javafx.application.Platform.runLater(() -> {
                PicanteRoulette dialog = new PicanteRoulette(primaryStage, onLetterSelected);
                dialog.showAndWait();
            });
        });
    }

    public void setOnGameStateChanged(Runnable callback) {
        this.onGameStateChanged = callback;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void notifyGameStateChanged() {
        if (onGameStateChanged != null) {
            onGameStateChanged.run();
        }
    }

    /**
     * Gérer le clic sur une carte
     */


    public void handleCardClick(Card card) {
        if (isProcessing) return;


        boolean success = game.attemptFlipCard(card);

        if (!success) {
            showAlert(
                    "Action impossible",
                    "Vous ne pouvez pas retourner cette carte.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        notifyGameStateChanged();

        int revealedCount = game.getRevealedCards().size();

        if (revealedCount == 2) {
            if (!game.checkTrio()) {
                isProcessing = true;
                Platform.runLater(() -> {
                    showAlert("Cartes différentes", "Les deux cartes ne correspondent pas!", Alert.AlertType.INFORMATION);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            Platform.runLater(() -> {
                                game.failPair();
                                isProcessing = false;
                                notifyGameStateChanged();
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                });
            }
        }
        else if (revealedCount == 3) {
            if (game.checkTrio()) {
                showAlert("Trio réussi!", "Bravo! Vous avez trouvé un trio!", Alert.AlertType.INFORMATION);
                game.rewardTrio();
            } else {
                isProcessing = true;
                showAlert("Trio échoué", "Les trois cartes ne forment pas un trio!", Alert.AlertType.INFORMATION);
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        Platform.runLater(() -> {
                            game.failTrio();
                            isProcessing = false;
                            notifyGameStateChanged();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            notifyGameStateChanged();
        }
    }


    public void endTurn() {
        game.nextPlayer();
        notifyGameStateChanged();
    }

    public void returnToMenu() {
        navController.showMainMenu();
    }

    public Game getGame() {
        return game;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}