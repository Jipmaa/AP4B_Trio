package controller;

import model.Card;
import model.Game;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class GameController {

    private Game game;
    private NavigationController navController;
    private Runnable onGameStateChanged;
    private boolean isProcessing = false; // Bloquer les clics pendant les animations

    public GameController(Game game, NavigationController navController) {
        this.game = game;
        this.navController = navController;
    }

    public void setOnGameStateChanged(Runnable callback) {
        this.onGameStateChanged = callback;
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
        // Bloquer si une animation est en cours
        if (isProcessing) {
            return;
        }

        // Ne rien faire si la carte est déjà retournée
        if (card.isFlipped()) {
            return;
        }

        // Tenter de retourner la carte
        boolean success = game.attemptFlipCard(card);

        if (!success) {
            showAlert("Action impossible", "Vous ne pouvez pas retourner cette carte.", Alert.AlertType.WARNING);
            return;
        }

        notifyGameStateChanged();

        // Gérer les cas selon le nombre de cartes révélées
        int revealedCount = game.getRevealedCards().size();

        if (revealedCount == 2) {
            // Vérifier si les 2 cartes correspondent
            if (!game.checkTrio()) {
                // Les cartes ne correspondent pas
                isProcessing = true; // Bloquer les clics
                Platform.runLater(() -> {
                    showAlert("Cartes différentes", "Les deux cartes ne correspondent pas!", Alert.AlertType.INFORMATION);

                    // Attendre 1.5 secondes puis retourner les cartes
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            Platform.runLater(() -> {
                                game.failPair();
                                isProcessing = false; // Débloquer les clics
                                notifyGameStateChanged();
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                });
            }
            // Si les cartes correspondent, attendre la 3ème carte
        } else if (revealedCount == 3) {
            // Vérifier le trio
            if (game.checkTrio()) {
                showAlert("Trio réussi!", "Bravo! Vous avez trouvé un trio!", Alert.AlertType.INFORMATION);
                game.rewardTrio();

                // Vérifier si la partie est terminée
                if (game.isGameOver()) {
                    Platform.runLater(() -> {
                        if (onGameStateChanged != null) {
                            onGameStateChanged.run();
                        }
                    });
                }
            } else {
                isProcessing = true; // Bloquer les clics
                showAlert("Trio échoué", "Les trois cartes ne forment pas un trio!", Alert.AlertType.INFORMATION);

                // Attendre 1.5 secondes puis retourner les cartes
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        Platform.runLater(() -> {
                            game.failTrio();
                            isProcessing = false; // Débloquer les clics
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