package client.scenes;

import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import javax.inject.Inject;

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

    public void submit(ActionEvent even){
        server.setUrl(urlBox.getText());
        server.addUser(nameBox.getText());

    }

    public void quit(ActionEvent even){

    }
//    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Logout");
//        alert.setHeaderText("You are about to logout");
//        alert.setContentText("Do you want to save before exiting");
//
//        if (alert.showAndWait().get() == ButtonType.OK) {
//        System.out.println("You successfully logged out");
//        stage.close();
//    }
}



