package view;

import controller.NavigationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
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

    public GameSetupView(NavigationController navController) {
        // Background
        setBackground(new Background(new BackgroundFill(
                Color.web("#585858"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
//        try {
//            Image bgImg = new Image(getClass().getResourceAsStream("/images/franck.jpg"));
//            BackgroundImage background = new BackgroundImage(
//                    bgImg,
//                    BackgroundRepeat.NO_REPEAT,
//                    BackgroundRepeat.NO_REPEAT,
//                    BackgroundPosition.CENTER,
//                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
//            );
//            setBackground(new Background(background));
//        } catch (Exception e) {
//            setBackground(new Background(new BackgroundFill(
//                    Color.rgb(20, 25, 35), CornerRadii.EMPTY, Insets.EMPTY)));
//        }

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Container principal
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setMaxWidth(700);

        // Panneau de configuration avec style
        VBox configPanel = new VBox(25);
        configPanel.setAlignment(Pos.CENTER);
        configPanel.setPadding(new Insets(40));
        configPanel.setStyle(
                "-fx-background-color: rgba(30, 35, 45, 0.95);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 20;"
        );
        configPanel.setEffect(new DropShadow(20, Color.BLACK));

        // Titre
        Label title = new Label("CONFIGURATION DE LA PARTIE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#FFD700"));

        // Mode de jeu
        VBox modeBox = createSection("Mode de Jeu");
        modeGroup = new ToggleGroup();

        RadioButton normalMode = createRadioButton("Solo", modeGroup, true);
        RadioButton teamMode = createRadioButton("Équipes", modeGroup, false);

        modeBox.getChildren().addAll(normalMode, teamMode);

        // Option Picante
        picanteCheckBox = new CheckBox("Mode Picante (2 points par trio)");
        picanteCheckBox.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        picanteCheckBox.setTextFill(Color.web("#FF6B6B"));
        picanteCheckBox.setStyle("-fx-cursor: hand;");

        // Nombre de joueurs - MINIMUM 3 JOUEURS
        VBox playerCountBox = createSection("Nombre de Joueurs");
        HBox spinnerBox = new HBox(15);
        spinnerBox.setAlignment(Pos.CENTER);

        Label playerLabel = new Label("Joueurs:");
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerLabel.setTextFill(Color.WHITE);

        // MODIFIÉ: Spinner de 3 à 6 joueurs (minimum 3)
        playerSpinner = new Spinner<>(3, 6, 3);
        playerSpinner.setEditable(false);
        playerSpinner.setPrefWidth(80);
        playerSpinner.setStyle("-fx-font-size: 16px;");

        spinnerBox.getChildren().addAll(playerLabel, playerSpinner);
        playerCountBox.getChildren().add(spinnerBox);

        // Noms des joueurs
        playerNamesBox = createSection("Noms des Joueurs");
        updatePlayerNameFields(3); // Commencer avec 3 joueurs

        // Noms des équipes (caché par défaut)
        teamNamesBox = createSection("Noms des Équipes");
        teamNamesBox.setVisible(false);
        teamNamesBox.setManaged(false);
        updateTeamNameFields();

        // Listeners
        playerSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            updatePlayerNameFields(newVal);
        });

        teamMode.selectedProperty().addListener((obs, oldVal, newVal) -> {
            teamNamesBox.setVisible(newVal);
            teamNamesBox.setManaged(newVal);
            picanteCheckBox.setDisable(newVal);
            if (newVal) {
                picanteCheckBox.setSelected(false);
            }
        });

        // Boutons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button startButton = createStyledButton("COMMENCER", "#4CAF50");
        Button backButton = createStyledButton("RETOUR", "#757575");

        startButton.setOnAction(e -> startGame(navController));
        backButton.setOnAction(e -> navController.showMainMenu());

        buttonBox.getChildren().addAll(backButton, startButton);

        configPanel.getChildren().addAll(
                title,
                modeBox,
                picanteCheckBox,
                playerCountBox,
                playerNamesBox,
                teamNamesBox,
                buttonBox
        );

        mainContainer.getChildren().add(configPanel);
        getChildren().addAll(overlay, mainContainer);
    }

    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER_LEFT);

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
        rb.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        rb.setTextFill(Color.WHITE);
        rb.setStyle("-fx-cursor: hand;");
        return rb;
    }

    private void updatePlayerNameFields(int count) {
        playerNamesBox.getChildren().removeIf(node -> node instanceof TextField);
        playerNameFields = new TextField[count];

        for (int i = 0; i < count; i++) {
            TextField tf = new TextField("Joueur " + (i + 1));
            tf.setFont(Font.font("Arial", 14));
            tf.setPrefWidth(300);
            tf.setStyle("-fx-background-radius: 5; -fx-padding: 8;");
            playerNameFields[i] = tf;
            playerNamesBox.getChildren().add(tf);
        }
    }

    private void updateTeamNameFields() {
        teamNamesBox.getChildren().removeIf(node -> node instanceof TextField);
        teamNameFields = new TextField[2];

        for (int i = 0; i < 2; i++) {
            TextField tf = new TextField("Équipe " + (i + 1));
            tf.setFont(Font.font("Arial", 14));
            tf.setPrefWidth(300);
            tf.setStyle("-fx-background-radius: 5; -fx-padding: 8;");
            teamNameFields[i] = tf;
            teamNamesBox.getChildren().add(tf);
        }
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setPrefSize(200, 45);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setScaleX(1.05));
        button.setOnMouseExited(e -> button.setScaleX(1.0));

        return button;
    }

    private void startGame(NavigationController navController) {
        RadioButton selectedModeRadio = (RadioButton) modeGroup.getSelectedToggle();
        Mode mode = selectedModeRadio.getText().equals("Équipes") ? Mode.TEAM : Mode.NORMAL;
        boolean isPicante = picanteCheckBox.isSelected();

        if (isPicante && mode == Mode.NORMAL) {
            mode = Mode.PICANTE;
        }

        int playerCount = playerSpinner.getValue();

        String[] playerNames = new String[playerCount];
        for (int i = 0; i < playerCount; i++) {
            playerNames[i] = playerNameFields[i].getText().trim();
            if (playerNames[i].isEmpty()) {
                playerNames[i] = "Joueur " + (i + 1);
            }
        }

        String[][] teamNames = new String[2][1];
        if (mode == Mode.TEAM) {
            teamNames[0][0] = teamNameFields[0].getText().trim();
            teamNames[1][0] = teamNameFields[1].getText().trim();
            if (teamNames[0][0].isEmpty()) teamNames[0][0] = "Équipe 1";
            if (teamNames[1][0].isEmpty()) teamNames[1][0] = "Équipe 2";
        }

        navController.startGame(mode, playerCount, playerNames, teamNames);
    }
}