package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Card {

    private final int value;
    private final List<Integer> linkedValues;

    public Card(int value) {
        this.value = value;
        this.linkedValues = new ArrayList<>();
        defineLinkedValues();
    }

    private void defineLinkedValues() {
        switch (this.value) {
            case 1:
                linkedValues.add(5);
                linkedValues.add(6);
                break;
            case 2:
                linkedValues.add(5);
                linkedValues.add(9);
                break;
            case 3:
                linkedValues.add(6);
                linkedValues.add(9);
                break;
            case 4:
                linkedValues.add(8);
                linkedValues.add(12);
                break;
            case 5:
                linkedValues.add(1);
                linkedValues.add(2);
                break;
            case 6:
                linkedValues.add(1);
                linkedValues.add(3);
                break;
            case 7:
                // Le 7 n'est lié à aucune autre carte
                break;
            case 8:
                linkedValues.add(4);
                linkedValues.add(12);
                break;
            case 9:
                linkedValues.add(2);
                linkedValues.add(3);
                break;
            case 10:
                linkedValues.add(11);
                linkedValues.add(12);
                break;
            case 11:
                linkedValues.add(10);
                linkedValues.add(12);
                break;
            case 12:
                linkedValues.add(4);
                linkedValues.add(8);
                linkedValues.add(10);
                linkedValues.add(11);
                break;
        }
    }

    public int getValue() {
        return value;
    }

    public List<Integer> getLinkedValues() {
        return linkedValues;
    }

    public boolean isLinkedWith(Card otherCard) {
        return this.linkedValues.contains(otherCard.getValue());
    }

    @Override
    public String toString() {
        return "Card{" + "value=" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
