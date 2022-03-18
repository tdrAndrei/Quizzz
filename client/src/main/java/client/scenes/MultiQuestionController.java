package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import commons.Messages.CorrectAnswerMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import commons.Messages.NewQuestionMessage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MultiQuestionController implements Initializable {

    @FXML
    private ImageView doublePointsJoker;

    @FXML
    private ImageView eliminateJoker;

    @FXML
    private ImageView timeJoker;

    @FXML
    private HBox jokerContainer;

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
    private Label timer;

    @FXML
    private Label pointsLabel;

    @FXML
    private GridPane grid;


    private double baseWidth;
    private double baseHeight;
    private boolean timeJokerUsed = false;
    private boolean eliminateJokerUsed = false;
    private boolean doublePointsJokerUsed = false;
    private long chosenAnswer;
    private boolean disable = false;

    private MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;

    // ImageView imageView;
    Image image = new Image("/client.photos/usedJoker.png");

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
        if (!disable && !eliminateJokerUsed) {
            eliminateJoker.setImage(image);
            eliminateJokerUsed = true;
            useEliminateJoker();
        }
    }
    public void changeJoker2() {
        if (!disable && !doublePointsJokerUsed) {
            doublePointsJoker.setImage(image);
            doublePointsJokerUsed = true;
            useDoublePointsJoker();

        }
    }
    public void changeJoker3() {
        if (!disable && !timeJokerUsed) {
            timeJoker.setImage(image);
            timeJokerUsed = true;
        }
    }

    public void showEstimate() {
        mainCtrl.showEstimate();
    }

    public void showQuestion(NewQuestionMessage message) {
        questionLabel.setText(message.getTitle());
    }

    public void showAnswer(CorrectAnswerMessage message) {
        long index = message.getCorrectAnswer();
        String answer = "";
        if (index == 0) {
            answer = activity1Label.getText();
        } else if (index == 1) {
            answer = activity2Label.getText();
        } else {
            answer = activity3Label.getText();
        }
        if (chosenAnswer == index) {
            questionLabel.setText("That's Right!");
        } else {
            questionLabel.setText("Unfortunately wrong answer\nThe answer is: " + answer);
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
            if (!disable) {
                chosenAnswer = 0;
                clientGameController.submitAnswer(0);
                disable = true;

            }
        }
        public void submit2(){
            if (!disable) {
                chosenAnswer = 1;
                clientGameController.submitAnswer(1);
                disable = true;
            }
        }
        public void submit3(){
            if (!disable) {
                chosenAnswer = 2;
                clientGameController.submitAnswer(2);
                disable = true;
            }
        }

        public void changeScore(int score) {
            pointsLabel.setText(score + "Pts");
        }
        public void enable() {
            disable = false;
        }
        public void setQuestions(List<Activity> activities) {
            Activity firstActivity = activities.get(0);
            question1Image.setImage(new Image(firstActivity.getImage_path()));
            activity1Label.setText(firstActivity.getTitle());
            Activity secondActivity = activities.get(1);
            question2Image.setImage(new Image(secondActivity.getImage_path()));
            activity2Label.setText(secondActivity.getTitle());
            Activity thirdActivity = activities.get(2);
            question3Image.setImage(new Image(thirdActivity.getImage_path()));
            activity3Label.setText(thirdActivity.getTitle());
        }

        public void useDoublePointsJoker() {
            clientGameController.doublePoint();
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

    public void unableAllJokers() {
        timeJokerUsed = false;
        eliminateJokerUsed = false;
        doublePointsJokerUsed = false;
        eliminateJoker.setImage(new Image("/client.photos/jokerOneAnswer.png"));
        doublePointsJoker.setImage(new Image("/client.photos/doubleJoker.png"));
        timeJoker.setImage(new Image("/client.photos/timeJoker.png"));
    }


    }

