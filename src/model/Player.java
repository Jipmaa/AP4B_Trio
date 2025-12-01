package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Player {

    private final String name;
    private final List<Card> hand;
    private final List<List<Card>> triosWon; // Each inner list is a trio of 3 identical cards

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.triosWon = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<List<Card>> getTriosWon() {
        return triosWon;
    }

    public void addCardToHand(Card card) {
        if (card != null) {
            hand.add(card);
            sortHand();
        }
    }

    public Card removeCardFromHand(Card card) {
        if (hand.remove(card)) {
            return card;
        }
        return null;
    }

    public Card getSmallestCard() {
        if (hand.isEmpty()) {
            return null;
        }
        return hand.get(0);
    }

    public Card getLargestCard() {
        if (hand.isEmpty()) {
            return null;
        }
        return hand.get(hand.size() - 1);
    }

    public void addTrio(List<Card> trio) {
        if (trio != null && trio.size() == 3 && trio.get(0).equals(trio.get(1)) && trio.get(1).equals(trio.get(2))) {
            triosWon.add(trio);
        } else {
            System.err.println("Attempted to add an invalid trio: " + trio);
        }
    }

    public int getScore() {
        return triosWon.size();
    }

    private void sortHand() {
        hand.sort(Comparator.comparingInt(Card::getValue));
    }

    @Override
    public String toString() {
        return "Player{" + "name='" + name + "'" + ", hand=" + hand + ", triosWon=" + triosWon.size() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
