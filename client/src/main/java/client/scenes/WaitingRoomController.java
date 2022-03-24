package client.scenes;

import client.utils.ServerUtils;
import commons.Player;
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

class WaitingRoomEntryLabel {

    Label nameLabel;
    Long entryId;

    public WaitingRoomEntryLabel(Label nameLabel, Long entryId) {
        this.nameLabel = nameLabel;
        this.entryId = entryId;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitingRoomEntryLabel that = (WaitingRoomEntryLabel) o;
        return Objects.equals(nameLabel, that.nameLabel) && Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameLabel, entryId);
    }

    @Override
    public String toString() {
        return "WaitingRoomEntryLabel{" +
                "nameLabel=" + nameLabel +
                ", entryId=" + entryId +
                '}';
    }
}

public class WaitingRoomController implements Initializable {

    @FXML
    private Label currentLabel;

    @FXML
    private Button exitButton;

    @FXML
    private Button temporaryButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private GridPane grid;

    private double progress = 0;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ClientGameController clientGameController;

    private long gameId;
    private List<WaitingRoomEntryLabel> entries = new ArrayList<>();
    private ObservableList<WaitingRoomEntryLabel> data;

    @FXML
    private TableColumn<WaitingRoomEntryLabel, Label> playersCol;

    @FXML
    private TableView mainTable;

    @Inject
    public WaitingRoomController(ServerUtils server, ClientGameController clientGameController, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.clientGameController = clientGameController;
    }
    public WaitingRoomEntryLabel addPlayer(String name, long entryId) {
        Label nameLabel = new Label();
        nameLabel.setText("  " + name);
        nameLabel.getStyleClass().add("barColor");
        double minWidth = grid.getPrefWidth()/1.4;
        nameLabel.setMinWidth(minWidth);
        nameLabel.setMinHeight(35);
        return new WaitingRoomEntryLabel(nameLabel, entryId);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playersCol.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getNameLabel()));
    }

    public void resetState() {
        entries.clear();
    }

    public void showEntries() {
        data = FXCollections.observableList(entries);
        mainTable.setItems(data);
    }

    public void showPlayers(List<Player> playersEntries) {
        entries.clear();
        for (Player player : playersEntries) {
            WaitingRoomEntryLabel processedEntry = addPlayer(player.getUser().getName(), 0);
            entries.add(processedEntry);
        }
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void increaseProgress() {
        progress += 0.1;
        progressBar.setProgress(progress);
    }

    public void startGame() {
        if (entries.size() > 1) {
            server.startGame(gameId);
        }
    }

    public void exit() {
        clientGameController.exitGame();
        mainCtrl.showMainMenu();
    }

}

