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
    private boolean clickable = false;

    public CardView(Card card) {
        this.card = card;
        this.imageView = new ImageView();

        loadImagesIfNeeded();
        updateImage();

        setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");
        imageView.setStyle("-fx-border-radius: 6;");

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

                if (!imageCache.containsKey(value)) {
                    try {
                        Image img = new Image(getClass().getResourceAsStream("/" + imagePath));
                        imageCache.put(value, img);
                    } catch (Exception e) {
                        System.out.println("Erreur chargement image: " + imagePath);
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Erreur lecture cards.txt : " + e);
        }
    }

    public void updateImage() {
        if (card.isFlipped()) {
            imageView.setImage(imageCache.getOrDefault(card.getValue(), backImage));
        } else {
            imageView.setImage(backImage);
        }
        imageView.setFitWidth(100);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
    }

    /**
     * Active ou désactive la carte pour les clics
     */
    public void setClickable(boolean clickable) {
        this.clickable = clickable;

        if (clickable && !card.isFlipped()) {
            // Activer les événements de souris
            setOnMouseEntered(e -> {
                setStyle("-fx-border-color: gold; -fx-border-width: 3; -fx-border-radius: 6; -fx-background-radius: 6;");
                setCursor(Cursor.HAND);
            });

            setOnMouseExited(e -> {
                setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");
                setCursor(Cursor.DEFAULT);
            });

            setOnMouseClicked(e -> {
                if (onClickCallback != null) {
                    onClickCallback.run();
                }
            });

            setOpacity(1.0);
        } else {
            // Désactiver tous les événements
            setOnMouseEntered(null);
            setOnMouseExited(null);
            setOnMouseClicked(null);
            setCursor(Cursor.DEFAULT);

            // Réduire l'opacité des cartes non-cliquables qui ne sont pas retournées
            if (!card.isFlipped()) {
                setOpacity(0.6);
            } else {
                setOpacity(1.0);
            }
        }
    }

    public Card getCard() {
        return card;
    }

    public void setOnCardClick(Runnable callback) {
        this.onClickCallback = callback;
    }
}