package view;

import controller.GameController;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import model.Board;

public class BoardView extends StackPane {

    private Board board;
    private GridPane grid;

    public BoardView(Board board, GameController controller) {
        this.board = board;

        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(15);

        refresh();

        getChildren().add(grid);
    }

    public void refresh() {
        grid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (var card : board.getCenterCards()) {
            CardView cv = new CardView(card);

            grid.add(cv, col, row);

            col++;
            if (col >= 6) {
                col = 0;
                row++;
            }
        }
    }
}
