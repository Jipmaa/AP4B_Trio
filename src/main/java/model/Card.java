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
        this.imagePath = imagePath; // ex: "card3.png"
        loadImage();
    }


    private void loadImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/" + imagePath));
        } catch (Exception e) {
            System.out.println("Missing image: " + imagePath + ", using back.png");
            image = new Image(getClass().getResourceAsStream("/images/back.png"));
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
