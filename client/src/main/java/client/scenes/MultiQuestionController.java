package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import commons.Messages.CorrectAnswerMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import commons.Messages.NewQuestionMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MultiQuestionController implements Initializable {

    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eliminateJoker;

    @FXML
    private ImageView skipQuestionJoker;

    @FXML
    private HBox jokerContainer;

    @FXML
    private GridPane Answer1;

    @FXML
    private GridPane Answer2;

    @FXML
    private GridPane Answer3;

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
    private Label activity1Label;

    @FXML
    private Label activity2Label;

    @FXML
    private Label activity3Label;

    @FXML
    private Label questionLabel;

    @FXML
    private Label timeText;

    @FXML
    private Label pointsLabel;

    @FXML
    private GridPane grid;

    private double baseWidth;
    private double baseHeight;
    private long chosenAnswer;
    private Border correctAnswerBorder = new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));

    private MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;

    @Inject
    public MultiQuestionController(MainCtrl mainCtrl, ClientGameController clientGameController, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
        this.serverUtils =serverUtils;
    }

    public void quit() throws IOException {
        clientGameController.exitGame();
        mainCtrl.showMainMenu();
    }

    public void changeJoker1() {
        if (!clientGameController.isDisableJokerUsage() && !clientGameController.isEliminateJokerUsed()) {
            eliminateJoker.setImage(clientGameController.getUsedJoker());
            clientGameController.setEliminateJokerUsed(true);
            useEliminateJoker();
        }
    }

    public void changeJoker2() {
        if (!clientGameController.isDisableJokerUsage() && !clientGameController.isDoublePointsJokerUsed()) {
            doublePointsJoker.setImage(clientGameController.getUsedJoker());
            clientGameController.setDoublePointsJokerUsed(true);
            useDoublePointsJoker();
        }
    }

    public void changeJoker3() {
        if (!clientGameController.isDisableJokerUsage() && !clientGameController.isSkipQuestionJokerUsed()) {
            skipQuestionJoker.setImage(clientGameController.getUsedJoker());
            clientGameController.setSkipQuestionJokerUsed(true);
            useSkipQuestionJoker();
        }
    }

    public void showQuestion(NewQuestionMessage message) {
        questionLabel.setText(message.getTitle());
        questionLabel.setTextFill(Color.rgb(0, 0, 0));
    }

    public void showAnswer(CorrectAnswerMessage message) {
        long index = message.getCorrectAnswer();
        String answer = "";
        if (index == 0) {
            Answer1.setBorder(correctAnswerBorder);
            activity1Label.setTextFill(Color.rgb(0, 225, 0));
        } else if (index == 1) {
            Answer2.setBorder(correctAnswerBorder);
            activity2Label.setTextFill(Color.rgb(0, 225, 0));
        } else {
            Answer3.setBorder(correctAnswerBorder);
            activity3Label.setTextFill(Color.rgb(0, 225, 0));
        }
        if (chosenAnswer == index) {
            questionLabel.setText("That's Right!");
            questionLabel.setTextFill(Color.rgb(0, 225, 0));
        } else {
            questionLabel.setText("Wrong!");
            questionLabel.setPrefSize(2 * questionLabel.getWidth(), 2 * questionLabel.getHeight());
            questionLabel.setTextFill(Color.rgb(219, 43, 43));
        }
    }

        @Override
        public void initialize (URL location, ResourceBundle resources){

            baseWidth = grid.getPrefWidth();
            baseHeight = grid.getPrefHeight();

            grid.widthProperty().addListener(e -> {
                resize(grid.getWidth(), grid.getHeight());
            });
            grid.heightProperty().addListener(e -> {
                resize(grid.getWidth(), grid.getHeight());
            });


            for (Node node : grid.lookupAll(".highlightable")) {
                node.setOnMouseEntered(e -> {
                    new Timeline(new KeyFrame(Duration.seconds(0), new KeyValue(node.scaleXProperty(), 1), new KeyValue(node.scaleYProperty(), 1)),
                            new KeyFrame(Duration.seconds(.2), new KeyValue(node.scaleXProperty(), 1.05), new KeyValue(node.scaleYProperty(), 1.05))).play();
                });
                node.setOnMouseExited(e -> {
                    node.setScaleX(1);
                    node.setScaleY(1);
                });
            }

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

        public void resize ( double width, double height){
            if (width < grid.getMinWidth() || height < grid.getMinHeight())
                return;

            double ratioW = width / baseWidth;
            double ratioH = height / baseHeight;

            exitButton.setPrefWidth(Math.min(1.5, ratioW) * exitButton.getMinWidth());
            exitButton.setPrefHeight(Math.min(1.5, ratioH) * exitButton.getMinHeight());
            exitButton.setStyle("-fx-font-size: " + Math.min(ratioW * 19, 21) + "px");

            for (Node node : grid.lookupAll(".questionBackground")) {
                AnchorPane pane = (AnchorPane) node;
                pane.setPrefWidth(Math.min(2, ratioW) * pane.getMinWidth());
                pane.setPrefHeight(Math.min(2, ratioH) * pane.getMinHeight());

                GridPane questionCardGrid = (GridPane) pane.getChildren().get(0);
                for (Node e : questionCardGrid.getChildren()) {
                    if (e instanceof ImageView) {
                        ImageView img = (ImageView) e;
                        img.setFitHeight(0.8 * 0.9 * pane.getPrefHeight());
                        img.setFitWidth(0.9 * pane.getPrefWidth());
                    }
                }
            }

            for (Node node : jokerContainer.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView img = (ImageView) node;
                    img.setFitHeight(Math.min(ratioH * 150, 225));
                    img.setFitWidth(Math.min(ratioW * 94, 141));
                }
            }
        }

        public void submit1(){
            if (!clientGameController.isDisableJokerUsage()) {
                chosenAnswer = 0;
                clientGameController.submitAnswer(0);
                clientGameController.setDisableJokerUsage(true);
            }
        }
        public void submit2(){
            if (!clientGameController.isDisableJokerUsage()) {
                chosenAnswer = 1;
                clientGameController.submitAnswer(1);
                clientGameController.setDisableJokerUsage(true);
            }
        }
        public void submit3(){
            if (!clientGameController.isDisableJokerUsage()) {
                chosenAnswer = 2;
                clientGameController.submitAnswer(2);
                clientGameController.setDisableJokerUsage(true);
            }
        }

        public void changeScore(int score) {
            pointsLabel.setText(score + " Pts");
        }

        public void setQuestions(List<Activity> activities, List<byte[]> imageByteList) {
            Activity firstActivity = activities.get(0);
            question1Image.setImage(new Image(new ByteArrayInputStream(imageByteList.get(0))));
            activity1Label.setText(firstActivity.getTitle());
            Activity secondActivity = activities.get(1);
            question2Image.setImage(new Image(new ByteArrayInputStream(imageByteList.get(1))));
            activity2Label.setText(secondActivity.getTitle());
            Activity thirdActivity = activities.get(2);
            question3Image.setImage(new Image(new ByteArrayInputStream(imageByteList.get(2))));
            activity3Label.setText(thirdActivity.getTitle());
            activity1Label.setTextFill(Color.rgb(0 , 0 , 0));
            activity2Label.setTextFill(Color.rgb(0 , 0 , 0));
            activity3Label.setTextFill(Color.rgb(0 , 0 , 0));
            Answer1.setBorder(Border.EMPTY);
            Answer2.setBorder(Border.EMPTY);
            Answer3.setBorder(Border.EMPTY);
        }

        public void useDoublePointsJoker() {
            clientGameController.doublePoint();
        }

        public void useSkipQuestionJoker() {
            questionLabel.setText("You skipped this question!");
            clientGameController.skipQuestion();
        }

        public void useEliminateJoker() {
            long index = clientGameController.eliminateJoker();
                if (index == 0) {
                    question1Image.setImage(new Image("/client.photos/bomb.png"));
                    activity1Label.setText("This answer has been eliminated");
                } else if (index == 1) {
                    question2Image.setImage(new Image("/client.photos/bomb.png"));
                    activity2Label.setText("This answer has been eliminated");
                } else {
                    question3Image.setImage(new Image("/client.photos/bomb.png"));
                    activity3Label.setText("This answer has been eliminated");
                }
        }

    public void reset(){
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

}

