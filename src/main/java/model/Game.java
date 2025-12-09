package model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public enum Mode {
        NORMAL,
        PICANTE,
        TEAM
    }

    private final Deck deck;
    private final Mode mode;
    private final Board board;
    private final List<Player> players = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();

    private int currentPlayerIndex = 0;
    private final List<Card> revealedCards = new ArrayList<>();

    public Game(Deck deck, Mode mode, Board board) {
        this.deck = deck;
        this.mode = mode;
        this.board = new Board(deck.getCards());
    }

    // ---------------------------
    //          SETUP
    // ---------------------------

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void addTeam(Team t) {
        teams.add(t);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // ---------------------------
    //        ACTIONS
    // ---------------------------

    /** Player flips a card */
    public boolean flipCard(Card card) {
        if (card.isFlipped()) return false;

        card.setFlipped(true);
        revealedCards.add(card);

        if (revealedCards.size() == 3)
            return checkTrio();

        return true;
    }

    /** Determine if the 3 revealed cards form a valid trio */
    private boolean checkTrio() {
        if (revealedCards.size() != 3) return false;

        int v1 = revealedCards.get(0).getValue();
        int v2 = revealedCards.get(1).getValue();
        int v3 = revealedCards.get(2).getValue();

        boolean success = (v1 == v2 && v2 == v3);

        if (success) rewardTrio();
        else failTrio();

        return success;
    }

    private void rewardTrio() {
        Player p = getCurrentPlayer();
        int pts = (mode == Mode.PICANTE ? 2 : 1);

        if (mode == Mode.TEAM) {
            Team t = findTeamOfPlayer(p);
            if (t != null) t.addPoint(pts);
        } else {
            p.addPoint(pts);
        }

        revealedCards.clear();
        // Player plays again in trio rules
    }

    private void failTrio() {
        // flip back cards
        for (Card c : revealedCards)
            c.setFlipped(false);

        revealedCards.clear();
        nextPlayer();
    }

    private Team findTeamOfPlayer(Player p) {
        for (Team t : teams) {
            if (t.getPlayers().contains(p)) return t;
        }
        return null;
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // ---------------------------
    //        GETTERS
    // ---------------------------

    public Deck getDeck() {
        return deck;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Card> getRevealedCards() {
        return revealedCards;
    }

    public Mode getMode() {
        return mode;
    }
}
