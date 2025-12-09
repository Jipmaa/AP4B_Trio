package view;

import controller.GameController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.Game;

public class GameView extends BorderPane {

    private BoardView boardView;
    private Label turnsLabel;

    public GameView(Game game, GameController controller) {
        this.boardView = new BoardView(game.getBoard());
        this.turnsLabel = new Label("Tour : " + game.getCurrentPlayer().getName());

        setCenter(boardView);

        Button endTurn = new Button("Fin du tour");
        endTurn.setOnAction(e -> controller.endTurn());

        HBox topBar = new HBox(20, turnsLabel, endTurn);
        topBar.setPadding(new Insets(10));

        setTop(topBar);
    }

    public void refresh(Game game) {
        turnsLabel.setText("Tour : " + game.getCurrentPlayer().getName());
        boardView.refresh();
    }
}
