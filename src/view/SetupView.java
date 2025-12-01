package view;

import controller.NavigationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Game;

import java.util.ArrayList;
import java.util.List;

public class SetupView {

    private final NavigationController navigationController;
    private final VBox view;

    private ComboBox<Integer> numPlayersComboBox;
    private List<TextField> playerNameFields;
    private ToggleGroup gameModeToggleGroup;
    private ToggleGroup playModeToggleGroup;

    public SetupView(NavigationController navigationController) {
        this.navigationController = navigationController;
        this.view = new VBox(20);
        this.playerNameFields = new ArrayList<>();
        initializeView();
    }

    private void initializeView() {
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(50));
        view.setStyle("-fx-background-color: #336699;"); // A simple background color

        Label title = new Label("Trio Game Setup");
        title.setStyle("-fx-font-size: 36px; -fx-text-fill: white;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));
        formGrid.setAlignment(Pos.CENTER);

        // Number of Players
        Label numPlayersLabel = new Label("Number of Players:");
        numPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        numPlayersComboBox = new ComboBox<>();
        numPlayersComboBox.getItems().addAll(3, 4, 5, 6);
        numPlayersComboBox.setValue(4); // Default to 4 players
        numPlayersComboBox.setOnAction(e -> updatePlayerNameFields(numPlayersComboBox.getValue()));
        formGrid.add(numPlayersLabel, 0, 0);
        formGrid.add(numPlayersComboBox, 1, 0);

        // Player Names
        Label playerNamesLabel = new Label("Player Names:");
        playerNamesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        formGrid.add(playerNamesLabel, 0, 1);
        for (int i = 0; i < 6; i++) { // Max 6 players
            TextField nameField = new TextField("Player " + (i + 1));
            nameField.setPromptText("Enter Player " + (i + 1) + " Name");
            nameField.setVisible(i < numPlayersComboBox.getValue());
            nameField.setManaged(i < numPlayersComboBox.getValue());
            playerNameFields.add(nameField);
            formGrid.add(nameField, 1, 2 + i);
        }

        // Game Mode (Simple/Picante)
        Label gameModeLabel = new Label("Game Mode:");
        gameModeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        gameModeToggleGroup = new ToggleGroup();
        RadioButton simpleMode = new RadioButton("Simple");
        simpleMode.setToggleGroup(gameModeToggleGroup);
        simpleMode.setSelected(true);
        simpleMode.setStyle("-fx-text-fill: white;");
        RadioButton picanteMode = new RadioButton("Picante");
        picanteMode.setToggleGroup(gameModeToggleGroup);
        picanteMode.setStyle("-fx-text-fill: white;");
        VBox gameModeBox = new VBox(5, simpleMode, picanteMode);
        formGrid.add(gameModeLabel, 0, 8);
        formGrid.add(gameModeBox, 1, 8);

        // Play Mode (Individual/Team)
        Label playModeLabel = new Label("Play Mode:");
        playModeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        playModeToggleGroup = new ToggleGroup();
        RadioButton individualMode = new RadioButton("Individual");
        individualMode.setToggleGroup(playModeToggleGroup);
        individualMode.setSelected(true);
        individualMode.setStyle("-fx-text-fill: white;");
        RadioButton teamMode = new RadioButton("Team");
        teamMode.setToggleGroup(playModeToggleGroup);
        teamMode.setStyle("-fx-text-fill: white;");
        VBox playModeBox = new VBox(5, individualMode, teamMode);
        formGrid.add(playModeLabel, 0, 9);
        formGrid.add(playModeBox, 1, 9);

        // Listener for playModeToggleGroup to enable/disable team mode based on player count
        playModeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null && ((RadioButton) newToggle).getText().equals("Team")) {
                if (numPlayersComboBox.getValue() != 4 && numPlayersComboBox.getValue() != 6) {
                    showAlert("Team Mode requires 4 or 6 players.", "Please select 4 or 6 players for Team Mode.");
                    individualMode.setSelected(true); // Revert to individual mode
                }
            }
        });

        // Listener for numPlayersComboBox to update play mode options
        numPlayersComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != 4 && newVal != 6) {
                // If not 4 or 6 players, force Individual mode
                individualMode.setSelected(true);
                teamMode.setDisable(true);
            } else {
                teamMode.setDisable(false);
            }
        });


        Button startGameButton = new Button("Start Game");
        startGameButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        startGameButton.setOnAction(e -> startGame());

        view.getChildren().addAll(title, formGrid, startGameButton);

        // Initial update for player name fields
        updatePlayerNameFields(numPlayersComboBox.getValue());
    }

    private void updatePlayerNameFields(int count) {
        for (int i = 0; i < playerNameFields.size(); i++) {
            playerNameFields.get(i).setVisible(i < count);
            playerNameFields.get(i).setManaged(i < count);
        }
        // Also update play mode options based on player count
        RadioButton teamMode = (RadioButton) playModeToggleGroup.getToggles().get(1);
        RadioButton individualMode = (RadioButton) playModeToggleGroup.getToggles().get(0);
        if (count != 4 && count != 6) {
            individualMode.setSelected(true);
            teamMode.setDisable(true);
        } else {
            teamMode.setDisable(false);
        }
    }

    private void startGame() {
        int numPlayers = numPlayersComboBox.getValue();
        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            String name = playerNameFields.get(i).getText().trim();
            if (name.isEmpty()) {
                showAlert("Missing Player Name", "Please enter a name for Player " + (i + 1));
                return;
            }
            playerNames.add(name);
        }

        Game.GameMode gameMode = ((RadioButton) gameModeToggleGroup.getSelectedToggle()).getText().equals("Simple") ?
                Game.GameMode.SIMPLE : Game.GameMode.PICANTE;
        Game.PlayMode playMode = ((RadioButton) playModeToggleGroup.getSelectedToggle()).getText().equals("Individual") ?
                Game.PlayMode.INDIVIDUAL : Game.PlayMode.TEAM;

        if (playMode == Game.PlayMode.TEAM && (numPlayers != 4 && numPlayers != 6)) {
            showAlert("Invalid Player Count for Team Mode", "Team mode requires exactly 4 or 6 players.");
            return;
        }

        navigationController.startGame(gameMode, playMode, numPlayers, playerNames);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}
