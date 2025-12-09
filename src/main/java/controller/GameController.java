package controller;

import javafx.stage.Stage;
import model.Game;
import view.GameView;

public class GameController {

    private Game game;
    private GameView view;
    private Stage stage;

    public GameController(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;

        this.view = new GameView(game, this);

        stage.getScene().setRoot(view);
    }

    public void endTurn() {
        game.nextPlayer();
        view.refresh(game);
    }
}
