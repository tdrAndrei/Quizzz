package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseConsumptionController implements Initializable, QuestionScene{

    @FXML private Label pointsLabel;
    @FXML private Label activity1Label;
    @FXML private ImageView doublePointsJoker;
    @FXML private ImageView eliminateJoker;
    @FXML private ImageView skipQuestionJoker;
    @FXML private ProgressBar progressBar;
    @FXML private Label timeText;
    @FXML private ImageView question1Image;

    @FXML private ListView<Pair<String, Integer>> emojiChatView;
    @FXML private Node emojiChatContainer;

    private MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;

    private List<ImageView> jokerPics;

    @Inject
    public ChooseConsumptionController(MainCtrl mainCtrl, ClientGameController clientGameController, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
        this.serverUtils = serverUtils;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    public void lockAnswer() {

    }

    @Override
    public void showQuestion(NewQuestionMessage message) {
        question1Image.setImage(new Image(new ByteArrayInputStream(message.getImagesBytes().get(0))));
        activity1Label.setText(message.getActivities().get(0).getTitle());
        clientGameController.startTimer(progressBar, timeText);
    }

    @Override
    public void showAnswer(CorrectAnswerMessage correctAnswerMessage) {

    }

    @Override
    public void showTimeReduced(String name) {

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

    public void useEliminateJoker(){}
    public void useTimeJoker(){}
    public void useDoublePointsJoker(){}
    public void useSkipQuestionJoker(){}
    public void submitUserAnswer(){ }

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

    public void quit() {
        clientGameController.exitGame();
    }

}
