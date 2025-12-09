package model;

public class Player {

    private final String name;
    private Team team;      // null en mode solo
    private int score;

    public Player(String name) {
        this.name = name;
        this.team = null;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getScore() {
        return score;
    }

    public void addPoint(int pts) {
        this.score += pts;

        if (team != null)
            team.addPoint(pts);
    }
}
