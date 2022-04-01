package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIController;
import com.google.inject.Inject;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseConsumptionController implements Initializable, QuestionScene{

    @FXML private Label pointsLabel;
    @FXML private Label newPoints;
    @FXML private Label activityLabel;
    @FXML private ImageView doublePointsJoker;
    @FXML private ImageView eliminateJoker;
    @FXML private ImageView skipQuestionJoker;
    @FXML private ProgressBar progressBar;
    @FXML private Label timeText;
    @FXML private ImageView questionImage;
    @FXML private Label timeReduced;
    @FXML private Label questionsLeftLabel;
    @FXML private Button exitButton;
    @FXML private GridPane grid;

    @FXML
    private HBox jokerContainer;

    @FXML
    private AnchorPane ans1pane;

    @FXML
    private AnchorPane ans2pane;

    @FXML
    private AnchorPane ans3pane;

    @FXML
    private GridPane answer1;

    @FXML
    private GridPane answer2;

    @FXML
    private GridPane answer3;

    @FXML
    private Label activity1Label;

    @FXML
    private Label activity2Label;

    @FXML
    private Label activity3Label;

    @FXML
    private ImageView question1Image;

    @FXML
    private ImageView question3Image;

    @FXML
    private ImageView question2Image;

    @FXML
    private Label questionTxt;

    @FXML private ListView<Pair<String, Integer>> emojiChatView;
    @FXML private Node emojiChatContainer;

    List<Button> answers;

    private long chosenAnswer;

    private UIController uiController;

    private MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;

    private List<ImageView> jokerPics;

    @Inject
    public ChooseConsumptionController(MainCtrl mainCtrl, ClientGameController clientGameController, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
        this.serverUtils = serverUtils;

        this.answers = new ArrayList<>();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiController = new UIController(emojiChatView, emojiChatContainer, doublePointsJoker, eliminateJoker, skipQuestionJoker, jokerContainer, ans1pane, ans2pane,
                ans3pane, exitButton, progressBar, question1Image, question3Image, question2Image, activity1Label, activity2Label , activity3Label, questionTxt, timeText,
                pointsLabel, timeReduced, grid, answer1, answer2 , answer3, pointsLabel);
        uiController.initialize(location, resources);

        jokerPics = List.of(eliminateJoker, doublePointsJoker, skipQuestionJoker);
    }

    @Override
    public Label getPointsLabel() {
        return this.pointsLabel;
    }

    @Override
    public List<ImageView> getJokerPics() {
        return this.jokerPics;
    }

    @Override
    public Label getQuestionsLeftLabel() {
        return this.questionsLeftLabel;
    }

    @Override
    public void lockAnswer() {
        answer1.setDisable(true);
        answer2.setDisable(true);
        answer3.setDisable(true);
    }

    @Override
    public void showQuestion(NewQuestionMessage message) {
        uiController.showQuestion(message);
        setChosenAnswer(-1);
        questionImage.setImage(new Image(new ByteArrayInputStream(message.getImagesBytes().get(0))));
        activityLabel.setText(message.getActivities().get(0).getTitle());
        clientGameController.startTimer(progressBar, timeText);

    }

    @Override
    public void showAnswer(CorrectAnswerMessage message) {
        if (chosenAnswer == -1) {
            clientGameController.incrementNotAnswered();
        }
        uiController.showAnswer(message, chosenAnswer);
        clientGameController.changeScore(message.getScore(), pointsLabel, newPoints);
    }

    @Override
    public void showTimeReduced(String name) {
        timeReduced.setText(name + " has reduced your time!");
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

    public void useEliminateJoker() {
        long index = clientGameController.eliminateJoker();
        uiController.useEliminateJoker(index);
    }
    public void useTimeJoker() {
        clientGameController.timeJoker();
    }

    public void useDoublePointsJoker() {
        clientGameController.doublePoint();
    }

    public void useSkipQuestionJoker() {
        questionTxt.setText("You skipped this question!");
        clientGameController.skipQuestion();
    }

    public void submitAnswer(Event event){
        GridPane clickedPane = (GridPane) event.getSource();
        long clickedAnswer = Long.parseLong(clickedPane.getId().replace("answer", "")) - 1;
        uiController.colorSelectedAnswer(clickedAnswer);
        chosenAnswer = clickedAnswer;
        clientGameController.submitAnswer(clickedAnswer);
        clientGameController.resetNotAnswered();
    }

    @Override
    public ListView<Pair<String, Integer>> getEmojiChatView() {
        return this.emojiChatView;
    }

    @Override
    public Node getEmojiChatContainer() {
        return this.emojiChatContainer;
    }

    @Override
    public void displayEmojiChat(ObservableList<Pair<String, Integer>> newEmojiList) {
        this.emojiChatView.setItems(newEmojiList);
    }

    public void processEmoji(Event event) {
        ImageView emoji = (ImageView) event.getSource();
        int emojiId = Integer.parseInt(emoji.getId().replace("e", ""));
        clientGameController.sendEmoji(emojiId);
    }

    @Override
    public void reset() {
    }

    public void setChosenAnswer(long chosenAnswer) {
        this.chosenAnswer = chosenAnswer;
    }

    public void quit() {
        clientGameController.exitGame();
    }

}
