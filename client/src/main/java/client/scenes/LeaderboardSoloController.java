package client.scenes;

import client.utils.ServerUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

class LeaderboardEntry { // TEMPORARY

    Label nameLabel;
    int points;

    public LeaderboardEntry(Label nameLabel, int points) {
        this.nameLabel=nameLabel;
        this.points=points;
    }

    public Label getNameLabel() {
        return nameLabel;
    }
    public int getPoints() {
        return points;
    }
    public void setName(Label nameLabel) {
        this.nameLabel = nameLabel;
    }
    public void setPoints(int points) {
        this.points = points;
    }
}

public class LeaderboardSoloController implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TableView leaderboardEntries;

    @FXML
    private TableColumn<LeaderboardEntry, Label> colName;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> colScore;

    @FXML
    private TextField score;

    private ObservableList<LeaderboardEntry> data;

    private List<LeaderboardEntry> entries = new LinkedList<>();

    @Inject
    public LeaderboardSoloController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void addLabel() { // WILL HAVE INPUT INT SCORE INSTEAD OF TAKING FROM TEXT FIELD
        try {
            int sc = Integer.parseInt(score.getText());
            Label label = new Label();
            label.setText("This is a new label.");
            if (sc >= 5000) {
                label.getStyleClass().add("greenBar");
            } else if (sc >= 4000) {
                label.getStyleClass().add("greenYellowBar");
            } else if (sc >= 3000) {
                label.getStyleClass().add("yellowBar");
            } else if (sc >= 2000) {
                label.getStyleClass().add("yellowOrangeBar");
            } else if (sc >= 1000) {
                label.getStyleClass().add("orangeBar");
            } else {
                label.getStyleClass().add("redBar");
            }
            entries.add(getNearestIndex(sc, 0, entries.size() - 1), new LeaderboardEntry(label, sc));
            data = FXCollections.observableList(entries);
            leaderboardEntries.setItems(data);
        } catch (NumberFormatException e) {
            score.setText("0");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getNameLabel()));
        colScore.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getPoints()));
    }

    public void backToMainMenu(){
        mainCtrl.showMainMenu();
    }

    public int getNearestIndex(int target, int low, int high) {
        if (low > high) {
            return low;
        }
        int mid = (low+high)/2;
        if ((mid == 0 || entries.get(mid-1).getPoints() >= target) && target > entries.get(mid).getPoints()) {
            return mid;
        }
        if (target <= entries.get(mid).getPoints()) {
            return getNearestIndex(target, mid+1, high);
        }
        return getNearestIndex(target, low, mid-1);
    }



}
