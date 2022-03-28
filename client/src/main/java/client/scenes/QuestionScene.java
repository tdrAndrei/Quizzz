package client.scenes;

import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.List;

public interface QuestionScene {
    void reset();
    Label getPointsLabel();
    List<ImageView> getJokerPics();
    void lockAnswer();
    void showQuestion(NewQuestionMessage newQuestionMessage);
    void showAnswer(CorrectAnswerMessage correctAnswerMessage);
    void showTimeReduced(String name);

    void subscribeToEmojiUpdate(ObservableList<Pair<String, Integer>> newEmojiList);
}
