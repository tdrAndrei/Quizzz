package client.utils;

import commons.Activity;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UIController {

    private ListView<Pair<String, Integer>> emojiChatView;
    private Node emojiChatContainer;
    private ImageView doublePointsJoker;
    private ImageView eliminateJoker;
    private ImageView skipQuestionJoker;
    private HBox jokerContainer;
    private AnchorPane ans1pane;
    private AnchorPane ans2pane;
    private AnchorPane ans3pane;
    private Button exitButton;
    private ProgressBar progressBar;
    private ImageView question1Image;
    private ImageView question3Image;
    private ImageView question2Image;
    private Label activity1Label;
    private Label activity2Label;
    private Label activity3Label;
    private Label questionLabel;
    private Label timeText;
    private Label pointsLabel;
    private Label timeReduced;
    private GridPane grid;
    private GridPane answer1;
    private GridPane answer2;
    private GridPane answer3;
    private Label newPoints;

    private List<AnchorPane> answerPaneList;
    private List<Label> activityLabelList;

    private double baseWidth;
    private double baseHeight;

    private final Border correctAnswerBorder = new Border(new BorderStroke(new Color(0, 164.0/255.0, 78.0/255.0, 0.5), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    private final Border selectedAnswerBorder = new Border(new BorderStroke(new Color(1, 0.9, 0, 0.5), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    private final Border selectedWrongAnswerBorder = new Border(new BorderStroke(new Color(148.0/255.0, 0, 17.0/255.0, 0.5), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));

    public UIController(ListView<Pair<String, Integer>> emojiChatView, Node emojiChatContainer, ImageView doublePointsJoker, ImageView eliminateJoker, ImageView skipQuestionJoker, HBox jokerContainer, AnchorPane ans1pane, AnchorPane ans2pane, AnchorPane ans3pane, Button exitButton, ProgressBar progressBar, ImageView question1Image, ImageView question3Image, ImageView question2Image, Label activity1Label, Label activity2Label, Label activity3Label, Label questionLabel, Label timeText, Label pointsLabel, Label timeReduced, GridPane grid, GridPane answer1, GridPane answer2, GridPane answer3, Label newPoints) {
        this.emojiChatView = emojiChatView;
        this.emojiChatContainer = emojiChatContainer;
        this.doublePointsJoker = doublePointsJoker;
        this.eliminateJoker = eliminateJoker;
        this.skipQuestionJoker = skipQuestionJoker;
        this.jokerContainer = jokerContainer;
        this.ans1pane = ans1pane;
        this.ans2pane = ans2pane;
        this.ans3pane = ans3pane;
        this.exitButton = exitButton;
        this.progressBar = progressBar;
        this.question1Image = question1Image;
        this.question3Image = question3Image;
        this.question2Image = question2Image;
        this.activity1Label = activity1Label;
        this.activity2Label = activity2Label;
        this.activity3Label = activity3Label;
        this.questionLabel = questionLabel;
        this.timeText = timeText;
        this.pointsLabel = pointsLabel;
        this.timeReduced = timeReduced;
        this.grid = grid;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.newPoints = newPoints;
        instantiateLists();
    }

    public void instantiateLists() {
        this.answerPaneList = new ArrayList<>();
        this.answerPaneList.add(ans1pane);
        this.answerPaneList.add(ans2pane);
        this.answerPaneList.add(ans3pane);

        this.activityLabelList = new ArrayList<>();
        this.activityLabelList.add(activity1Label);
        this.activityLabelList.add(activity2Label);
        this.activityLabelList.add(activity3Label);
    }
    public void showQuestion(NewQuestionMessage message) {
        answer1.setDisable(false);
        answer2.setDisable(false);
        answer3.setDisable(false);

        timeReduced.setText("");
        activity1Label.setStyle("-fx-text-fill: #000000");
        activity2Label.setStyle("-fx-text-fill: #000000");
        activity3Label.setStyle("-fx-text-fill: #000000");
        questionLabel.setText(message.getTitle());

        questionLabel.setTextFill(Color.rgb(0, 0, 0));
        setQuestions(message.getActivities(), message.getImagesBytes());
    }

    public void colorSelectedAnswer(long selectedAnswer) {
        answerPaneList.get((int) selectedAnswer).setBorder(selectedAnswerBorder);
    }

    public void lockAnswer() {
        answer1.setDisable(true);
        answer2.setDisable(true);
        answer3.setDisable(true);
    }

    public void showAnswer(CorrectAnswerMessage message, long chosenAnswer) {
        long correctAnswer = message.getCorrectAnswer();
        answerPaneList.get((int) chosenAnswer).setBorder(selectedWrongAnswerBorder);
        answerPaneList.get((int) correctAnswer).setBorder(correctAnswerBorder);
        activityLabelList.get((int) correctAnswer).setTextFill(Color.rgb(94, 206, 119));

        if (chosenAnswer == correctAnswer) {
            questionLabel.setText("That's Right!");
            questionLabel.setTextFill(Color.rgb(94, 206, 119));
        } else {
            questionLabel.setText("Wrong!");
            questionLabel.setTextFill(Color.rgb(201, 89, 89));
        }
    }

    public void initialize (URL location, ResourceBundle resources){
        newPoints.setText("");
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
    }

    public void useEliminateJoker(long index) {
        if (index == 0) {
            question1Image.setImage(new Image("/client.photos/bomb.png"));
            activity1Label.setStyle("-fx-text-fill: #c95959");
            activity1Label.setText("THIS ANSWER HAS BEEN ELIMINATED");
            answer1.setDisable(true);
        } else if (index == 1) {
            question2Image.setImage(new Image("/client.photos/bomb.png"));
            activity2Label.setStyle("-fx-text-fill: #c95959");
            activity2Label.setText("THIS ANSWER HAS BEEN ELIMINATED");
            answer2.setDisable(true);
        } else {
            question3Image.setImage(new Image("/client.photos/bomb.png"));
            activity3Label.setStyle("-fx-text-fill: #c95959");
            activity3Label.setText("THIS ANSWER HAS BEEN ELIMINATED");
            answer3.setDisable(true);
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

}
