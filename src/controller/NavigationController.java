package controller;

import javafx.scene.Scene;
import model.Game;
import view.BoardView;
import view.EndGameView;
import view.MainView;

import java.util.List;

public class NavigationController {

    public final MainView mainView; // Made public to be accessible from EndGameView
    private Game game; // The game instance

    public NavigationController(MainView mainView) {
        this.mainView = mainView;
    }

    public void startGame(Game.GameMode gameMode, Game.PlayMode playMode, int numberOfPlayers, List<String> playerNames) {
        this.game = new Game(gameMode, playMode, numberOfPlayers, playerNames);
        game.setupGame(); // Initialize the game model

        BoardView boardView = new BoardView(game, this); // Pass the game instance to the BoardView
        Scene gameScene = new Scene(boardView.getView(), mainView.getPrimaryStage().getWidth(), mainView.getPrimaryStage().getHeight());
        mainView.getPrimaryStage().setScene(gameScene);
    }

    public void endGame(String winnerName) {
        EndGameView endGameView = new EndGameView(winnerName, this);
        Scene endGameScene = new Scene(endGameView.getView(), mainView.getPrimaryStage().getWidth(), mainView.getPrimaryStage().getHeight());
        mainView.getPrimaryStage().setScene(endGameScene);
    }

    public Game getGame() {
        return game;
    }
}
