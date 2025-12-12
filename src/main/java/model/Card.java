package model;

import javafx.scene.image.Image;

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
            image = new Image(getClass().getResourceAsStream("/" + imagePath));
        } catch (Exception e) {
            System.out.println("Missing image: " + imagePath + ", using back.png");
            try {
                image = new Image(getClass().getResourceAsStream("/images/back.png"));
            } catch (Exception ex) {
                System.out.println("back.png also missing!");
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

    public String getImagePath() {
        return imagePath;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    @Override
    public String toString() {
        return "Card{id=" + id + ", value=" + value + ", flipped=" + flipped + "}";
    }
}