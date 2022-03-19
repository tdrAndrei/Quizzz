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
    private ImageView question1Image;

    @FXML
    private Button submitAnswerButton;

    @FXML
    private GridPane grid;

    private final MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;
    private ChangeListener<Number> changeListener;

    @Inject
    public EstimateQuestionController(MainCtrl mainCtrl, ClientGameController clientGameController, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
        this.serverUtils = serverUtils;
    }

    public void showQuestion(NewQuestionMessage message) {

        answerSlider.valueProperty().addListener(changeListener);
        answerSlider.setMin(message.getBounds().get(0));
        answerSlider.setMax(message.getBounds().get(1));
        answerSlider.setValue(answerSlider.getMin());
        answerSlider.setDisable(false);

        questionTxt.setText(message.getTitle());
        activity1Label.setText(message.getActivities().get(0).getTitle());
        question1Image.setImage(new Image(message.getActivities().get(0).getImage_path()));

        answerLabel.setText("");
        answerLabel.setStyle("-fx-text-fill: black");

    }

    public void submitUserAnswer(){

        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);

        clientGameController.submitAnswer((long) answerSlider.getValue());

    }

    public void showAnswer(CorrectAnswerMessage message) {

        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);

        answerLabel.setText("The answer is " + message.getCorrectAnswer());
        answerLabel.setStyle("-fx-text-fill: rgb(131,210,0)");

        pointsLabel.setText(Integer.toString(message.getScore()) + " Pts");

    }

    public void quit() throws IOException {
        clientGameController.exitGame();
        mainCtrl.showMainMenu();
    }

    //(eliminate one answer joker) -> minimizes the range for the slider
    public void changeJoker1() {

        if (!clientGameController.isDisableJokerUsage()) {
            eliminateJoker.setImage(clientGameController.getUsedJoker());
            answerSlider.setMin(answerSlider.getMin() + 1/5 * answerSlider.getMin());
            answerSlider.setMax(answerSlider.getMax() - 1/5 * answerSlider.getMin());
        }

    }

    //doublePointsJoker
    public void changeJoker2() {

        if (!clientGameController.isDisableJokerUsage()) {
            doublePointsJoker.setImage(clientGameController.getUsedJoker());
            clientGameController.doublePoint();
        }

    }

    //skip question joker
    public void changeJoker3() {

        if (!clientGameController.isDisableJokerUsage()) {
            timeJoker.setImage(clientGameController.getUsedJoker());
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

    public void reset(){
        eliminateJoker.setImage(new Image("/client.photos/jokerOneAnswer.png"));
        doublePointsJoker.setImage(new Image("/client.photos/doubleJoker.png"));
        timeJoker.setImage(new Image("/client.photos/timeJoker.png"));
        pointsLabel.setText("0");
    }

}
