package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards = new ArrayList<>();

    /**
     * Format attendu dans le .txt :
     * id;value;imagePath
     * Exemple :
     * 1;1;images/c1.png
     * 2;1;images/c2.png
     */
    public Deck(String cardListPath) {
        loadFromFile(cardListPath);
        shuffle();
    }

    private void loadFromFile(String path) {
        try (BufferedReader br = new BufferedReader(
                new FileReader("src/main/resources/cards.txt") // chemin relatif depuis le projet
        )) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                int id = Integer.parseInt(split[0]);
                int value = Integer.parseInt(split[1]);
                String img = split[2];
                cards.add(new Card(id, value, img));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load card list : " + path + " (" + e.getMessage() + ")");
        }
    }


    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card getCardById(int id) {
        return cards.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}
