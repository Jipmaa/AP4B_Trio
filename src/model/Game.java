package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {

    public enum GameMode {
        SIMPLE,
        PICANTE
    }

    public enum PlayMode {
        INDIVIDUAL,
        TEAM
    }

    public enum GameState {
        LOBBY,
        SETUP,
        CARD_EXCHANGE, // New state for team mode card exchange
        IN_PROGRESS,
        TURN_ENDED, // A turn has ended, cards are being returned, next player is set
        GAME_OVER
    }

    // Helper class to track revealed cards and their origin
    private static class RevealedCardInfo {
        Card card;
        Player originalPlayer; // Null if from board
        int originalBoardIndex; // -1 if from player hand, or the index on the board if from board

        RevealedCardInfo(Card card, Player originalPlayer, int originalBoardIndex) {
            this.card = card;
            this.originalPlayer = originalPlayer;
            this.originalBoardIndex = originalBoardIndex;
        }
    }

    private GameMode gameMode;
    private PlayMode playMode;
    private GameState gameState;
    private int numberOfPlayers;
    private List<Player> players;
    private List<Team> teams;
    private Deck deck;
    private Board board;
    private int currentPlayerIndex;
    private List<RevealedCardInfo> revealedCardsThisTurn; // Cards revealed during the current player's turn
    private Card lastRevealedCard; // The last card revealed by the current player

    // For card exchange in Team Mode
    private Map<Player, Card> proposedExchanges; // Player -> Card they want to exchange
    private Map<Player, Boolean> exchangeConfirmed; // Player -> true if they confirmed exchange

    public Game(GameMode gameMode, PlayMode playMode, int numberOfPlayers, List<String> playerNames) {
        this.gameMode = gameMode;
        this.playMode = playMode;
        this.numberOfPlayers = numberOfPlayers;
        this.gameState = GameState.LOBBY;
        this.players = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.revealedCardsThisTurn = new ArrayList<>();
        this.proposedExchanges = new HashMap<>(); // Initialize
        this.exchangeConfirmed = new HashMap<>(); // Initialize
        initializePlayers(playerNames);
    }

    private void initializePlayers(List<String> playerNames) {
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        if (playMode == PlayMode.TEAM) {
            // Assuming 4 or 6 players for team mode, forming teams of 2
            for (int i = 0; i < players.size(); i += 2) {
                Team team = new Team("Team " + ((i / 2) + 1));
                team.addPlayer(players.get(i));
                team.addPlayer(players.get(i + 1));
                teams.add(team);
            }
        }
    }

    public void setupGame() {
        this.deck = new Deck();
        this.board = new Board();
        deck.shuffle();
        dealCards();
        this.currentPlayerIndex = new Random().nextInt(numberOfPlayers); // Randomly choose starting player
        if (playMode == PlayMode.TEAM) {
            this.gameState = GameState.CARD_EXCHANGE;
            System.out.println("Game setup complete. Entering card exchange phase for teams.");
        } else {
            this.gameState = GameState.IN_PROGRESS;
            System.out.println("Game setup complete. Starting player: " + players.get(currentPlayerIndex).getName());
        }
    }

    private void dealCards() {
        // Rules for dealing cards vary by play mode and number of players
        // Individual Mode:
        // 3 players: 9 cards each, 9 in center
        // 4 players: 7 cards each, 8 in center
        // 5 players: 6 cards each, 6 in center
        // 6 players: 5 cards each, 6 in center
        // Team Mode: All cards distributed among players, none in center. (4 players: 9 each, 6 players: 6 each)

        if (playMode == PlayMode.INDIVIDUAL) {
            int cardsPerPlayer = 0;
            int cardsInCenter = 0;

            switch (numberOfPlayers) {
                case 3: cardsPerPlayer = 9; cardsInCenter = 9; break;
                case 4: cardsPerPlayer = 7; cardsInCenter = 8; break;
                case 5: cardsPerPlayer = 6; cardsInCenter = 6; break;
                case 6: cardsPerPlayer = 5; cardsInCenter = 6; break;
            }

            for (int i = 0; i < cardsPerPlayer; i++) {
                for (Player player : players) {
                    player.addCardToHand(deck.drawCard());
                }
            }
            for (int i = 0; i < cardsInCenter; i++) {
                board.addCard(deck.drawCard());
            }
        } else if (playMode == PlayMode.TEAM) {
            // All 36 cards are distributed among players
            int cardsPerPlayer = 36 / numberOfPlayers;
            for (int i = 0; i < cardsPerPlayer; i++) {
                for (Player player : players) {
                    player.addCardToHand(deck.drawCard());
                }
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void revealCardFromCenter(int cardIndex) {
        if (gameState != GameState.IN_PROGRESS) return;
        if (cardIndex < 0 || cardIndex >= board.getCenterCards().size()) {
            System.out.println("Invalid card index on board.");
            return;
        }

        Card revealed = board.getCenterCards().get(cardIndex); // Get card without removing yet
        revealedCardsThisTurn.add(new RevealedCardInfo(revealed, null, cardIndex));
        lastRevealedCard = revealed;
        System.out.println(getCurrentPlayer().getName() + " revealed " + revealed + " from center.");
        checkTurnOutcome();
    }

    public void revealCardFromPlayerHand(Player targetPlayer, boolean smallest) {
        if (gameState != GameState.IN_PROGRESS) return;
        if (!players.contains(targetPlayer)) {
            System.out.println("Target player not found.");
            return;
        }
        if (targetPlayer.getHand().isEmpty()) {
            System.out.println(targetPlayer.getName() + " has no cards to reveal.");
            endTurn(false); // End turn if no card could be revealed
            return;
        }

        Card revealed = null;
        if (smallest) {
            revealed = targetPlayer.getSmallestCard();
        } else {
            revealed = targetPlayer.getLargestCard();
        }

        if (revealed != null) {
            // Do not remove from hand yet, just track it
            revealedCardsThisTurn.add(new RevealedCardInfo(revealed, targetPlayer, -1));
            lastRevealedCard = revealed;
            System.out.println(getCurrentPlayer().getName() + " revealed " + revealed + " from " + targetPlayer.getName() + "'s hand (isSmallest: " + smallest + ").");
            checkTurnOutcome();
        } else {
            System.out.println("Error: Card not found in " + targetPlayer.getName() + "'s hand.");
            endTurn(false);
        }
    }

    private void checkTurnOutcome() {
        if (revealedCardsThisTurn.size() < 2) {
            // Need more cards to check for a pair/trio
            return;
        }

        // Check if the last revealed card matches any previous revealed card this turn
        boolean matchFound = false;
        Card currentCard = revealedCardsThisTurn.get(revealedCardsThisTurn.size() - 1).card;
        for (int i = 0; i < revealedCardsThisTurn.size() - 1; i++) {
            if (currentCard.equals(revealedCardsThisTurn.get(i).card)) {
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            // If the last revealed card doesn't match any previous, the turn ends
            System.out.println("No match found. Turn ends.");
            endTurn(false);
        } else if (revealedCardsThisTurn.size() == 3 &&
                   revealedCardsThisTurn.get(0).card.equals(revealedCardsThisTurn.get(1).card) &&
                   revealedCardsThisTurn.get(1).card.equals(revealedCardsThisTurn.get(2).card)) {
            // Three identical cards revealed - a trio is formed!
            System.out.println("Trio formed: " + revealedCardsThisTurn.get(0).card.getValue());
            
            // Remove cards from their original locations
            List<Card> trioCards = new ArrayList<>();
            for(RevealedCardInfo info : revealedCardsThisTurn) {
                if (info.originalPlayer != null) {
                    info.originalPlayer.removeCardFromHand(info.card);
                } else {
                    board.removeCard(info.card); // Remove from board
                }
                trioCards.add(info.card);
            }
            getCurrentPlayer().addTrio(trioCards);
            endTurn(true); // Player re-plays
        }
        // If two cards match, but not three, the player can continue to try for a third
        // This is handled by not calling endTurn(false)
    }

    private void endTurn(boolean playerReplays) {
        if (!playerReplays) {
            // Return revealed cards to their original places
            for (RevealedCardInfo info : revealedCardsThisTurn) {
                if (info.originalPlayer != null) {
                    info.originalPlayer.addCardToHand(info.card); // Return to player's hand
                } else {
                    // If from board, it was never removed, so no need to add back
                    // However, if it was removed to be revealed, it needs to be put back at its original index
                    // This requires a more complex board structure or tracking original positions
                    // For now, if it was from the board and not part of a trio, it stays on the board
                }
            }
            revealedCardsThisTurn.clear();
            lastRevealedCard = null;
            moveToNextPlayer();
        } else {
            // Player re-plays, so clear revealed cards but don't change player
            revealedCardsThisTurn.clear();
            lastRevealedCard = null;
        }
        checkGameOver();
    }

    private void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % numberOfPlayers;
        System.out.println("Next player: " + getCurrentPlayer().getName());
    }

    private void checkGameOver() {
        // Game ends when all trios are found (12 trios total)
        int totalTriosFound = players.stream().mapToInt(Player::getScore).sum();
        if (totalTriosFound == 12) { // 12 unique trios (1-12)
            gameState = GameState.GAME_OVER;
                    determineWinner();
                    return;
        }

        // Check for specific win conditions based on game mode
        if (gameMode == GameMode.SIMPLE) {
            for (Player player : players) {
                if (player.getScore() >= 3) { // First to 3 trios
                    gameState = GameState.GAME_OVER;
                    determineWinner();
                    return;
                }
                // Check for trio of 7s
                if (player.getTriosWon().stream().anyMatch(trio -> trio.get(0).getValue() == 7)) {
                    gameState = GameState.GAME_OVER;
                    System.out.println("Game Over! " + player.getName() + " won with the trio of 7s!");
                    determineWinner();
                    return;
                }
            }
            if (playMode == PlayMode.TEAM) {
                for (Team team : teams) {
                    if (team.getScore() >= 3) {
                        gameState = GameState.GAME_OVER;
                        determineWinner();
                        return;
                    }
                    if (team.getAllTriosWon().stream().anyMatch(trio -> trio.get(0).getValue() == 7)) {
                        gameState = GameState.GAME_OVER;
                        determineWinner();
                        return;
                    }
                }
            }
        } else if (gameMode == GameMode.PICANTE) {
            for (Player player : players) {
                if (checkLinkedTrios(player.getTriosWon())) {
                    gameState = GameState.GAME_OVER;
                    determineWinner();
                    return;
                }
                // Check for trio of 7s
                if (player.getTriosWon().stream().anyMatch(trio -> trio.get(0).getValue() == 7)) {
                    gameState = GameState.GAME_OVER;
                    System.out.println("Game Over! " + player.getName() + " won with the trio of 7s!");
                    determineWinner();
                    return;
                }
            }
            if (playMode == PlayMode.TEAM) {
                for (Team team : teams) {
                    if (checkLinkedTrios(team.getAllTriosWon())) {
                        gameState = GameState.GAME_OVER;
                        determineWinner();
                        return;
                    }
                    if (team.getAllTriosWon().stream().anyMatch(trio -> trio.get(0).getValue() == 7)) {
                        gameState = GameState.GAME_OVER;
                        determineWinner();
                        return;
                    }
                }
            }
        }
    }

    private boolean checkLinkedTrios(List<List<Card>> trios) {
        if (trios.size() < 2) return false;
        List<Integer> trioValues = trios.stream()
                .map(trio -> trio.get(0).getValue())
                .collect(Collectors.toList());

        for (int i = 0; i < trioValues.size(); i++) {
            for (int j = i + 1; j < trioValues.size(); j++) {
                Card card1 = new Card(trioValues.get(i)); // Create temporary cards to use isLinkedWith
                Card card2 = new Card(trioValues.get(j));
                if (card1.isLinkedWith(card2)) {
                    return true; // Found two linked trios
                }
            }
        }
        return false;
    }

    public String determineWinner() {
        String winnerName = "No one";
        if (playMode == PlayMode.INDIVIDUAL) {
            Player winner = null;
            int maxScore = -1;
            for (Player player : players) {
                if (player.getScore() > maxScore) {
                    maxScore = player.getScore();
                    winner = player;
                } else if (player.getScore() == maxScore) {
                    if (player.getTriosWon().stream().anyMatch(trio -> trio.get(0).getValue() == 7)) {
                        winner = player;
                    }
                }
            }
            if (winner != null) {
                winnerName = winner.getName();
            }
        } else { // Team mode
            Team winner = null;
            int maxScore = -1;
            for (Team team : teams) {
                if (team.getScore() > maxScore) {
                    maxScore = team.getScore();
                    winner = team;
                } else if (team.getScore() == maxScore) {
                    if (team.getAllTriosWon().stream().anyMatch(trio -> trio.get(0).getValue() == 7)) {
                        winner = team;
                    }
                }
            }
            if (winner != null) {
                winnerName = winner.getName();
            }
        }
        return winnerName;
    }

    public void proposeCardForExchange(Player player, Card card) {
        if (gameState != GameState.CARD_EXCHANGE || playMode != PlayMode.TEAM) {
            System.out.println("Cannot propose card for exchange outside of card exchange phase in team mode.");
            return;
        }
        if (!player.getHand().contains(card)) {
            System.out.println(player.getName() + " does not have " + card + " in hand.");
            return;
        }
        proposedExchanges.put(player, card);
        System.out.println(player.getName() + " proposed " + card + " for exchange.");
    }

    public void confirmExchange(Player player) {
        if (gameState != GameState.CARD_EXCHANGE || playMode != PlayMode.TEAM) {
            System.out.println("Cannot confirm exchange outside of card exchange phase in team mode.");
            return;
        }
        if (!proposedExchanges.containsKey(player)) {
            System.out.println(player.getName() + " has not proposed a card for exchange yet.");
            return;
        }
        exchangeConfirmed.put(player, true);
        System.out.println(player.getName() + " confirmed exchange.");

        if (isExchangePhaseComplete()) {
            executeExchanges();
            this.gameState = GameState.IN_PROGRESS;
            System.out.println("Card exchange phase complete. Game in progress.");
        }
    }

    public boolean isExchangePhaseComplete() {
        if (playMode != PlayMode.TEAM) return true; // Not applicable for individual mode
        // All players in teams must have proposed a card and confirmed
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                if (!proposedExchanges.containsKey(player) || !exchangeConfirmed.getOrDefault(player, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void executeExchanges() {
        if (playMode != PlayMode.TEAM) return;

        for (Team team : teams) {
            Player player1 = team.getPlayers().get(0);
            Player player2 = team.getPlayers().get(1);

            Card card1 = proposedExchanges.get(player1);
            Card card2 = proposedExchanges.get(player2);

            if (card1 != null && card2 != null) {
                // Perform the swap
                player1.removeCardFromHand(card1);
                player2.removeCardFromHand(card2);

                player1.addCardToHand(card2);
                player2.addCardToHand(card1);
                System.out.println("Team " + team.getName() + " exchanged cards: " + player1.getName() + " got " + card2 + ", " + player2.getName() + " got " + card1);
            }
        }
        proposedExchanges.clear();
        exchangeConfirmed.clear();
    }

    // Getters for game state and components
    public GameMode getGameMode() {
        return gameMode;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Board getBoard() {
        return board;
    }

    public List<Card> getRevealedCards() {
        return revealedCardsThisTurn.stream().map(info -> info.card).collect(Collectors.toList());
    }

    public Card getLastRevealedCard() {
        return lastRevealedCard;
    }
}

