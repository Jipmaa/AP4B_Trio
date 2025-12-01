package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Card;

public class CardView extends StackPane {

    private Card card;
    private boolean faceUp;
    private final Rectangle background;
    private final Label valueLabel;

    public CardView(Card card, double width, double height) {
        this.card = card;
        this.faceUp = false; // Cards start face down
        this.setPrefSize(width, height);

        background = new Rectangle(width, height);
        background.setArcWidth(10);
        background.setArcHeight(10);
        background.setFill(Color.GRAY); // Default for face down

        valueLabel = new Label(String.valueOf(card.getValue()));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setVisible(false); // Hidden when face down

        this.getChildren().addAll(background, valueLabel);
        this.setAlignment(Pos.CENTER);

        updateView();
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
        updateView();
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public Card getCard() {
        return card;
    }

    private void updateView() {
        if (faceUp) {
            background.setFill(Color.LIGHTBLUE); // Color for face up cards
            valueLabel.setText(String.valueOf(card.getValue()));
            valueLabel.setVisible(true);
        } else {
            background.setFill(Color.DARKBLUE); // Color for face down cards
            valueLabel.setVisible(false);
        }
    }
}
