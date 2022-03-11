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

    private MainMenuController mainMenuController;
    private Scene mainMenuScene;

    private MultiQuestionController multiCtrl;
    private Scene multiScene;

    private LeaderboardSoloController leaderboardSoloController;
    private Scene leaderboardSoloScene;

    @Inject
    private ServerUtils server;

    public void initialize(Stage primaryStage, Pair<LoginController, Parent> login,
                           Pair<MainMenuController, Parent> mainMenu,
                           Pair<MultiQuestionController, Parent> multiQuestion,
                           Pair<LeaderboardSoloController, Parent> leaderboardSolo) {
        this.primaryStage = primaryStage;

        this.loginController = login.getKey();
        this.loginScene = new Scene(login.getValue());

        this.mainMenuController = mainMenu.getKey();
        this.mainMenuScene = new Scene(mainMenu.getValue());

        this.multiCtrl = multiQuestion.getKey();
        this.multiScene = new Scene(multiQuestion.getValue());

        this.leaderboardSoloController = leaderboardSolo.getKey();
        this.leaderboardSoloScene = new Scene(leaderboardSolo.getValue());

        showLogin();
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

    public void showLeaderboardSolo() {
        primaryStage.setTitle("Quizzzz!");
        leaderboardSoloController.refresh();
        primaryStage.setScene(leaderboardSoloScene);
    }

    public void showLogin() {
        primaryStage.setTitle("Quizzzz!");
        primaryStage.setScene(loginScene);
    }

    public void showMainMenu(){
        primaryStage.setScene(mainMenuScene);
        mainMenuController.makeAnimations();
    }

    public void showMultiQuestion(){
       primaryStage.setScene(multiScene);
    }

    public void quit() throws IOException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit the application");
        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Goodbye!");
            mainMenuController.stopAnimatorThread();
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