package model;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Card {

    private final int id;
    private final int value;
    private final String imagePath;
    private Image image;
    private boolean flipped = false;

    public Card(int id, int value, String imagePath) {
        this.id = id;
        this.value = value;
        this.imagePath = imagePath;
        loadImage();
    }

    private void loadImage() {
        try {
            image = new Image(new FileInputStream("src/main/resources/" + imagePath));
        } catch (FileNotFoundException e) {
            System.out.println("Missing image: " + imagePath + ", using back.png");
            try {
                image = new Image(new FileInputStream("src/main/resources/images/back.png"));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException("Missing back.png in resources/images/");
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public Image getImage() {
        return image;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }
}
