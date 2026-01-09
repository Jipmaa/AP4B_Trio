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
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Création de la flèche
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(
                0.0, 0.0,   // Point supérieur gauche
                20.0, 0.0, // Point supérieur droit
                10.0, 30.0 // Point inférieur (centre)
        );
        arrow.setFill(Color.GOLD);

        // Ajout du titre et de la flèche au conteneur
        titleContainer.getChildren().addAll(title, arrow);

        // Cercle représentant la roulette
        ImageView wheel = new ImageView(new Image(getClass().getResourceAsStream("/images/roulette.png")));
        wheel.setFitWidth(500);
        wheel.setFitHeight(500);
        wheel.setPreserveRatio(true);

        resultLabel = new Label("?");
        resultLabel.setTextFill(Color.WHITE);
        resultLabel.setFont(Font.font("Arial", 18));

        rewardImage = new ImageView();
        rewardImage.setFitHeight(100);
        rewardImage.setPreserveRatio(true);

        Button spinBtn = new Button("LANCER !");
        closeBtn = new Button("RETOURNER AU JEU");
        closeBtn.setDisable(true);

        spinBtn.setOnAction(e -> {
            spinBtn.setDisable(true);
            animateWheel(wheel);
        });

        closeBtn.setOnAction(e -> this.close());

        // Ajout du conteneur de titre et des autres éléments au layout principal
        layout.getChildren().addAll(titleContainer, wheel, spinBtn, resultLabel, rewardImage, closeBtn);
        setScene(new Scene(layout, 400, 500));
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
            case "A": text = "JACKPOT !!! Vous gagnez 1 point bonus !\nExcellent semestre."; imgPath = "/images/letter-A.png"; break;
            case "B": text = "Vous gagnez... un incroyable rien !\nVous avez l'autorisation de partir en FISE INFORMATIQUE."; imgPath = "/images/letter-B.png"; break;
            case "C": text = "Vous gagnez... un incroyable rien !\nVous n'avez pas l'autorisation de déposer une demande d'études à l'étranger. Efforts à poursuivre."; imgPath = "/images/letter-C.png"; break;
            case "D": text = "Vous gagnez... un incroyable rien !\nAttention aux seuils de crédits. Le TC5 n'est pas un droit acquis d'avance"; imgPath = "/images/letter-D.png"; break;
            case "E": text = "Vous gagnez... -1 point ?!\nTrès mauvais semestre. Prenez vos précautions en vue d'une réorientation"; imgPath = "/images/letter-E.png"; break;
            case "F": text = "Vous gagnez... -2 points ?!\nConvoqué devant le 2ème jury de suivi"; imgPath = "/images/letter-F.png"; break;

            default: text = "Error"; break;
        }

        resultLabel.setText(text);
        try {
            rewardImage.setImage(new Image(getClass().getResourceAsStream(imgPath)));
        } catch (Exception e) {}

        closeBtn.setDisable(false);
    }
}