package client.scenes;

import client.utils.ServerUtils;
import commons.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    @FXML
    private Label warnBox;

    @Inject
    public LoginController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void submit(ActionEvent event) {
        server.setUrl(urlBox.getText());
        User user = server.addUser(new User(nameBox.getText()));
        if (user == null) {
            nameBox.setText("");
            warnBox.setText("Chosen username is already taken");
            return;
        }
        mainCtrl.setUser(user);
        mainCtrl.showMainMenu();
    }

    public void quit(ActionEvent event) throws IOException {
        mainCtrl.quit();
    }
}



