package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CompareQuestionController {

    @FXML
    private Label title;

    @FXML
    private ImageView mainActivityImage;

    @FXML
    private Label mainActivityLabel;

    @FXML
    private ImageView answer1Image;

    @FXML
    private Label answer1Label;

    @FXML
    private ImageView answer2Image;

    @FXML
    private Label answer2Label;

    @FXML
    private ImageView answer3Image;

    @FXML
    private Label answer3Label;

    private long chosenAnswer;

    private MainCtrl mainCtrl;
    private final ClientGameController clientGameController;
    private final ServerUtils serverUtils;

    private boolean isMulti;

    @Inject
    public CompareQuestionController(MainCtrl mainCtrl, ClientGameController clientGameController, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.clientGameController = clientGameController;
        this.serverUtils =serverUtils;
    }

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showQuestion(NewQuestionMessage message) {
        title.setText(message.getTitle());
        showActivities(message.getActivities(), message.getImagesBytes());
    }

    public void showAnswer(CorrectAnswerMessage message) {
        long index = message.getCorrectAnswer();
        if (chosenAnswer == index) {
            title.setText("That's Right!");
        } else {
            title.setText("Wrong!");
        }
    }

    public void showActivities(List<Activity> activities, List<byte[]> imageByteList) {
        Activity mainActivity = activities.get(3);
        mainActivityImage.setImage(new Image(new ByteArrayInputStream(imageByteList.get(3))));
        mainActivityLabel.setText(mainActivity.getTitle());
        Activity firstActivity = activities.get(0);
        answer1Image.setImage(new Image(new ByteArrayInputStream(imageByteList.get(0))));
        answer1Label.setText(firstActivity.getTitle());
        Activity secondActivity = activities.get(1);
        answer2Image.setImage(new Image(new ByteArrayInputStream(imageByteList.get(1))));
        answer2Label.setText(secondActivity.getTitle());
        Activity thirdActivity = activities.get(2);
        answer3Image.setImage(new Image(new ByteArrayInputStream(imageByteList.get(2))));
        answer3Label.setText(thirdActivity.getTitle());
    }

    public void submit1() {
        if (!clientGameController.isDisableJokerUsage()) {
            chosenAnswer = 0;
            clientGameController.submitAnswer(0);
            clientGameController.setDisableJokerUsage(true);
        }
    }
    public void submit2() {
        if (!clientGameController.isDisableJokerUsage()) {
            chosenAnswer = 1;
            clientGameController.submitAnswer(1);
            clientGameController.setDisableJokerUsage(true);
        }
    }
    public void submit3() {
        if (!clientGameController.isDisableJokerUsage()) {
            chosenAnswer = 2;
            clientGameController.submitAnswer(2);
            clientGameController.setDisableJokerUsage(true);
        }
    }


    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }
}
