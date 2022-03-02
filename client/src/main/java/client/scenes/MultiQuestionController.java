package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MultiQuestionController {

    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eliminateJoker;

    @FXML
    private Button exitButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView question1Image;

    @FXML
    private ImageView question3Image;

    @FXML
    private ImageView question2Image;

    @FXML
    private Label questionLabel;

    @FXML
    private Label timeButton;

    @FXML
    private ImageView timeJoker;

    private MainCtrl mainCtrl;
    // ImageView imageView;
    Image image = new Image("/client.photos/usedJoker.png");

    @Inject
    public MultiQuestionController(MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
    }


    public void quit() throws IOException {
        mainCtrl.quit();
    }
    public void changeJoker1() {
        eliminateJoker.setImage(image);
    }
    public void changeJoker2() {
        doublePointsJoker.setImage(image);
    }
    public void changeJoker3() {
        timeJoker.setImage(image);
    }



}

