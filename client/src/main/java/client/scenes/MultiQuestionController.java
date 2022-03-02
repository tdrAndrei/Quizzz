package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MultiQuestionController {

    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eleminateJoker;

    @FXML
    private Button exitButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView quesiton1Image;

    @FXML
    private ImageView quesiton3Image;

    @FXML
    private ImageView question2Image;

    @FXML
    private Label questionLabel;

    @FXML
    private Label timeButton;

    @FXML
    private ImageView timeJoker;

   // ImageView imageView;
    Image image = new Image("/client.photos/usedJoker.png");

    public void changeJoker1() {
        eleminateJoker.setImage(image);
    }
    public void changeJoker2() {
        doublePointsJoker.setImage(image);
    }
    public void changeJoker3() {
        timeJoker.setImage(image);
    }



}

