package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class LoginController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField nameBox;

    @FXML
    private Button quitButton;

    @FXML
    private Button submitButton;

    @FXML
    private TextField urlBox;

    @Inject
    public LoginController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void submit(){

    }

    public void quit(){

    }


}
