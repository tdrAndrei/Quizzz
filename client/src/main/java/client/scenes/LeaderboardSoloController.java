package client.scenes;

import client.utils.ServerUtils;
import commons.LeaderboardEntry;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

class LeaderboardEntryLabel {

    Label nameLabel;
    Label scoreLabel;
    Long entryId;

    public LeaderboardEntryLabel(Long entryId, Label nameLabel, Label scoreLabel) {
        this.entryId = entryId;
        this.nameLabel=nameLabel;
        this.scoreLabel=scoreLabel;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setName(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public void setScoreLabel(Label scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaderboardEntryLabel)) return false;
        LeaderboardEntryLabel that = (LeaderboardEntryLabel) o;
        return Objects.equals(scoreLabel, that.scoreLabel) && Objects.equals(nameLabel, that.nameLabel);
    }

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
                "nameLabel=" + nameLabel +
                ", scoreLabel=" + scoreLabel +
                '}';
    }
}

public class LeaderboardSoloController implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TableView leaderboardEntries;

    @FXML
    private Button mainMenuButton;

    @FXML
    private Button replayButton;

    @FXML
    private TableColumn<LeaderboardEntryLabel, Label> colName;

    @FXML
    private TableColumn<LeaderboardEntryLabel, Label> colScore;

    @FXML
    private GridPane grid;

    @FXML
    private Label rank;

    private ObservableList<LeaderboardEntryLabel> data;

    private List<LeaderboardEntryLabel> entries = new ArrayList<>();

    private int scoreLowerBound;
    private int scoreUpperBound;

    @Inject
    public LeaderboardSoloController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public LeaderboardEntryLabel addLabel(String name, int sc, long entryId) {
            Label nameLabel = new Label();
            nameLabel.setText(name);
            int range = scoreUpperBound-scoreLowerBound;
            if (sc >= (scoreLowerBound+range*5/6)) {
                nameLabel.getStyleClass().add("greenBar");
            } else if (sc >= (scoreLowerBound+range*4/6)) {
                nameLabel.getStyleClass().add("greenYellowBar");
            } else if (sc >= (scoreLowerBound+range*3/6)) {
                nameLabel.getStyleClass().add("yellowBar");
            } else if (sc >= (scoreLowerBound+range*2/6)) {
                nameLabel.getStyleClass().add("yellowOrangeBar");
            } else if (sc >= (scoreLowerBound+range*1/6)) {
                nameLabel.getStyleClass().add("orangeBar");
            } else {
                nameLabel.getStyleClass().add("redBar");
            }
            Label scoreLabel = new Label();
            scoreLabel.setText(String.valueOf(sc));

            return new LeaderboardEntryLabel(entryId, nameLabel, scoreLabel);
    }

    public void resizeLabels() {
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntryLabel entry = entries.get(i);

            int range = scoreUpperBound-scoreLowerBound;
            double minWidth = grid.getPrefWidth()/3;
            double x = minWidth/100;

            int score = Integer.parseInt(entry.getScoreLabel().getText());

            if (score < (scoreLowerBound+range/6)) {
                minWidth+=50*x;
            } else if (score < (scoreLowerBound+range*2/6)) {
                minWidth+=40*x;
            } else if (score < (scoreLowerBound+range*3/6)) {
                minWidth+=30*x;
            } else if (score < (scoreLowerBound+range*4/6)) {
                minWidth+=20*x;
            } else if (score < (scoreLowerBound+range*5/6)) {
                minWidth+=10*x;
            }
            entries.get(i).getNameLabel().setStyle("-fx-shape: \"M 0 100 L "+(minWidth)+" 100 L "+(minWidth+25)+" 50 L "+(minWidth)+" 0 L 0 0 L 0 100 z\"");
            entries.get(i).getNameLabel().setMinWidth(minWidth);
        }
        showEntries();
    }

    public void showMine(Long entryId) {
        int pos = 0;
        while (pos < entries.size()) {
            LeaderboardEntryLabel entry = entries.get(pos);
            if (Objects.equals(entry.getEntryId(), entryId)) {
                entries.get(pos).getScoreLabel().getStyleClass().add("selectedScore");
                int width = 64;
                entries.get(pos).getScoreLabel().setStyle("-fx-shape: \"M 0 50 L 150 100 L 700 100 L 700 0 L 150 0 L 0 50 z\"");
                entries.get(pos).getScoreLabel().setMinWidth(width);
                entries.get(pos).getScoreLabel().setMaxHeight(100);
                rank.setText("You finished in " + (pos+1) + ordinal(pos+1) + " place!");
                break;
            }
            pos++;
        }
        showEntries();
        leaderboardEntries.scrollTo(pos);
    }
    public static String ordinal(int n) {
        if (n % 100 > 3 && n % 100 < 21) {
            return "th";
        } else if (n % 10 == 1) {
            return "st";
        } else if (n % 10 == 2) {
            return "nd";
        } else if (n % 10 == 3) {
            return "rd";
        } else {
            return "th";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getNameLabel()));
        colScore.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getScoreLabel()));
    }

    public void backToMainMenu(){
        mainCtrl.showMainMenu();
    }

    public int getNearestIndex(int target, int low, int high) {
        if (low > high) {
            return low;
        }
        int mid = (low+high)/2;
        if ((mid == 0 || Integer.parseInt(entries.get(mid-1).getScoreLabel().getText()) >= target)
                && target > Integer.parseInt(entries.get(mid).getScoreLabel().getText())) {
            return mid;
        }
        if (target <= Integer.parseInt(entries.get(mid).getScoreLabel().getText())) {
            return getNearestIndex(target, mid+1, high);
        }
        return getNearestIndex(target, low, mid-1);
    }

    public void resetState() {
        replayButton.setStyle("visibility: hidden");
        mainMenuButton.setText("BACK");
        entries.clear();
        leaderboardEntries.scrollTo(0);
        rank.setText("");
    }

    public void initMulti(List<LeaderboardEntry> multiEntries, String userName) {
        entries.clear();

        scoreUpperBound = 0;
        scoreLowerBound = Integer.MAX_VALUE;

        for (LeaderboardEntry rawEntry : multiEntries) {
            if (rawEntry.getScore() < scoreLowerBound) {
                scoreLowerBound = rawEntry.getScore();
            }
            if (rawEntry.getScore() > scoreUpperBound) {
                scoreUpperBound = rawEntry.getScore();
            }
        }
        for (LeaderboardEntry rawEntry : multiEntries) {
            LeaderboardEntryLabel processedEntry = addLabel(rawEntry.getName(), rawEntry.getScore(), 0);
            if (rawEntry.getName().equals(userName)) {
                processedEntry.getScoreLabel().getStyleClass().add("selectedScore");
                int width = 64;
                processedEntry.getScoreLabel().setStyle("-fx-shape: \"M 0 50 L 150 100 L 700 100 L 700 0 L 150 0 L 0 50 z\"");
                processedEntry.getScoreLabel().setMinWidth(width);
                processedEntry.getScoreLabel().setMaxHeight(100);
                //rank.setText("You finished in " + (pos+1) + ordinal(pos+1) + " place!");
            }
            entries.add(getNearestIndex(rawEntry.getScore(), 0, entries.size() - 1), processedEntry);
        }
        resizeLabels();
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
            entries.add(getNearestIndex(rawEntry.getScore(), 0, entries.size() - 1), addLabel(rawEntry.getName(), rawEntry.getScore(), rawEntry.getId()));
        }
        resizeLabels();
    }

    public void showEntries() {
        data = FXCollections.observableList(entries);
        leaderboardEntries.setItems(data);
    }

    public void resizeWidth(double width){
        grid.setPrefWidth(width);
        resizeLabels();
    }

    public void setEndScreen() {
        mainMenuButton.setText("LEAVE");
        mainMenuButton.setStyle("visibility: visible");
        replayButton.setStyle("visibility: visible");

    }

    public void setMidScreen() {
     mainMenuButton.setStyle("visibility: hidden");
    }

    public void replay() {
        mainCtrl.joinGame(true);
    }

    public void resizeHeight(double height){
        grid.setPrefHeight(height);
    }


}
