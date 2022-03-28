package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EstimateQuestionController implements Initializable, QuestionScene {

    @FXML
    private ListView<Pair<String, Integer>> emojiChatView;
    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eliminateJoker;

    @FXML
    private ImageView skipQuestionJoker;

    @FXML
    private Label activity1Label;

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
    private ProgressBar progressBar;

    @FXML
    private Label timeText;

    @FXML
    private Label timeReduced;

    @FXML
    private GridPane grid;

    @FXML
    private Label newPoints;

    @FXML
    private Button submitAnswerButton;

    private final MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;
    private ChangeListener<Number> changeListener;

    private List<ImageView> jokerPics;

    @Inject
    public EstimateQuestionController(MainCtrl mainCtrl, ClientGameController clientGameController, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
        this.serverUtils = serverUtils;
    }

    @Override
    public void showQuestion(NewQuestionMessage message) {
        clientGameController.startTimer(progressBar, timeText);
        answerSlider.valueProperty().addListener(changeListener);
        answerSlider.setMin(message.getBounds().get(0));
        answerSlider.setMax(message.getBounds().get(1));
        answerSlider.setMajorTickUnit( (answerSlider.getMax() - answerSlider.getMin()) * 1/10);
        answerSlider.setValue(answerSlider.getMin());
        answerSlider.setDisable(false);
        submitAnswerButton.setDisable(false);

        questionTxt.setText(message.getTitle());
        activity1Label.setText(message.getActivities().get(0).getTitle());
        question1Image.setImage(new Image(new ByteArrayInputStream(message.getImagesBytes().get(0))));

        pointsLabel.setText(clientGameController.getScore() + " pts");
        answerLabel.setText("");
        timeReduced.setText("");
        answerLabel.setStyle("-fx-text-fill: black");

    }

    public void submitUserAnswer(){
        clientGameController.submitAnswer((long) answerSlider.getValue());
    }

    @Override
    public void showAnswer(CorrectAnswerMessage message) {
        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);

        answerLabel.setText("The answer is " + message.getCorrectAnswer());
        answerLabel.setStyle("-fx-text-fill: rgb(131,210,0);");

        clientGameController.changeScore(message.getScore(), pointsLabel, newPoints);
    }

    public void quit() {
        clientGameController.exitGame();
    }

    public void changeJoker1() {
        Joker joker = clientGameController.getRemainingJokers().get(0);
        clientGameController.useJoker(joker, this::useEliminateJoker);
    }

    public void changeJoker2() {
        Joker joker = clientGameController.getRemainingJokers().get(1);
        clientGameController.useJoker(joker, this::useDoublePointsJoker);
    }

    public void changeJoker3() {
        Joker joker = clientGameController.getRemainingJokers().get(2);
        if (joker == Joker.REDUCETIME)
            clientGameController.useJoker(joker, this::useTimeJoker);
        else
            clientGameController.useJoker(joker, this::useSkipQuestionJoker);
    }

    public void resize(double width, double height){
        grid.setPrefWidth(width);
        grid.setPrefHeight(height);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        answerLabel.setText("");
        newPoints.setText("");

        changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int value = (int) answerSlider.getValue();
                answerLabel.setText(Integer.toString(value));
            }
        };
        progressBar.setProgress(1.0);

        jokerPics = List.of(eliminateJoker, doublePointsJoker, skipQuestionJoker);
    }

    @Override
    public void showTimeReduced(String name) {
        timeReduced.setText(name + " has reduced your time!");
    }

    @Override
    public void reset() {

    }

    @Override
    public Label getPointsLabel() {
        return this.pointsLabel;
    }

    @Override
    public List<ImageView> getJokerPics() {
        return jokerPics;
    }

    @Override
    public void lockAnswer() {
        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);
        submitAnswerButton.setDisable(true);
    }

    public void useEliminateJoker(){
        answerSlider.setMin(Math.floor(answerSlider.getMin() + answerSlider.getMin() * 1/4));
        answerSlider.setMax(Math.floor(answerSlider.getMax() - answerSlider.getMin() * 1/4));
        clientGameController.eliminateJoker();
    }

    public void useDoublePointsJoker(){
        clientGameController.doublePoint();
    }

    public void useTimeJoker(){
        clientGameController.timeJoker();
    }

    public void useSkipQuestionJoker(){
        questionTxt.setText("You skipped this question!");
        clientGameController.skipQuestion();
    }

}
