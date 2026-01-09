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
    private String lastPicanteLetter;
    private boolean picanteEnabled = false; // Nouveau : Active la roulette en mode TEAM


    private int currentPlayerIndex = 0;
    private final List<Card> revealedCards = new ArrayList<>();
    private boolean gameOver = false;
    private Runnable onPicanteTrio;
    private Runnable onTeamCardExchange; // Nouveau callback pour l'échange de cartes

    public void setOnPicanteTrio(Runnable callback) {
        this.onPicanteTrio = callback;
    }

    public void setOnTeamCardExchange(Runnable callback) {
        this.onTeamCardExchange = callback;
    }

    public void setLastPicanteLetter(String letter) {
        this.lastPicanteLetter = letter;
    }

    public String getLastPicanteLetter() {
        return lastPicanteLetter;
    }

    public void setPicanteEnabled(boolean enabled) {
        this.picanteEnabled = enabled;
    }

    public boolean isPicanteEnabled() {
        return picanteEnabled;
    }


    public Game(Deck deck, Mode mode, Board board) {
        this.deck = deck;
        this.mode = mode;
        this.board = board;
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

    public void distributeCards() {
        // Vider le board d'abord
        board.getCenterCards().clear();

        List<Card> allCards = new ArrayList<>(deck.getCards());
        int playerCount = players.size();
        int cardsPerPlayer = 0;
        int centerCards = 0;

        if (mode == Mode.TEAM) {
            // En mode équipe, distribuer toutes les cartes
            cardsPerPlayer = allCards.size() / playerCount;
        } else {
            // En mode solo, selon le nombre de joueurs
            switch (playerCount) {
                case 3:
                    cardsPerPlayer = 9;
                    centerCards = 9;
                    break;
                case 4:
                    cardsPerPlayer = 7;
                    centerCards = 8;
                    break;
                case 5:
                    cardsPerPlayer = 6;
                    centerCards = 6;
                    break;
                case 6:
                    cardsPerPlayer = 5;
                    centerCards = 6;
                    break;
                default:
                    cardsPerPlayer = 7;
                    centerCards = 8;
            }
        }

        int cardIndex = 0;

        // Distribuer aux joueurs
        for (Player player : players) {
            for (int i = 0; i < cardsPerPlayer && cardIndex < allCards.size(); i++) {
                Card card = allCards.get(cardIndex++);
                player.addCardToHand(card);
            }
            player.sortHand(); // Trier les cartes du joueur
        }

        // Mettre le reste au centre (mode solo uniquement)
        if (mode != Mode.TEAM) {
            for (int i = 0; i < centerCards && cardIndex < allCards.size(); i++) {
                board.addCard(allCards.get(cardIndex++));
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // ---------------------------
    //        ACTIONS
    // ---------------------------

    /**
     * Tentative de retourner une carte
     */


    public boolean attemptFlipCard(Card card) {
        // 1. Vérifier si la carte est déjà révélée ce tour-ci
        if (revealedCards.contains(card)) {
            return false;
        }

        // 2. Si la carte est sur le plateau central
        if (board.getCenterCards().contains(card)) {
            return flipBoardCard(card);
        }

        // 3. Si la carte appartient à un joueur
        for (Player p : players) {
            if (p.getHand().contains(card)) {
                // Appeler la méthode spécifique aux joueurs (qui vérifie les extrémités)
                return flipPlayerCard(card, p);
            }
        }

        return false;
    }

    private boolean flipBoardCard(Card card) {
        card.setFlipped(true);
        revealedCards.add(card);
        return true;
    }

    private boolean flipPlayerCard(Card card, Player owner) {
        if (!owner.canFlipCard(card)) {
            return false;
        }

        card.setFlipped(true);
        revealedCards.add(card);
        owner.markCardRevealed(card);

        return true;
    }



    /**
     * Vérifie si les 2 premières cartes révélées correspondent
     */
    private boolean checkPairMatch() {
        if (revealedCards.size() != 2) return false;

        int v1 = revealedCards.get(0).getValue();
        int v2 = revealedCards.get(1).getValue();

        return v1 == v2;
    }

    private boolean isTripleSeven() {
        if (revealedCards.size() != 3) return false;

        return revealedCards.get(0).getValue() == 7
                && revealedCards.get(1).getValue() == 7
                && revealedCards.get(2).getValue() == 7;
    }



    /**
     * Vérifie si les 3 cartes révélées forment un trio
     */
    public boolean checkTrio() {
        if (revealedCards.size() < 2) return false;

        if (revealedCards.size() == 2) {
            int v1 = revealedCards.get(0).getValue();
            int v2 = revealedCards.get(1).getValue();
            return (v1 == v2);
        }

        if (revealedCards.size() == 3) {
            int v1 = revealedCards.get(0).getValue();
            int v2 = revealedCards.get(1).getValue();
            int v3 = revealedCards.get(2).getValue();
            return (v1 == v2 && v2 == v3);
        }

        return false;
    }

    /**
     * Récompenser le joueur pour un trio réussi
     */
    public void rewardTrio() {
        Player p = getCurrentPlayer();

        // En mode TEAM avec Picante activé, déclencher la roulette
        if (mode == Mode.TEAM && picanteEnabled && onPicanteTrio != null) {
            onPicanteTrio.run();
            return; // La suite sera gérée dans applyPicanteReward
        }

        // En mode PICANTE solo, déclencher la roulette
        if (mode == Mode.PICANTE && onPicanteTrio != null) {
            onPicanteTrio.run();
            return;
        }

        int pts = 1;

        if (mode == Mode.TEAM) {
            Team t = findTeamOfPlayer(p);
            if (t != null) t.addPoint(pts);

            // Retirer les cartes...
            for (Card card : revealedCards) {
                board.removeCard(card);
                for (Player player : players) {
                    player.removeCardFromHand(card);
                }
            }

            revealedCards.clear();

            checkGameOver();

            // Déclencher l'échange de cartes SEULEMENT si la partie n'est pas terminée
            if (!gameOver && onTeamCardExchange != null) {
                onTeamCardExchange.run();
            }
        } else {
            p.addPoint(pts);

            // Retirer les cartes...
            for (Card card : revealedCards) {
                board.removeCard(card);
                for (Player player : players) {
                    player.removeCardFromHand(card);
                }
            }

            revealedCards.clear();

            checkGameOver();
        }
    }

    public void applyPicanteReward(String letter) {
        Player p = getCurrentPlayer();
        int pts = 0;

        // Attribution des points selon la lettre
        switch(letter) {
            case "A": pts = 2; break;
            case "B": pts = 1; break;
            case "C": pts = 1; break;
            case "D": pts = 1; break;
            case "E": pts = 0; break;
            case "F": pts = -1; break;
        }

        if (mode == Mode.TEAM) {
            Team t = findTeamOfPlayer(p);
            if (t != null) t.addPoint(pts);

            // Retirer les cartes
            for (Card card : revealedCards) {
                board.removeCard(card);
                for (Player player : players) {
                    player.removeCardFromHand(card);
                }
            }

            revealedCards.clear();

            checkGameOver();

            // Déclencher l'échange de cartes APRÈS la roulette
            // L'équipe gagnante ne peut PAS échanger
            // ET seulement si la partie n'est pas terminée
            if (!gameOver && onTeamCardExchange != null) {
                onTeamCardExchange.run();
            }

        } else {
            p.addPoint(pts);

            // Retirer les cartes
            for (Card card : revealedCards) {
                board.removeCard(card);
                for (Player player : players) {
                    player.removeCardFromHand(card);
                }
            }

            revealedCards.clear();
            checkGameOver();
        }
    }


    /**
     * Échec du trio - retourner les cartes
     */
    public void failTrio() {
        for (Card c : revealedCards) {
            // Si la carte appartient au board, on la cache
            if (board.getCenterCards().contains(c)) {
                c.setFlipped(false);
            }
            // Les cartes des joueurs ne changent pas d'état visible/caché
        }

        // Réinitialiser les cartes révélées de tous les joueurs
        for (Player player : players) {
            player.resetRevealedThisTurn();
        }

        revealedCards.clear();
        nextPlayer();
    }

    /**
     * Échec de la paire - retourner les cartes après un délai
     */
    public void failPair() {
        for (Card c : revealedCards) {
            // Si la carte appartient au board, on la cache
            if (board.getCenterCards().contains(c)) {
                c.setFlipped(false);
            }
            // Les cartes des joueurs ne changent pas d'état visible/caché
        }

        // Réinitialiser les cartes révélées de tous les joueurs
        for (Player player : players) {
            player.resetRevealedThisTurn();
        }

        revealedCards.clear();
        nextPlayer();
    }

    private Team findTeamOfPlayer(Player p) {
        for (Team t : teams) {
            if (t.getPlayers().contains(p)) return t;
        }
        return null;
    }

    /**
     * Trouve le coéquipier d'un joueur
     */
    public Player getTeammate(Player player) {
        Team team = findTeamOfPlayer(player);
        if (team == null) return null;

        for (Player p : team.getPlayers()) {
            if (!p.equals(player)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Échange deux cartes entre deux joueurs
     */
    public void exchangeCards(Player player1, Card card1, Player player2, Card card2) {
        // Retirer les cartes des mains
        player1.removeCardFromHand(card1);
        player2.removeCardFromHand(card2);

        // Ajouter les cartes aux nouvelles mains
        player1.addCardToHand(card2);
        player2.addCardToHand(card1);

        // Retrier les mains
        player1.sortHand();
        player2.sortHand();
    }

    public void nextPlayer() {
        // 1. Remettre toutes les cartes actuellement révélées face cachée
        for (Card card : revealedCards) {
            card.setFlipped(false);
        }

        // 2. Vider la liste des cartes révélées du plateau pour le nouveau tour
        revealedCards.clear();

        // 3. Réinitialiser les cartes révélées de chaque joueur (logique interne)
        for (Player player : players) {
            // IMPORTANT : Si des cartes de la main étaient retournées, on les cache aussi
            for (Card card : player.getHand()) {
                // On ne cache que si elle n'est pas censée être vue (ex: fin du tour)
                // La visibilité de la main du joueur local est gérée par forceVisible dans la vue
                card.setFlipped(false);
            }
            player.resetRevealedThisTurn();
        }

        // 4. Changer de joueur
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Vérifie si la partie est terminée (plus de trios possibles)
     */
    private void checkGameOver() {
        // 1. Vérification par le score (Victoire par 3 trios)
        if (mode == Mode.TEAM) {
            for (Team t : teams) {
                if (t.getScore() >= 3) {
                    gameOver = true;
                    return;
                }
            }
        } else {
            for (Player p : players) {
                if (p.getScore() >= 3) {
                    gameOver = true;
                    return;
                }
            }
        }

        if(isTripleSeven()){
            gameOver = true;
            return;
        }

        // 2. Vérification par épuisement des cartes
        int totalCards = board.size();
        for (Player player : players) {
            totalCards += player.getHand().size();
        }

        if (totalCards < 3) {
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
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

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}