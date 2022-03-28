package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class EditActivityController {

    @FXML
    private TextField sourceBox;

    @FXML
    private Button backButton;

    @FXML
    private TextField consumptionBox;

    @FXML
    private Button submitButton;

    @FXML
    private Label warnField;

    @FXML
    private Label numberWarnLabel;

    @FXML
    private TextField titleBox;

    @FXML
    private Label messageLabel;


    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Activity current;

    @Inject
    public EditActivityController(ServerUtils server, MainCtrl mainCtrl, Activity current) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.current = current;
    }

    public void backToAdmin() {
        resetWarnings();
        mainCtrl.showAdmin();
        messageLabel.setText("");
    }

    public void resetBoxes() {
        titleBox.setText(current.getTitle());
        consumptionBox.setText(current.getConsumption_in_wh().toString());
        sourceBox.setText(current.getSource());
        resetWarnings();
    }

    public boolean checkIfFilledCorrectly() {
        if (titleBox.getText().equals("") || consumptionBox.getText().equals("")
                || sourceBox.getText().equals("")) {
            warnField.setText("Please fill all fields");
            return false;
        }

        return checkConsumptionBox();
    }

    public void submit() {
        if (!checkIfFilledCorrectly()) {
            return;
        }
        String title = this.titleBox.getText();
        String source = this.sourceBox.getText();
        long consumption = Long.parseLong(this.consumptionBox.getText());
        messageLabel.setText("This activity has been successfully edited");
        server.updateActivity(current, title, consumption, source);
        this.current = server.getActivityById(this.current.getId());
        resetBoxes();
    }

    public void resetWarnings() {
        warnField.setText("");
        numberWarnLabel.setText("");
    }

    public void setCurrent(Activity activity){
        this.current = activity;
    }

    public Activity getCurrent() {
        return current;
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
