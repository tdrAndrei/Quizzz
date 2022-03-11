package client.scenes;

import client.utils.ServerUtils;
import commons.User;
import jakarta.ws.rs.BadRequestException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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

    @FXML
    private GridPane grid;

    @Inject
    public LoginController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void submit(ActionEvent event) {
        server.setUrl(urlBox.getText());
        try{
            User user = server.addUser(new User(nameBox.getText()));
            mainCtrl.setUser(user);
            mainCtrl.showMainMenu();
        } catch (BadRequestException e){
            nameBox.setText("");
            warnBox.setText("This username is already taken.");
        }
    }

    public void quit(ActionEvent event) throws IOException {
        mainCtrl.quit();
    }

    public void resizeWidth(double width){
        grid.setPrefWidth(width);
    }

    public void resizeHeight(double height){
        grid.setPrefHeight(height);
    }

}



