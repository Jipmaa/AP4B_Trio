package view;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.Board;

import java.io.FileInputStream;

public class BoardView extends StackPane {

    private Board board;
    private GridPane grid;

    public BoardView(Board board) {
        this.board = board;

        // Fond plateau → tu remplaceras l’image
        try {
            Image bgImg = new Image(getClass().getResourceAsStream("/images/board.png"));
            ImageView bg = new ImageView(bgImg);
            bg.setFitWidth(800);
            bg.setFitHeight(600);
            getChildren().add(bg);
        } catch (Exception e) {
            System.out.println("Missing board.png");
        }


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
