package controller;

import model.Game;

public class GameController {

    private Game game;
    private NavigationController navController;

    public GameController(Game game, NavigationController navController) {
        this.game = game;
        this.navController = navController;
    }

    public void endTurn() {
        game.nextPlayer();
        // The view will be responsible for observing the model and refreshing itself.
        // Or the view will call methods on the controller to get state.
        // For now, we just update the model.
    }

    public void returnToMenu() {
        navController.showMainMenu();
    }
    
    public Game getGame() {
        return game;
    }
}
