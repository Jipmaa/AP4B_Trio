package controller;

import javafx.stage.Stage;
import model.*;
import model.Game.Mode;
import view.GameSetupView;
import view.GameView;
import view.MenuView;

import java.util.ArrayList;
import java.util.List;

public class NavigationController {

    private Stage stage;

    public NavigationController(Stage stage) {
        this.stage = stage;
    }

    public void showMainMenu() {
        MenuView menuView = new MenuView(this, stage);
        menuView.show();
    }

    public void showGameSetup() {
        GameSetupView gameSetupView = new GameSetupView(this);
        stage.getScene().setRoot(gameSetupView);
    }

    public void startGame(Mode mode, int playerCount, String[] playerNames, String[][] teamNames) {
        Deck deck = new Deck("resources/cards.txt");
        Board board = new Board(new ArrayList<>()); // Board vide au départ

        Game game = new Game(deck, mode, board);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            Player p = new Player(playerNames[i]);
            players.add(p);
            game.addPlayer(p);
        }

        if (mode == Mode.TEAM) {
            Team team1 = new Team(teamNames[0][0]);
            Team team2 = new Team(teamNames[1][0]);

            // Simple répartition
            for (int i = 0; i < players.size(); i++) {
                if (i % 2 == 0) {
                    team1.addPlayer(players.get(i));
                } else {
                    team2.addPlayer(players.get(i));
                }
            }
            game.addTeam(team1);
            game.addTeam(team2);
        }

        // Distribuer les cartes
        game.distributeCards();

        GameController gameController = new GameController(game, this);
        GameView gameView = new GameView(game, gameController);
        stage.getScene().setRoot(gameView);
    }
}