package client.scenes;

import client.utils.ServerUtils;
import commons.User;
import jakarta.ws.rs.BadRequestException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LoginController implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String resourcesPath = "client/src/main/resources/";

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameBox.setText(readProperty("username"));
        nameBox.setFocusTraversable(false);

        String serverUrl = readProperty("server");
        if (!serverUrl.equals(""))
            urlBox.setText(serverUrl);
        urlBox.setFocusTraversable(false);
    }

    public void submit(ActionEvent event) {
        server.setUrl(urlBox.getText());
        writeProperty("username", nameBox.getText());
        writeProperty("server", urlBox.getText());
        try{
            User user = server.addUser(new User(nameBox.getText()));
            mainCtrl.setUser(user);
            mainCtrl.showMainMenu();
        } catch (BadRequestException e){
            nameBox.setText("");
            warnBox.setText("This username is already taken.");
        }
    }

    private String readProperty(String property) {
        try {
            File config = new File(resourcesPath + "config.txt");
            boolean toCreate = config.createNewFile();
            String value = "";

            if (!toCreate) {
                Scanner sc = new Scanner(config);
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.contains(property)) {
                        value = line.replaceAll(property + "=|" + System.lineSeparator(), "");
                        break;
                    }
                }
                sc.close();
            }

            return value;
        } catch (IOException e) {
            return "";
        }
    }

    private void writeProperty(String property, String value) {
        String insertInFile = String.format("%s=%s", property, value);
        try {
            File config = new File(resourcesPath + "config.txt");
            boolean toCreate = config.createNewFile();
            String toWrite = insertInFile + System.lineSeparator();

            if (!toCreate) {
                BufferedReader bf = new BufferedReader(new FileReader(config));

                String current = "";
                while ((current = bf.readLine()) != null) {
                    if (!current.matches(String.format("%s=(.*)", property))) {
                        toWrite += current + System.lineSeparator();
                    }
                }
                bf.close();
            }

            PrintWriter pr = new PrintWriter(config);
            pr.write(toWrite);
            pr.flush();
            pr.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ;
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



