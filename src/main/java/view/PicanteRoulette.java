package view;

import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;
import java.util.function.Consumer;

public class PicanteRoulette extends Stage {
    private final String[] options = {"A", "B", "C", "D", "E", "F"};
    private Label resultLabel;
    private ImageView rewardImage;
    private Button closeBtn;
    private Consumer<String> onLetterSelected;

    public PicanteRoulette(Stage owner, Consumer<String> onLetterSelected) {
        this.onLetterSelected = onLetterSelected;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("🌶️ MODE PICANTE - ROULETTE 🌶️");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50; -fx-padding: 30;");

        // Conteneur pour le titre et la flèche
        VBox titleContainer = new VBox(10);
        titleContainer.setAlignment(Pos.CENTER);

        Label title = new Label("TOURNEZ LA ROULETTE !\n");
        title.setTextFill(Color.GOLD);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        // Création de la flèche (plus grande)
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(
                0.0, 0.0,   // Point supérieur gauche
                30.0, 0.0, // Point supérieur droit
                15.0, 45.0 // Point inférieur (centre)
        );
        arrow.setFill(Color.GOLD);

        // Ajout du titre et de la flèche au conteneur
        titleContainer.getChildren().addAll(title, arrow);

        // Cercle représentant la roulette (plus grande)
        ImageView wheel = new ImageView(new Image(getClass().getResourceAsStream("/images/roulette.png")));
        wheel.setFitWidth(600);
        wheel.setFitHeight(600);
        wheel.setPreserveRatio(true);

        resultLabel = new Label("?");
        resultLabel.setTextFill(Color.WHITE);
        resultLabel.setFont(Font.font("Arial", 24));

        rewardImage = new ImageView();
        rewardImage.setFitHeight(150);
        rewardImage.setPreserveRatio(true);

        Button spinBtn = new Button("LANCER !");
        spinBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        spinBtn.setPrefSize(200, 60);
        spinBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;");

        closeBtn = new Button("RETOURNER AU JEU");
        closeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        closeBtn.setPrefSize(250, 60);
        closeBtn.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;");
        closeBtn.setDisable(true);

        spinBtn.setOnAction(e -> {
            spinBtn.setDisable(true);
            animateWheel(wheel);
        });

        closeBtn.setOnAction(e -> this.close());

        // Ajout du conteneur de titre et des autres éléments au layout principal
        layout.getChildren().addAll(titleContainer, wheel, spinBtn, resultLabel, rewardImage, closeBtn);

        // Créer une scène qui s'adapte à la taille de l'écran et maximiser
        Scene scene = new Scene(layout);
        setScene(scene);
        setMaximized(true);
    }


    private void animateWheel(ImageView wheel) {
        RotateTransition rt = new RotateTransition(Duration.seconds(2), wheel);
        Random rand = new Random();
        int angle = 1080 + rand.nextInt(360); // 3 tours complets + un angle aléatoire
        rt.setByAngle(angle);

        rt.setOnFinished(e -> {
            // Normalise l'angle final entre 0 et 359°
            double finalAngle = angle % 360;
            // Calcule le secteur en tenant compte de la disposition
            String win = "G";

            System.out.println("Angle final (degrés) : " + finalAngle + "avec lettre " + win);
            if (finalAngle >= 0 && finalAngle < 60) {

                System.out.println("Angle final (degrés) : " + finalAngle + "ON RENTRE ICI F");
                win = "F"; // Secteur F : de 300° à 360° et de 0° à 60°
            } else if (finalAngle >= 60 && finalAngle < 120) {
                System.out.println("Angle final (degrés) : " + finalAngle + "ON RENTRE ICI E");
                win = "E"; // Secteur E : de 60° à 120°
            } else if (finalAngle >= 120 && finalAngle < 180) {
                System.out.println("Angle final (degrés) : " + finalAngle + "ON RENTRE ICI D");
                win = "D"; // Secteur D : de 120° à 180°
            } else if (finalAngle >= 180 && finalAngle < 240) {
                System.out.println("Angle final (degrés) : " + finalAngle + "ON RENTRE ICI C");
                win = "C"; // Secteur C : de 180° à 240°
            } else if (finalAngle >= 240 && finalAngle < 300) {
                System.out.println("Angle final (degrés) : " + finalAngle + "ON RENTRE ICI B");
                win = "B"; // Secteur B : de 240° à 300°
            } else {
                System.out.println("Angle final (degrés) : " + finalAngle + "ON RENTRE ICI A");
                win = "A"; // Secteur A : de 300° à 360° (mais déjà couvert par la première condition)
            }

            System.out.println("Angle final (degrés) : " + finalAngle + "avec lettre " + win);
            showReward(win);
        });
        rt.play();
    }


    private void showReward(String letter) {
        resultLabel.setText("Résultat : " + letter);

        if (onLetterSelected != null) {
            onLetterSelected.accept(letter);
        }

        // Exemple de logique de gain
        String text;
        String imgPath = null;

        switch(letter) {
            case "A": text = "JACKPOT !!! Vous gagnez 2 points ! Excellent semestre."; imgPath = "/images/letter-A.png"; break;
            case "B": text = "Vous gagnez 1 point ! Vous avez l'autorisation de partir en FISE INFORMATIQUE."; imgPath = "/images/letter-B.png"; break;
            case "C": text = "Vous gagnez 1 point ! Vous n'avez pas l'autorisation de déposer une demande d'études à l'étranger. Efforts à poursuivre."; imgPath = "/images/letter-C.png"; break;
            case "D": text = "Vous gagnez 1 point ! Attention aux seuils de crédits. Le TC5 n'est pas un droit acquis d'avance."; imgPath = "/images/letter-D.png"; break;
            case "E": text = "Vous ne gagnez rien ! Très mauvais semestre. Prenez vos précautions en vue d'une réorientation."; imgPath = "/images/letter-E.png"; break;
            case "F": text = "Vous perdez 1 point ! Convoqué devant le 2ème jury de suivi."; imgPath = "/images/letter-F.png"; break;

            default: text = "Error"; break;
        }

        resultLabel.setText(text);
        resultLabel.setWrapText(true); // Active le retour à la ligne
        resultLabel.setMaxWidth(1200);  // Largeur maximale pour le retour à la ligne
        resultLabel.setPrefWidth(1200); // Largeur préférée pour éviter que le label soit trop petit
        try {
            rewardImage.setImage(new Image(getClass().getResourceAsStream(imgPath)));
        } catch (Exception e) {}

        closeBtn.setDisable(false);
    }
}