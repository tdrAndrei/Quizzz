package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.io.File;


public class AddActivityController {

    @FXML
    private TextField sourceBox;

    @FXML
    private Button backButton;

    @FXML
    private TextField consumptionBox;

    @FXML
    private TextField imagePath;

    @FXML
    private Button submitButton;

    @FXML
    private Label warnField;

    @FXML
    private Label imageWarnLabel;

    @FXML
    private Label numberWarnLabel;

    @FXML
    private TextField titleBox;

    @FXML
    private Label messageLabel;


    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public AddActivityController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void backToAdmin() {
        resetWarnings();
        mainCtrl.showAdmin();
        messageLabel.setText("");
    }

    public void resetBoxes() {
        titleBox.clear();
        consumptionBox.clear();
        imagePath.clear();
        sourceBox.clear();
        resetWarnings();
    }

    public boolean checkIfFilledCorrectly() {
        if (titleBox.getText().equals("") || consumptionBox.getText().equals("")
                || imagePath.getText().equals("") || sourceBox.getText().equals("")) {
            warnField.setText("Please fill all fields");
            return false;
        }
        File file = new File(imagePath.getText());
        System.out.println(file.exists());
        if (!file.exists()) {
            imageWarnLabel.setText("Image path is invalid");
            return false;
        }

        return checkConsumptionBox();
    }

    public void submit() {
        if (!checkIfFilledCorrectly()) {
            return;
        }
        String title = titleBox.getText();
        String imagePath = this.imagePath.getText();
        String source = this.sourceBox.getText();
        long consumption = Long.parseLong(this.consumptionBox.getText());
        messageLabel.setText("Your activity has been successfully added to the database");
        server.addActivity(new Activity(imagePath, title, consumption, source));
        resetBoxes();
    }

    public void resetWarnings() {
        warnField.setText("");
        imageWarnLabel.setText("");
        numberWarnLabel.setText("");
    }

    public boolean checkConsumptionBox() {
        try {
            Integer.parseInt(consumptionBox.getText());
            long number = Long.parseLong(consumptionBox.getText());
            return true;
        } catch (Exception e) {
            numberWarnLabel.setText("Please enter a number");
            return false;
        }
    }

}
