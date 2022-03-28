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
import javafx.scene.layout.HBox;

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
    Integer rank;

    public LeaderboardEntryLabel(Long entryId, Label nameLabel, Label scoreLabel) {
        this.entryId = entryId;
        this.nameLabel=nameLabel;
        this.scoreLabel=scoreLabel;
        this.rank = -1;
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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaderboardEntryLabel that = (LeaderboardEntryLabel) o;
        return Objects.equals(nameLabel, that.nameLabel) && Objects.equals(scoreLabel, that.scoreLabel) && Objects.equals(entryId, that.entryId) && Objects.equals(rank, that.rank);
    }

    @Override
    public String toString() {
        return "LeaderboardEntryLabel{" +
                "nameLabel=" + nameLabel +
                ", scoreLabel=" + scoreLabel +
                ", entryId=" + entryId +
                ", rank=" + rank +
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
    private Button leaveButton;

    @FXML
    private HBox multiEndButtons;

    @FXML
    private TableColumn<LeaderboardEntryLabel, Label> colName;

    @FXML
    private TableColumn<LeaderboardEntryLabel, Label> colScore;

    @FXML
    private TableColumn<LeaderboardEntryLabel, Integer> colRank;

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

            int pos = i+1;
            while (pos > 1 && entries.get(pos-1).getScoreLabel().getText().equals(entries.get(pos-2).getScoreLabel().getText())) {
                pos--;
            }
            entries.get(i).setRank(pos);
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
                pos++;
                while (pos > 1 && entries.get(pos-1).getScoreLabel().getText().equals(entries.get(pos-2).getScoreLabel().getText())) {
                    pos--;
                }
                rank.setText("You finished in " + (pos) + ordinal(pos) + " place!");
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
        leaderboardEntries.setSelectionModel(null);
        colName.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getNameLabel()));
        colScore.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getScoreLabel()));
        colRank.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getRank()));
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
        multiEndButtons.setMouseTransparent(true);
        hideNode(leaveButton);
        hideNode(replayButton);
        showNode(mainMenuButton);
        entries.clear();
        leaderboardEntries.scrollTo(0);
        rank.setText("ALL TIME SOLO LEADERBOARD");
    }

    public void initMulti(List<LeaderboardEntry> multiEntries, String userName, String gameProgress) {
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
        LeaderboardEntryLabel myEntry = null;
        for (LeaderboardEntry rawEntry : multiEntries) {
            LeaderboardEntryLabel processedEntry = addLabel(rawEntry.getName(), rawEntry.getScore(), 0);
            if (rawEntry.getName().equals(userName)) {
                processedEntry.getScoreLabel().getStyleClass().add("selectedScore");
                int width = 64;
                processedEntry.getScoreLabel().setStyle("-fx-shape: \"M 0 50 L 150 100 L 700 100 L 700 0 L 150 0 L 0 50 z\"");
                processedEntry.getScoreLabel().setMinWidth(width);
                processedEntry.getScoreLabel().setMaxHeight(100);
                myEntry = processedEntry;
            }
            entries.add(getNearestIndex(rawEntry.getScore(), 0, entries.size() - 1), processedEntry);
        }
        int pos = entries.indexOf(myEntry) + 1;
        while (pos > 1 && entries.get(pos-1).getScoreLabel().getText().equals(entries.get(pos-2).getScoreLabel().getText())) {
            pos--;
        }
        if (gameProgress.equals("End")) {
            rank.setText("You finished in " + (pos) + ordinal(pos) + " place!");
        } else {
            String motivation = "";
            if (pos == 1) {
                motivation = "Keep it up!";
            } else if (pos < entries.size() * 0.5) {
                motivation = "Almost there!";
            } else {
                motivation = "Keep trying!";
            }
            rank.setText("You are in " + (pos) + ordinal(pos) + " place! " + motivation);
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
        hideNode(mainMenuButton);
        showNode(leaveButton);
        showNode(replayButton);
        multiEndButtons.setMouseTransparent(false);
    }

    public void setMidScreen() {
        hideNode(mainMenuButton);
    }

    public void replay() {
        mainCtrl.joinGame(true);
    }

    public void resizeHeight(double height){
        grid.setPrefHeight(height);
    }

    public void hideNode(Control c) {
        c.setMouseTransparent(true);
        c.setStyle("visibility: hidden");
    }

    public void showNode(Control c) {
        c.setMouseTransparent(false);
        c.setStyle("visibility: visible");
    }
}
