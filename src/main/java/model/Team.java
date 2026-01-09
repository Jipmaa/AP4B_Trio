package model;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final String name;
    private final List<Player> members = new ArrayList<>();
    private int score;

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