package view;

import controller.NavigationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Game.Mode;

public class GameSetupView extends StackPane {

    private ToggleGroup modeGroup;
    private CheckBox picanteCheckBox;
    private Spinner<Integer> playerSpinner;
    private VBox playerNamesBox;
    private VBox teamNamesBox;
    private TextField[] playerNameFields;
    private TextField[] teamNameFields;
    private final String[] TEAM_COLORS = {"#4CAF50", "#2196F3", "#9C27B0"};

    public GameSetupView(NavigationController navController) {
        setBackground(new Background(new BackgroundFill(Color.web("#585858"), CornerRadii.EMPTY, Insets.EMPTY)));

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox configPanel = new VBox(25);
        configPanel.setAlignment(Pos.CENTER);
        configPanel.setPadding(new Insets(40));
        configPanel.setStyle("-fx-background-color: rgba(30, 35, 45, 0.95); -fx-background-radius: 20; -fx-border-color: #FFD700; -fx-border-width: 3; -fx-border-radius: 20;");
        configPanel.setEffect(new DropShadow(20, Color.BLACK));
        configPanel.setMaxWidth(600);

        Label title = new Label("CONFIGURATION DE LA PARTIE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#FFD700"));

        // Mode de Jeu
        VBox modeBox = createSection("Mode de Jeu");
        modeGroup = new ToggleGroup();
        RadioButton normalMode = createRadioButton("Solo", modeGroup, true);
        RadioButton teamMode = createRadioButton("Équipes", modeGroup, false);
        modeBox.getChildren().addAll(normalMode, teamMode);

        picanteCheckBox = new CheckBox("Mode Picante");
        picanteCheckBox.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        picanteCheckBox.setTextFill(Color.web("#FF6B6B"));

        // Nombre de Joueurs
        VBox playerCountBox = createSection("Nombre de Joueurs");
        playerSpinner = new Spinner<>(3, 6, 3);
        playerSpinner.setPrefWidth(80);
        playerCountBox.getChildren().add(playerSpinner);

        playerNamesBox = createSection("Noms des Joueurs");
        teamNamesBox = createSection("Noms des Équipes");
        teamNamesBox.setVisible(false);
        teamNamesBox.setManaged(false);

        // INITIALISATION DES CHAMPS
        updatePlayerNameFields(3);

        // --- LISTENERS UNIQUES ET CORRIGÉS ---

        teamMode.selectedProperty().addListener((obs, oldVal, isTeamMode) -> {
            teamNamesBox.setVisible(isTeamMode);
            teamNamesBox.setManaged(isTeamMode);

            if (isTeamMode) {

                // Force un nombre pair en mode équipe
                if (playerSpinner.getValue() % 2 != 0) {
                    playerSpinner.getValueFactory().setValue(4);
                }
                updateTeamNameFields(playerSpinner.getValue());
            }
        });

        playerSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (teamMode.isSelected() && newVal % 2 != 0) {
                // Sauter les nombres impairs en mode équipe
                int adjusted = (newVal > oldVal) ? newVal + 1 : newVal - 1;
                playerSpinner.getValueFactory().setValue(Math.max(4, Math.min(6, adjusted)));
                return;
            }
            updatePlayerNameFields(playerSpinner.getValue());
            if (teamMode.isSelected()) {
                updateTeamNameFields(playerSpinner.getValue());
            }
        });

        // Boutons
        Button startButton = createStyledButton("COMMENCER", "#4CAF50");
        startButton.setOnAction(e -> startGame(navController));
        Button backButton = createStyledButton("RETOUR", "#757575");
        backButton.setOnAction(e -> navController.showMainMenu());

        HBox buttonBox = new HBox(20, backButton, startButton);
        buttonBox.setAlignment(Pos.CENTER);

        configPanel.getChildren().addAll(title, modeBox, picanteCheckBox, playerCountBox, playerNamesBox, teamNamesBox, buttonBox);
        getChildren().addAll(overlay, configPanel);
    }

    private void updatePlayerNameFields(int count) {
        playerNamesBox.getChildren().removeIf(node -> node instanceof TextField);
        playerNameFields = new TextField[count];

        boolean isTeamMode = ((RadioButton) modeGroup.getSelectedToggle()).getText().equals("Équipes");

        for (int i = 0; i < count; i++) {
            playerNameFields[i] = new TextField("Joueur " + (i + 1));
            playerNameFields[i].setFont(Font.font("Arial", 14));

            // Style de base
            String style = "-fx-background-radius: 5; -fx-padding: 8; -fx-background-color: #f4f4f4;";

            if (isTeamMode) {
                // Joueur 1&2 -> Couleur 1, Joueur 3&4 -> Couleur 2, etc.
                String teamColor = TEAM_COLORS[i / 2];
                style += "-fx-border-color: " + teamColor + "; -fx-border-width: 2; -fx-border-radius: 5;";
            } else {
                style += "-fx-border-color: #757575; -fx-border-width: 1; -fx-border-radius: 5;";
            }

            playerNameFields[i].setStyle(style);
            playerNamesBox.getChildren().add(playerNameFields[i]);
        }
    }

    private void updateTeamNameFields(int playerCount) {
        teamNamesBox.getChildren().removeIf(node -> node instanceof TextField);
        int numTeams = playerCount / 2;
        teamNameFields = new TextField[numTeams];

        for (int i = 0; i < numTeams; i++) {
            teamNameFields[i] = new TextField("Équipe " + (i + 1));
            teamNameFields[i].setFont(Font.font("Arial", FontWeight.BOLD, 14));

            String teamColor = TEAM_COLORS[i];
            // Style : Fond légèrement coloré et bordure assortie
            teamNameFields[i].setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                            "-fx-background-radius: 5;" +
                            "-fx-padding: 8;" +
                            "-fx-border-color: " + teamColor + ";" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 5;" +
                            "-fx-text-fill: #333333;"
            );

            teamNamesBox.getChildren().add(teamNameFields[i]);
        }
    }

    private void startGame(NavigationController navController) {
        Mode mode = ((RadioButton) modeGroup.getSelectedToggle()).getText().equals("Équipes") ? Mode.TEAM : Mode.NORMAL;
        if (picanteCheckBox.isSelected() && mode == Mode.NORMAL) mode = Mode.PICANTE;

        int playerCount = playerSpinner.getValue();
        String[] playerNames = new String[playerCount];
        for (int i = 0; i < playerCount; i++) {
            playerNames[i] = playerNameFields[i].getText().trim();
        }

        String[][] teamNamesArray = null;
        if (mode == Mode.TEAM) {
            teamNamesArray = new String[teamNameFields.length][1];
            for (int i = 0; i < teamNameFields.length; i++) {
                teamNamesArray[i][0] = teamNameFields[i].getText().trim();
            }
        }

        navController.startGame(mode, playerCount, playerNames, teamNamesArray);
    }

    // Méthodes utilitaires (createSection, createRadioButton, createStyledButton) identiques à votre code original...
    private VBox createSection(String title) {
        VBox section = new VBox(10);
        Label label = new Label(title);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.web("#FFD700"));
        section.getChildren().add(label);
        return section;
    }

    private RadioButton createRadioButton(String text, ToggleGroup group, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(group);
        rb.setSelected(selected);
        rb.setTextFill(Color.WHITE);
        return rb;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-cursor: hand;");
        btn.setPrefSize(150, 40);
        return btn;
    }
}