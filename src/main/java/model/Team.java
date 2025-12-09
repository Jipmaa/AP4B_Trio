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
        members.add(p);
        p.setTeam(this);
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
}
