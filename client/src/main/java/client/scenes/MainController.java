package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.io.IOException;

public class MainController {

    @FXML
    private ImageView factImage;
    @FXML
    private Label factLabel;
    @FXML
    private Button leaderboardButton;
    @FXML
    private ImageView logoImage;
    @FXML
    private Button multiplayerButton;
    @FXML
    private Button quitButton;
    @FXML
    private Button soloButton;


    private final MainCtrl mainCtrl;

    @Inject
    public MainController(MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
    }

    public void quit() throws IOException {
        mainCtrl.quit();
    }

    public void startSolo(){
        mainCtrl.showMultiQuestion();
    }
}

