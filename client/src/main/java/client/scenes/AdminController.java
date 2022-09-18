package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


class ActivityEntryLabel {

    Label title;
    Long entryId;
    Label edit;

    public ActivityEntryLabel(Label title, Long entryId) {
        this.title = title;
        this.entryId = entryId;
        this.edit = new Label("Edit");
    }

    public Label getTitle() {
        return title;
    }

    public void setTitle(Label title) {
        this.title = title;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Label getEdit() {
        return edit;
    }

    public void setEdit(Label edit) {
        this.edit = edit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, entryId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityEntryLabel that = (ActivityEntryLabel) o;
        return Objects.equals(title, that.title) && Objects.equals(entryId, that.entryId);
    }

    @Override
    public String toString() {
        return "ActivityEntryLabel{" +
                "title=" + title +
                ", entryId=" + entryId +
                '}';
    }
}

public class AdminController implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private Button menuButton;

    @FXML
    private Button newActivity;

    @FXML
    private TableColumn<ActivityEntryLabel, Label> editCol;

    @FXML
    private TableView tableView;

    @FXML // -> need to the column layout
    private TableColumn<ActivityEntryLabel, Label> titleCol;

    private List<ActivityEntryLabel> entries = new ArrayList<>();
    private ObservableList<ActivityEntryLabel> data;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public AdminController(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void backToMainMenu(){
        mainCtrl.showMainMenu();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleCol.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getTitle()));
        editCol.setCellValueFactory(q -> new SimpleObjectProperty<>(q.getValue().getEdit()));
    }

    public TableColumn<ActivityEntryLabel, Label> getTitleCol() {
        return titleCol;
    }

    public ActivityEntryLabel addLabel(String title, long entryId) {
        Label titleLabel = new Label();
        titleLabel.setText(title);
        titleLabel.setText("  " + entryId + ". " + title);
        titleLabel.getStyleClass().add("barColor");
        double minWidth = gridPane.getPrefWidth()/1.4;
        titleLabel.setMinWidth(minWidth);
       // titleLabel.setMinHeight(35);
        return new ActivityEntryLabel(titleLabel, entryId);
    }

    public void showEntries() {
        data = FXCollections.observableList(entries);
        tableView.setItems(data);
    }

    public void refresh() {
        entries.clear();
        var allTimeEntries = server.getActivties();

        for (Activity activity: allTimeEntries) {
            ActivityEntryLabel activityEntryLabel = addLabel(activity.getTitle(), activity.getId());
            entries.add(activityEntryLabel);
            activityEntryLabel.getEdit().setOnMouseClicked(event -> {
                goToEditActivity(activity.getId());
            });
        }
        showEntries();
    }

    public void goToAddActivityScreen() {
        mainCtrl.showAddActivity();
    }

    public void goToEditActivity(long id){
        mainCtrl.showEditActivity(id);
    }



}
