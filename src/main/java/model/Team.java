package model;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final String name;
    private final List<Player> members = new ArrayList<>();
    private int score;

    // Dans model/Team.java
    private boolean canExchange = true; // Par défaut, le droit est présent au début

    public boolean canExchange() {
        return canExchange;
    }

    public void setCanExchange(boolean canExchange) {
        this.canExchange = canExchange;
    }

    public Team(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public void addPlayer(Player p) {
        if (!members.contains(p)) {
            members.add(p);
            p.setTeam(this);
        }
    }

    public void removePlayer(Player p) {
        members.remove(p);
        if (p.getTeam() == this) {
            p.setTeam(null);
        }
    }

    // Dans Team.java
    public Player getPartner(Player currentPlayer) {
        return members.stream()
                .filter(p -> !p.equals(currentPlayer))
                .findFirst()
                .orElse(null);
    }

    public boolean hasExchangeRight() {
        return canExchange;
    }

    public void setExchangeRight(boolean available) {
        this.canExchange = available;
    }

    public List<Player> getPlayers() {
        return members;
    }

    public int getScore() {
        return score;
    }

    public void addPoint(int pts) {
        this.score += pts;
    }

    @Override
    public String toString() {
        return "Team{name='" + name + "', score=" + score + ", members=" + members.size() + "}";
    }
}