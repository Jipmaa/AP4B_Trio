package view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.Card;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CardView extends StackPane {

    private static Map<Integer, Image> imageCache = new HashMap<>();
    private static Image backImage;

    private Card card;
    private ImageView imageView;

    public CardView(Card card) {
        this.card = card;
        this.imageView = new ImageView();

        loadImagesIfNeeded();

        updateImage();

        getChildren().add(imageView);
    }

    private void loadImagesIfNeeded() {
        if (backImage == null) {
            try {
                backImage = new Image(new FileInputStream("resources/images/back.png"));
            } catch (Exception e) {
                System.out.println("Missing back.png");
            }
        }

        if (!imageCache.isEmpty()) return;

        try {
            var lines = java.nio.file.Files.readAllLines(
                    java.nio.file.Paths.get("resources/cards.txt")
            );

            for (String line : lines) {
                if (!line.contains("=")) continue;

                String[] parts = line.split("=");
                int value = Integer.parseInt(parts[0].trim());
                String path = parts[1].trim();

                Image img = new Image(new FileInputStream("resources/" + path));
                imageCache.put(value, img);
            }

        } catch (IOException e) {
            System.out.println("Erreur lecture cards.txt");
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
    }

    public Card getCard() {
        return card;
    }
}
