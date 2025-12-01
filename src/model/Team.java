package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    private final String name;
    private final List<Player> players;

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (player != null && players.size() < 2) { // Trio team typically has 2 players
            players.add(player);
        } else if (player != null) {
            System.err.println("Team " + name + " already has 2 players. Cannot add more.");
        }
    }

    public int getScore() {
        return players.stream().mapToInt(Player::getScore).sum();
    }

    public List<List<Card>> getAllTriosWon() {
        List<List<Card>> allTrios = new ArrayList<>();
        for (Player player : players) {
            allTrios.addAll(player.getTriosWon());
        }
        return allTrios;
    }

    @Override
    public String toString() {
        return "Team{" + "name='" + name + "\'" + ", players=" + players.stream().map(Player::getName).toList() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
