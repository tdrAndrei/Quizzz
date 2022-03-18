package client.scenes;

import com.google.inject.Inject;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class EstimateQuestionController {

    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eliminateJoker;

    @FXML
    private ImageView timeJoker;

    @FXML
    private Button exitButton;

    @FXML
    private Label questionTxt;

    @FXML
    private Label scoreTxt;

    @FXML
    private Slider answerSlider;

    @FXML
    private Label maxAnswer;

    @FXML
    private Label minAnswer;

    @FXML
    private GridPane grid;

    private final MainCtrl mainCtrl;
    private final ClientGameController clientGameController;

    Image image = new Image("/client.photos/usedJoker.png");

    @Inject
    public EstimateQuestionController(MainCtrl mainCtrl, ClientGameController clientGameController){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
    }

    public void showQuestion(NewQuestionMessage message) {
        questionTxt.setText(message.getTitle());
    }

    public void showAnswer(CorrectAnswerMessage message) {
        questionTxt.setText("The answer is "+message.getCorrectAnswer());
    }

    public void quit() throws IOException {
        clientGameController.exitGame();
        mainCtrl.showMainMenu();
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

    public void resizeWidth(double width){
        grid.setPrefWidth(width);
    }

    public void resizeHeight(double height){
        grid.setPrefHeight(height);
    }

}
