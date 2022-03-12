package client.scenes;

import client.utils.ServerUtils;
import commons.LeaderboardEntry;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

class LeaderboardEntryLabel {

    Label nameLabel;
    int points;

    public LeaderboardEntryLabel(Label nameLabel, int points) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaderboardEntryLabel)) return false;
        LeaderboardEntryLabel that = (LeaderboardEntryLabel) o;
        return points == that.points && Objects.equals(nameLabel, that.nameLabel);
    }

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
                "nameLabel=" + nameLabel +
                ", points=" + points +
                '}';
    }
}

public class LeaderboardSoloController implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TableView leaderboardEntries;

    @FXML
    private TableColumn<LeaderboardEntryLabel, Label> colName;

    @FXML
    private TableColumn<LeaderboardEntryLabel, Integer> colScore;

    @FXML
    private TextField name;

    @FXML
    private TextField score;

    private ObservableList<LeaderboardEntryLabel> data;

    private List<LeaderboardEntryLabel> entries = new ArrayList<>();

    private int scoreLowerBound;
    private int scoreUpperBound;

    @Inject
    public LeaderboardSoloController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void addEntry() {
        try {
            server.addLeaderboardEntry(new LeaderboardEntry(name.getText(), Integer.parseInt(score.getText())));
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        refresh();
    }

    public void addLabel(String name, int sc) {
            Label label = new Label();
            label.setText(name);
            int range = scoreUpperBound-scoreLowerBound;
            if (sc >= (scoreLowerBound+range*5/6)) {
                label.getStyleClass().add("greenBar");
            } else if (sc >= (scoreLowerBound+range*4/6)) {
                label.getStyleClass().add("greenYellowBar");
            } else if (sc >= (scoreLowerBound+range*3/6)) {
                label.getStyleClass().add("yellowBar");
            } else if (sc >= (scoreLowerBound+range*2/6)) {
                label.getStyleClass().add("yellowOrangeBar");
            } else if (sc >= (scoreLowerBound+range*1/6)) {
                label.getStyleClass().add("orangeBar");
            } else {
                label.getStyleClass().add("redBar");
            }
            entries.add(getNearestIndex(sc, 0, entries.size() - 1), new LeaderboardEntryLabel(label, sc));
            data = FXCollections.observableList(entries);
            leaderboardEntries.setItems(data);
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

    public void refresh() {
        entries.clear();
        var allTimeEntries = server.getLeaderboardEntries();

        scoreUpperBound = 0;
        scoreLowerBound = Integer.MAX_VALUE;

        for (LeaderboardEntry rawEntry : allTimeEntries) {
            if (rawEntry.getScore() < scoreLowerBound) {
                scoreLowerBound = rawEntry.getScore();
            }
            if (rawEntry.getScore() > scoreUpperBound) {
                scoreUpperBound = rawEntry.getScore();
            }
        }
        for (LeaderboardEntry rawEntry : allTimeEntries) {
            addLabel(rawEntry.getName(), rawEntry.getScore());
        }
    }


}
