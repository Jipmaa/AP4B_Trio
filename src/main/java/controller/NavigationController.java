package controller;

import javafx.stage.Stage;
import model.Deck;
import model.Game;
import model.Board;
import model.Player;
import model.Game.Mode;

public class NavigationController {

    private Stage stage;

    public NavigationController(Stage stage) {
        this.stage = stage;
    }

    public void startNormalGame() {
        Deck deck = new Deck("resources/cards.txt");
        Board board = new Board(deck.getCards());
        Game game = new Game(deck, Mode.NORMAL, board);

        game.addPlayer(new Player("Fabien"));
        game.addPlayer(new Player("Romulo"));
        game.addPlayer(new Player("Baptiste"));

        new GameController(game, stage);
    }
}
