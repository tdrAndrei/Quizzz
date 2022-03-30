package client;

import client.scenes.QuestionScene;
import commons.Messages.EmojiMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class EmojiChat {
    private ObservableList<Pair<String, Integer>> emojiChatList;
    private List<QuestionScene> controllers;
    private final List<Image> emojiImageList;

    public EmojiChat() {
        emojiChatList = FXCollections.observableArrayList();

        emojiImageList = new ArrayList<>();
        emojiImageList.add(new Image("client.photos/angryEmoji.gif"));
        emojiImageList.add(new Image("client.photos/eyebrowEmoji.gif"));
        emojiImageList.add(new Image("client.photos/devilEmoji.gif"));
        emojiImageList.add(new Image("client.photos/sunglassesEmoji.gif"));
        emojiImageList.add(new Image("client.photos/confoundedEmoji.gif"));

        controllers = new ArrayList<>();
    }

    public EmojiChat addClient(QuestionScene controller) {
        controllers.add(controller);
        configureListView(controller.getEmojiChatView());
        return this;
    }

    public void configureListView(ListView<Pair<String, Integer>> emojiChatView) {
        emojiChatView.setCellFactory(param -> new ListCell<>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(Pair<String, Integer> valuePair, boolean empty) {
                super.updateItem(valuePair, empty);
                setContentDisplay(ContentDisplay.RIGHT);
                setStyle("-fx-background-color: #ffc5ac");
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setDisable(true);
                    imageView.setImage(emojiImageList.get(valuePair.getValue()));
                    imageView.setFitHeight(32.0);
                    imageView.setFitWidth(32.0);
                    setText(valuePair.getKey() + ": ");
                    setGraphic(imageView);
                    getGraphic().setBlendMode(BlendMode.DARKEN);
                }
            }
        });
    }

    public void emojiHandler(EmojiMessage message) {
        Platform.runLater(() -> {
            emojiChatList.add(new Pair<>(message.getUsername(), message.getEmojiIndex()));
            int size = emojiChatList.size();
            if (size > 3) {
                emojiChatList.remove(0);
            }

            modifiedEmojiChatList();
        });
    }

    public void resetChat() {
        emojiChatList = FXCollections.observableArrayList();
        modifiedEmojiChatList();
    }

    public void setVisibility(boolean visibility) {
        for (var controller : controllers) {
            controller.getEmojiChatContainer().setVisible(visibility);
        }
    }

    public void modifiedEmojiChatList() {
        for (var controller : controllers) {
            controller.displayEmojiChat(emojiChatList);
        }
    }
}
