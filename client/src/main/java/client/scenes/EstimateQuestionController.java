package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class EstimateQuestionController implements Initializable {

    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eliminateJoker;

    @FXML
    private ImageView skipQuestionJoker;

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
    private ProgressBar progressBar;

    @FXML
    private Label timeText;

    @FXML
    private Label timeReduced;

    @FXML
    private GridPane grid;

    private final MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;
    private ChangeListener<Number> changeListener;

    private boolean isMulti;

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
        answerSlider.setMajorTickUnit( (answerSlider.getMax() - answerSlider.getMin()) * 1/10);
        answerSlider.setValue(answerSlider.getMin());
        answerSlider.setDisable(false);

        questionTxt.setText(message.getTitle());
        activity1Label.setText(message.getActivities().get(0).getTitle());
        question1Image.setImage(new Image(new ByteArrayInputStream(message.getImagesBytes().get(0))));

        answerLabel.setText("");
        timeReduced.setText("");
        answerLabel.setStyle("-fx-text-fill: black");

    }

    public void submitUserAnswer(){

        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);
        clientGameController.setDisableJokerUsage(true);

        clientGameController.submitAnswer((long) answerSlider.getValue());

    }

    public void showAnswer(CorrectAnswerMessage message) {

        answerSlider.valueProperty().removeListener(changeListener);
        answerSlider.setDisable(true);

        answerLabel.setText("The answer is " + message.getCorrectAnswer());
        answerLabel.setStyle("-fx-text-fill: rgb(131,210,0)");

        pointsLabel.setText(Integer.toString(message.getScore()) + " Pts");

    }

    public void processEmoji(Event event) {
        ImageView emoji = (ImageView) event.getSource();
        int emojiId = Integer.parseInt(emoji.getId().replace("e", ""));
        clientGameController.sendEmoji(emojiId);
    }

    public void quit() throws IOException {
        clientGameController.exitGame();
        mainCtrl.showMainMenu();
    }

    //(eliminate one answer joker) -> minimizes the range for the slider
    public void changeJoker1() {

        if (!clientGameController.isDisableJokerUsage() && !clientGameController.isEliminateJokerUsed()) {
            eliminateJoker.setImage(clientGameController.getUsedJoker());
            clientGameController.setEliminateJokerUsed(true);
            answerSlider.setMin(Math.floor(answerSlider.getMin() + answerSlider.getMin() * 1/4));
            answerSlider.setMax(Math.floor(answerSlider.getMax() - answerSlider.getMin() * 1/4));
        }

    }

    //doublePointsJoker
    public void changeJoker2() {

        if (!clientGameController.isDisableJokerUsage() && !clientGameController.isDoublePointsJokerUsed()) {
            doublePointsJoker.setImage(clientGameController.getUsedJoker());
            clientGameController.setDoublePointsJokerUsed(true);
            clientGameController.doublePoint();
        }

    }

    //skip question joker
    public void changeJoker3() {
        if (!clientGameController.isDisableJokerUsage() && !clientGameController.isSkipQuestionJokerUsed()) {
            if (isMulti) {
                clientGameController.timeJoker(mainCtrl.getUser().getId());
            } else {
                questionTxt.setText("You skipped this question!");
                clientGameController.skipQuestion();
            }
            skipQuestionJoker.setImage(clientGameController.getUsedJoker());
            clientGameController.setSkipQuestionJokerUsed(true);
        }
        
    }

    public void resize(double width, double height){
        grid.setPrefWidth(width);
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

        progressBar.setProgress(1.0);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double maxTime = clientGameController.getMaxTime();
                double timeLeft = clientGameController.getTimeLeft();
                clientGameController.updateTimeLeft(0.05, timeLeft);
                Platform.runLater(() -> clientGameController.updateProgressBar(timeLeft, maxTime, progressBar, timeText));
            }
        }, 0, 100);

    }

    public void showTimeReduced(String name) {
        timeReduced.setText(name + " has reduced your time!");
    }

    public void resetSolo() {
        eliminateJoker.setImage(new Image("/client.photos/jokerOneAnswer.png"));
        doublePointsJoker.setImage(new Image("/client.photos/doubleJoker.png"));
        skipQuestionJoker.setImage(new Image("/client.photos/skipJoker.png"));
        pointsLabel.setText("0 Pts");
    }

    public void resetMulti() {
        eliminateJoker.setImage(new Image("/client.photos/jokerOneAnswer.png"));
        doublePointsJoker.setImage(new Image("/client.photos/doubleJoker.png"));
        skipQuestionJoker.setImage(new Image("/client.photos/timeJoker.png"));
        pointsLabel.setText("0 Pts");
    }

    public void setJokersPic() {

        if (clientGameController.isEliminateJokerUsed())
            eliminateJoker.setImage(clientGameController.getUsedJoker());
        if (clientGameController.isDoublePointsJokerUsed())
            doublePointsJoker.setImage(clientGameController.getUsedJoker());
        if (clientGameController.isSkipQuestionJokerUsed())
            skipQuestionJoker.setImage(clientGameController.getUsedJoker());

    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }

    public boolean isMulti() {
        return isMulti;
    }

}
