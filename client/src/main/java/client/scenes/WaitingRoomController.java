package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


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


    public void increaseProgress() {
        progress += 0.1;
        progressBar.setProgress(progress);
    }

}

