package controller;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.Card;
import model.Game;
import model.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import view.CardExchangeDialog;
import view.PicanteRoulette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
            Platform.runLater(() -> {
                PicanteRoulette dialog = new PicanteRoulette(primaryStage, onLetterSelected);
                dialog.showAndWait();
            });
        });
        
        // Nouveau callback pour l'échange de cartes en mode TEAM
        game.setOnTeamCardExchange(() -> {
            Platform.runLater(() -> {
                handleTeamCardExchange();
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
     * Propose l'échange initial au début du jeu (mode TEAM uniquement)
     */
    public void offerInitialExchange() {
        if (game.getMode() != Game.Mode.TEAM) {
            return;
        }
        
        Platform.runLater(() -> {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Échange initial");
            confirmDialog.setHeaderText("🔄 ÉCHANGE DE DÉBUT DE PARTIE 🔄");
            confirmDialog.setContentText(
                "Les équipes souhaitent-elles échanger des cartes avant de commencer ?\n\n" +
                "(Chaque équipe peut échanger une carte entre coéquipiers)"
            );
            
            ButtonType yesButton = new ButtonType("Oui");
            ButtonType noButton = new ButtonType("Non");
            confirmDialog.getButtonTypes().setAll(yesButton, noButton);
            
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == yesButton) {
                    handleInitialTeamExchange();
                }
            });
        });
    }
    
    /**
     * Gérer l'échange initial entre toutes les équipes au début du jeu
     */
    private void handleInitialTeamExchange() {
        isProcessing = true;
        
        // Tous les joueurs peuvent échanger au début
        List<Player> eligiblePlayers = new ArrayList<>(game.getPlayers());
        Map<Player, Card> playerChoices = new HashMap<>();
        
        // Traiter les joueurs séquentiellement
        processPlayerExchangeSequentially(eligiblePlayers, 0, playerChoices);
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

    /**
     * Gérer l'échange de cartes entre tous les coéquipiers
     * L'échange est optionnel et séquentiel (un joueur après l'autre)
     * L'équipe qui vient de gagner le trio ne peut PAS échanger
     */
    private void handleTeamCardExchange() {
        isProcessing = true;
        
        // Identifier l'équipe du joueur actuel (celle qui a gagné le trio)
        Player currentPlayer = game.getCurrentPlayer();
        model.Team winningTeam = findTeamOfPlayer(currentPlayer);
        
        // Liste des joueurs pouvant échanger (tous sauf l'équipe gagnante)
        List<Player> eligiblePlayers = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            model.Team playerTeam = findTeamOfPlayer(player);
            // Exclure l'équipe gagnante
            if (playerTeam != null && !playerTeam.equals(winningTeam) && !player.getHand().isEmpty()) {
                eligiblePlayers.add(player);
            }
        }
        
        if (eligiblePlayers.isEmpty()) {
            isProcessing = false;
            notifyGameStateChanged();
            return;
        }
        
        // Map pour stocker les choix de chaque joueur
        Map<Player, Card> playerChoices = new HashMap<>();
        
        // Traiter les joueurs séquentiellement (un par un)
        processPlayerExchangeSequentially(eligiblePlayers, 0, playerChoices);
    }
    
    /**
     * Traite l'échange pour chaque joueur de manière séquentielle
     */
    private void processPlayerExchangeSequentially(List<Player> eligiblePlayers, int index, Map<Player, Card> playerChoices) {
        if (index >= eligiblePlayers.size()) {
            // Tous les joueurs ont fait leur choix, effectuer les échanges
            performExchanges(playerChoices);
            return;
        }
        
        Player player = eligiblePlayers.get(index);
        Player teammate = game.getTeammate(player);
        
        if (teammate == null || teammate.getHand().isEmpty()) {
            // Passer au joueur suivant
            processPlayerExchangeSequentially(eligiblePlayers, index + 1, playerChoices);
            return;
        }
        
        Platform.runLater(() -> {
            // Créer un dialogue avec option de refuser
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Échange de carte - " + player.getName());
            confirmDialog.setHeaderText("🔄 ÉCHANGE OPTIONNEL 🔄");
            confirmDialog.setContentText(
                player.getName() + ", voulez-vous échanger une carte avec " + teammate.getName() + " ?\n\n" +
                "(L'échange est facultatif)"
            );
            
            ButtonType yesButton = new ButtonType("Oui, échanger");
            ButtonType noButton = new ButtonType("Non, passer");
            confirmDialog.getButtonTypes().setAll(yesButton, noButton);
            
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == yesButton) {
                    // Le joueur veut échanger
                    CardExchangeDialog dialog = new CardExchangeDialog(
                        primaryStage, 
                        player, 
                        player.getName() + ", choisissez une carte à échanger avec " + teammate.getName()
                    );
                    
                    dialog.show();
                    
                    dialog.getSelectedCard().thenAccept(selectedCard -> {
                        playerChoices.put(player, selectedCard);
                        // Passer au joueur suivant
                        processPlayerExchangeSequentially(eligiblePlayers, index + 1, playerChoices);
                    });
                } else {
                    // Le joueur refuse l'échange
                    playerChoices.put(player, null);
                    // Passer au joueur suivant
                    processPlayerExchangeSequentially(eligiblePlayers, index + 1, playerChoices);
                }
            });
        });
    }
    
    /**
     * Effectue les échanges entre coéquipiers ayant choisi d'échanger
     */
    private void performExchanges(Map<Player, Card> playerChoices) {
        Platform.runLater(() -> {
            List<Player> processedPlayers = new ArrayList<>();
            List<String> exchangeMessages = new ArrayList<>();
            
            for (Player player : playerChoices.keySet()) {
                if (processedPlayers.contains(player)) continue;
                
                Player teammate = game.getTeammate(player);
                if (teammate == null) continue;
                
                Card card1 = playerChoices.get(player);
                Card card2 = playerChoices.get(teammate);
                
                // Vérifier que les DEUX coéquipiers ont choisi d'échanger
                if (card1 != null && card2 != null) {
                    game.exchangeCards(player, card1, teammate, card2);
                    processedPlayers.add(player);
                    processedPlayers.add(teammate);
                    
                    exchangeMessages.add("✓ " + player.getName() + " ↔ " + teammate.getName());
                    
                    System.out.println("Échange effectué entre " + player.getName() + " et " + teammate.getName());
                } else if (card1 == null && card2 == null) {
                    exchangeMessages.add("✗ " + player.getName() + " et " + teammate.getName() + " ont refusé");
                } else {
                    // Un seul des deux a accepté
                    String refuser = (card1 == null) ? player.getName() : teammate.getName();
                    exchangeMessages.add("✗ " + refuser + " a refusé l'échange");
                }
            }
            
            isProcessing = false;
            notifyGameStateChanged();
            
            if (!exchangeMessages.isEmpty()) {
                showAlert(
                    "Échanges terminés", 
                    "Résumé des échanges :\n\n" + String.join("\n", exchangeMessages), 
                    Alert.AlertType.INFORMATION
                );
            }
        });
    }
    
    /**
     * Trouve l'équipe d'un joueur
     */
    private model.Team findTeamOfPlayer(Player player) {
        for (model.Team team : game.getTeams()) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
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
