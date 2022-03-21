package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javax.inject.Inject;


public class WaitingRoomController {

    @FXML
    private Label currentLabel;

    @FXML
    private Button exitButton;

    @FXML
    private HBox hBox;

    @FXML
    private Button temporaryButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private GridPane grid;

    @FXML
    private ScrollPane scrollPane;

    private double progress = 0;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private long gameId;

    @Inject
    public WaitingRoomController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void increaseProgress() {
        progress += 0.1;
        progressBar.setProgress(progress);
    }

    public void startGame() {
        server.startGame(gameId);
    }

    public void exit() {
        mainCtrl.showMainMenu();
    }

}

