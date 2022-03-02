/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.ServerUtils;
import commons.User;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.io.IOException;

public class MainCtrl {

    private Stage primaryStage;
    private User user;

    private LoginController loginController;
    private Scene loginScene;

    @Inject
    private ServerUtils server;


    public void initialize(Stage primaryStage, Pair<LoginController, Parent> overview) {
        this.primaryStage = primaryStage;
        this.loginController = overview.getKey();
        this.loginScene = new Scene(overview.getValue());

        showOverview();
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try {
                event.consume();
                quit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void showOverview() {
        primaryStage.setTitle("Quizzzz!");
        primaryStage.setScene(loginScene);
    }

    public void quit() throws IOException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit the application");
        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Goodbye!");
            if (user != null) {
                server.deleteSelf(user);
            }
            primaryStage.close();
        }
    }

    public void setUser(User user){
        this.user = user;
    }
}