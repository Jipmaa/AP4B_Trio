package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Card;
import model.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Dialogue pour l'échange de cartes entre coéquipiers après un trio réussi
 */
public class CardExchangeDialog extends Stage {
    
    private Card selectedCard = null;
    private CompletableFuture<Card> futureCard;
    
    public CardExchangeDialog(Stage owner, Player player, String instruction) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Échange de carte - " + player.getName());
        
        futureCard = new CompletableFuture<>();
        
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #2c3e50;");
        
        // Titre
        Label title = new Label("🔄 ÉCHANGE DE CARTE 🔄");
        title.setTextFill(Color.GOLD);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Instructions
        Label instructionLabel = new Label(instruction);
        instructionLabel.setTextFill(Color.WHITE);
        instructionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(400);
        instructionLabel.setAlignment(Pos.CENTER);
        
        // Conteneur pour les cartes
        HBox cardsBox = new HBox(15);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setPadding(new Insets(20));
        
        // Afficher les cartes du joueur
        for (Card card : player.getHand()) {
            CardView cv = new CardView(card);
            cv.setForceVisible(true);
            cv.updateImage();
            
            // Style pour la sélection
            cv.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
            
            cv.setOnMouseEntered(e -> {
                if (selectedCard != card) {
                    cv.setStyle("-fx-border-color: gold; -fx-border-width: 4; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
                }
            });
            
            cv.setOnMouseExited(e -> {
                if (selectedCard != card) {
                    cv.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
                } else {
                    cv.setStyle("-fx-border-color: #FF6B6B; -fx-border-width: 4; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
                }
            });
            
            cv.setOnMouseClicked(e -> {
                // Désélectionner les autres cartes
                cardsBox.getChildren().forEach(node -> {
                    if (node instanceof CardView) {
                        CardView cardView = (CardView) node;
                        if (cardView.getCard() != card) {
                            cardView.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
                        }
                    }
                });
                
                selectedCard = card;
                cv.setStyle("-fx-border-color: #FF6B6B; -fx-border-width: 4; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
            });
            
            cardsBox.getChildren().add(cv);
        }
        
        // Bouton de confirmation
        Button confirmBtn = new Button("CONFIRMER L'ÉCHANGE");
        confirmBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        confirmBtn.setPrefSize(250, 50);
        confirmBtn.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        confirmBtn.setOnAction(e -> {
            if (selectedCard != null) {
                futureCard.complete(selectedCard);
                close();
            }
        });
        
        layout.getChildren().addAll(title, instructionLabel, cardsBox, confirmBtn);
        
        Scene scene = new Scene(layout);
        setScene(scene);
        setResizable(false);
    }
    
    /**
     * Affiche le dialogue et retourne la carte sélectionnée
     */
    public CompletableFuture<Card> getSelectedCard() {
        return futureCard;
    }
}
