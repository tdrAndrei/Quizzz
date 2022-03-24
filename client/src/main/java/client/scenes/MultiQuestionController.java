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
    private AnchorPane ans1pane;

    @FXML
    private AnchorPane ans2pane;

    @FXML
    private AnchorPane ans3pane;

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
    private Label timeReduced;

    @FXML
    private GridPane grid;

    private double baseWidth;
    private double baseHeight;
    private long chosenAnswer;
    private final Border correctAnswerBorder = new Border(new BorderStroke(new Color(0, 164.0/255.0, 78.0/255.0, 0.5), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    private final Border selectedAnswerBorder = new Border(new BorderStroke(new Color(1, 0.9, 0, 0.5), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    private final Border selectedWrongAnswerBorder = new Border(new BorderStroke(new Color(148.0/255.0, 0, 17.0/255.0, 0.5), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));

    private MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;

    private boolean isMulti;

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
        timeReduced.setText("");
        activity1Label.setStyle("-fx-text-fill: #000000");
        activity2Label.setStyle("-fx-text-fill: #000000");
        activity3Label.setStyle("-fx-text-fill: #000000");
        questionLabel.setText(message.getTitle());
        questionLabel.setTextFill(Color.rgb(0, 0, 0));
    }
    public void colorIncorrectRed() {
        if (!(ans1pane.getBorder() == null) && ans1pane.getBorder().equals(selectedAnswerBorder)) {
            ans1pane.setBorder(selectedWrongAnswerBorder);
        } else if (!(ans2pane.getBorder() == null) && ans2pane.getBorder().equals(selectedAnswerBorder)) {
            ans2pane.setBorder(selectedWrongAnswerBorder);

        } else if (!(ans3pane.getBorder() == null) && ans3pane.getBorder().equals(selectedAnswerBorder)) {
            ans3pane.setBorder(selectedWrongAnswerBorder);
        }
    }
    public void showAnswer(CorrectAnswerMessage message) {
        long index = message.getCorrectAnswer();
        if (index == 0) {
            ans1pane.setBorder(correctAnswerBorder);
            activity1Label.setTextFill(Color.rgb(94, 206, 119));
        } else if (index == 1) {
            ans2pane.setBorder(correctAnswerBorder);
            activity2Label.setTextFill(Color.rgb(94, 206, 119));
        } else {
            ans3pane.setBorder(correctAnswerBorder);
            activity3Label.setTextFill(Color.rgb(94, 206, 119));
        }
        if (chosenAnswer == index) {
            questionLabel.setText("That's Right!");
            questionLabel.setTextFill(Color.rgb(94, 206, 119));
        } else {
            questionLabel.setText("Wrong!");
            questionLabel.setPrefSize(2 * questionLabel.getWidth(), 2 * questionLabel.getHeight());
            questionLabel.setTextFill(Color.rgb(201, 89, 89));
        }
        colorIncorrectRed();
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
            ans1pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);");
            ans2pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);");
            ans3pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);");
            progressBar.setProgress(1.0);

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    double maxTime = clientGameController.getMaxTime();
                    double timeLeft = clientGameController.getTimeLeft();
                    clientGameController.updateTimeLeft(0.05, timeLeft);
                    Platform.runLater(() -> clientGameController.updateProgressBar(clientGameController.getTimeLeft(), maxTime, progressBar, timeText));
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
                ans1pane.setBorder(selectedAnswerBorder);
            }
        }
        public void submit2(){
            if (!clientGameController.isDisableJokerUsage()) {
                chosenAnswer = 1;
                clientGameController.submitAnswer(1);
                clientGameController.setDisableJokerUsage(true);
                ans2pane.setBorder(selectedAnswerBorder);
            }
        }
        public void submit3(){
            if (!clientGameController.isDisableJokerUsage()) {
                chosenAnswer = 2;
                clientGameController.submitAnswer(2);
                clientGameController.setDisableJokerUsage(true);
                ans3pane.setBorder(selectedAnswerBorder);
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
            ans1pane.setBorder(Border.EMPTY);
            ans2pane.setBorder(Border.EMPTY);
            ans3pane.setBorder(Border.EMPTY);
        }

        public void useDoublePointsJoker() {
            clientGameController.doublePoint();
        }

        public void useSkipQuestionJoker() {
            if (isMulti) {
                clientGameController.timeJoker(mainCtrl.getUser().getId());
            } else {
                questionLabel.setText("You skipped this question!");
                clientGameController.skipQuestion();
            }
        }

        public void useEliminateJoker() {
            long index = clientGameController.eliminateJoker();
                if (index == 0) {
                    question1Image.setImage(new Image("/client.photos/bomb.png"));
                    activity1Label.setStyle("-fx-text-fill: #c95959");
                    activity1Label.setText("THIS ANSWER HAS BEEN ELIMINATED");
                } else if (index == 1) {
                    question2Image.setImage(new Image("/client.photos/bomb.png"));
                    activity2Label.setStyle("-fx-text-fill: #c95959");
                    activity2Label.setText("THIS ANSWER HAS BEEN ELIMINATED");
                } else {
                    question3Image.setImage(new Image("/client.photos/bomb.png"));
                    activity3Label.setStyle("-fx-text-fill: #c95959");
                    activity3Label.setText("THIS ANSWER HAS BEEN ELIMINATED");
                }
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

