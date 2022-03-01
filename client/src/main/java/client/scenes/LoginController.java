package client.scenes;

import client.utils.ServerUtils;
import commons.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.io.IOException;

public class LoginController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField nameBox;

    @FXML
    private TextField urlBox;

    @Inject
    public LoginController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void submit(ActionEvent event) {
        server.setUrl(urlBox.getText());
        User user = server.addUser(new User(nameBox.getText()));
        mainCtrl.setUser(user);
    }

    public void quit(ActionEvent event) throws IOException {
        mainCtrl.quit();
    }
}



