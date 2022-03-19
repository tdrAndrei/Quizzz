package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EstimateQuestionController implements Initializable {

    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eliminateJoker;

    @FXML
    private ImageView timeJoker;

    @FXML
    private Label activity1Label;

    @FXML
    private Button exitButton;

    @FXML
    private Label questionTxt;

    @FXML
    private Slider answerSlider;

    @FXML
    private Label answerLabel;

    @FXML
    private Label pointsLabel;

    @FXML
    private GridPane grid;

    private final MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;
    private ChangeListener<Number> changeListener;
    private boolean jokerAvailable;
    private boolean usedDbPointsJoker;

    Image image = new Image("/client.photos/usedJoker.png");

    @Inject
    public EstimateQuestionController(MainCtrl mainCtrl, ClientGameController clientGameController, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
        this.serverUtils = serverUtils;
    }

    public void showQuestion(NewQuestionMessage message) {

        answerSlider.valueProperty().addListener(changeListener);
        answerSlider.setMin(message.getBounds().getLeft());
        answerSlider.setMax(message.getBounds().getRight());
        answerSlider.setValue(answerSlider.getMin());
        answerSlider.setDisable(false);

        jokerAvailable = true;
        questionTxt.setText(message.getTitle());
        activity1Label.setText(message.getActivities().get(0).getTitle());

        answerLabel.setText("");
        answerLabel.setStyle("-fx-text-fill: black");

    }

    public void submitUserAnswer(){

        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);
        serverUtils.submitAnswer(clientGameController.getGameId(), mainCtrl.getUser().getId(), (long) answerSlider.getValue());

    }

    public void showAnswer(CorrectAnswerMessage message) {

        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);
        answerLabel.setText("The answer is " + message.getCorrectAnswer());
        answerLabel.setStyle("-fx-text-fill: rgb(131,210,0)");
        pointsLabel.setText(Integer.toString(message.getScore()) + " Pts");
        jokerAvailable = false;

    }

    public void quit() throws IOException {
        clientGameController.exitGame();
        mainCtrl.showMainMenu();
    }

    //(eliminate one answer joker) -> minimizes the range for the slider
    public void changeJoker1() {

        if (jokerAvailable) {
            eliminateJoker.setImage(image);
            answerSlider.setMin(answerSlider.getMin() + 1/5 * answerSlider.getMin());
            answerSlider.setMax(answerSlider.getMax() - 1/5 * answerSlider.getMin());
        }

    }

    //doublePointsJoker
    public void changeJoker2() {

        if (jokerAvailable) {
            doublePointsJoker.setImage(image);
            serverUtils.useDoublePointsJoker(clientGameController.getGameId(), mainCtrl.getUser().getId());
        }

    }

    //skip question joker
    public void changeJoker3() {

        if (jokerAvailable) {
            timeJoker.setImage(image);
            serverUtils.useNewQuestionJoker(clientGameController.getGameId());
        }
        
    }

    public void resizeWidth(double width){
        grid.setPrefWidth(width);
    }

    public void resizeHeight(double height){
        grid.setPrefHeight(height);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        answerLabel.setText("");

        changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int value = (int) answerSlider.getValue();
                answerLabel.setText(Integer.toString(value));
            }
        };

    }

}
