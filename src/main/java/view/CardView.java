package view;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.Card;

import java.util.HashMap;
import java.util.Map;

public class CardView extends StackPane {

    private static Map<Integer, Image> imageCache = new HashMap<>();
    private static Image backImage;

    private Card card;
    private ImageView imageView;
    private Runnable onClickCallback;

    public CardView(Card card) {
        this.card = card;
        this.imageView = new ImageView();

        loadImagesIfNeeded();
        updateImage();

        setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");
        imageView.setStyle("-fx-border-radius: 6;");

        // Ajouter un effet de survol
        setOnMouseEntered(e -> {
            if (!card.isFlipped()) {
                setStyle("-fx-border-color: gold; -fx-border-width: 3; -fx-border-radius: 6; -fx-background-radius: 6;");
                setCursor(Cursor.HAND);
            }
        });

        setOnMouseExited(e -> {
            setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");
            setCursor(Cursor.DEFAULT);
        });

        // Gérer le clic
        setOnMouseClicked(e -> {
            if (onClickCallback != null && !card.isFlipped()) {
                onClickCallback.run();
            }
        });

        getChildren().add(imageView);
    }

    private void loadImagesIfNeeded() {
        if (backImage == null) {
            try {
                backImage = new Image(getClass().getResourceAsStream("/images/back.png"));
            } catch (Exception e) {
                System.out.println("Missing back.png");
            }
        }

        if (!imageCache.isEmpty()) return;

        try (var stream = getClass().getResourceAsStream("/cards.txt")) {

            if (stream == null) {
                System.out.println("cards.txt introuvable !");
                return;
            }

            var lines = new java.io.BufferedReader(new java.io.InputStreamReader(stream)).lines();

            lines.forEach(line -> {
                String[] split = line.split(";");
                int value = Integer.parseInt(split[1]);
                String imagePath = split[2];

                Image img = new Image(getClass().getResourceAsStream("/" + imagePath));
                imageCache.put(value, img);
            });

        } catch (Exception e) {
            System.out.println("Erreur lecture cards.txt : " + e);
        }
    }

    public void updateImage() {
        if (card.isFlipped()) {
            imageView.setImage(imageCache.getOrDefault(card.getValue(), backImage));
            setCursor(Cursor.DEFAULT);
        } else {
            imageView.setImage(backImage);
            setCursor(Cursor.HAND);
        }
        imageView.setFitWidth(100);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
    }

    public Card getCard() {
        return card;
    }

    public void setOnCardClick(Runnable callback) {
        this.onClickCallback = callback;
    }
}