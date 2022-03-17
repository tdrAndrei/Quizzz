package client.scenes;

import com.google.inject.Inject;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import commons.Activity;
import commons.Messages.Message;
import commons.Messages.NewQuestionMessage;
import commons.Question;
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

    private MainCtrl mainCtrl;

    // ImageView imageView;
    Image image = new Image("/client.photos/usedJoker.png");

    @Inject
    public MultiQuestionController(MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
    }


    public void quit() throws IOException {
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

    public void showEstimate() {
        mainCtrl.showEstimate();
    }

    public void makeNewQuestion(NewQuestionMessage message) {
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
    }

