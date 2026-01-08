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
            // Création des 3 équipes (pour 6 joueurs) ou 2 équipes (pour 4 joueurs)
            int numTeams = (playerCount == 6) ? 3 : 2;

            for (int i = 0; i < numTeams; i++) {
                // On récupère le nom saisi ou on en génère un par défaut
                String tName = (teamNames != null && teamNames.length > i && teamNames[i][0] != null)
                        ? teamNames[i][0] : "Équipe " + (i + 1);
                Team team = new Team(tName);
                game.addTeam(team);
            }

            // Répartition des joueurs dans les équipes :
            // Joueur 0 et 1 -> Équipe 0, Joueur 2 et 3 -> Équipe 1, etc.
            for (int i = 0; i < players.size(); i++) {
                int teamIndex = i / 2;
                // Sécurité pour ne pas dépasser le nombre d'équipes créées
                if (teamIndex < game.getTeams().size()) {
                    game.getTeams().get(teamIndex).addPlayer(players.get(i));
                }
            }
        }

        // Distribuer les cartes
        game.distributeCards();

        GameController gameController = new GameController(game, this);
        GameView gameView = new GameView(game, gameController);
        stage.getScene().setRoot(gameView);
    }
}