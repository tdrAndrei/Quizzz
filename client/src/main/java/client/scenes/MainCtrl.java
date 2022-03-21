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

    private EstimateQuestionController estimateQuestionController;
    private Scene estimateQuestionScene;

    private WaitingRoomController waitingRoomController;
    private Scene waitingRoomScene;

    private ClientGameController clientGameController;

    @Inject
    private ServerUtils server;

    public void initialize(Stage primaryStage, Pair<LoginController, Parent> login,
                           Pair<MainMenuController, Parent> mainMenu,
                           Pair<MultiQuestionController, Parent> multiQuestion,
                           Pair<LeaderboardSoloController, Parent> leaderboardSolo,
                           Pair<EstimateQuestionController, Parent> estimateQuestion,
                           ClientGameController clientGameController, Pair<WaitingRoomController, Parent> waitingRoom ) {
        this.primaryStage = primaryStage;

        this.loginController = login.getKey();
        this.loginScene = new Scene(login.getValue());

        this.mainMenuController = mainMenu.getKey();
        this.mainMenuScene = new Scene(mainMenu.getValue());

        this.multiCtrl = multiQuestion.getKey();
        this.multiScene = new Scene(multiQuestion.getValue());

        this.leaderboardSoloController = leaderboardSolo.getKey();
        this.leaderboardSoloScene = new Scene(leaderboardSolo.getValue());

        this.estimateQuestionController = estimateQuestion.getKey();
        this.estimateQuestionScene = new Scene(estimateQuestion.getValue());

        this.waitingRoomController = waitingRoom.getKey();
        this.waitingRoomScene = new Scene(waitingRoom.getValue());

        this.clientGameController = clientGameController;

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
        leaderboardSoloController.resetState();
        leaderboardSoloController.refresh();
        leaderboardSoloController.showEntries();
        primaryStage.setScene(leaderboardSoloScene);
        leaderboardSoloScene.widthProperty().addListener(e -> {
            leaderboardSoloController.resizeWidth(leaderboardSoloScene.getWidth());
        });
        leaderboardSoloScene.heightProperty().addListener(e -> {
            leaderboardSoloController.resizeHeight(leaderboardSoloScene.getHeight());
        });
    }

    public void showLeaderboardMulti() {
        primaryStage.setTitle("Quizzzz!");
        leaderboardSoloController.resetState();
        primaryStage.setScene(leaderboardSoloScene);
        leaderboardSoloScene.widthProperty().addListener(e -> {
            leaderboardSoloController.resizeWidth(leaderboardSoloScene.getWidth());
        });
        leaderboardSoloScene.heightProperty().addListener(e -> {
            leaderboardSoloController.resizeHeight(leaderboardSoloScene.getHeight());
        });
    }

    public void showWaitingRoom() {
        primaryStage.setTitle("Quizzzz!");
        waitingRoomController.resetState();
        primaryStage.setScene(waitingRoomScene);

    }

    public void showEstimate(){
        primaryStage.setTitle("Quizzzz!");
        primaryStage.setScene(estimateQuestionScene);
        estimateQuestionScene.widthProperty().addListener(e -> {
            estimateQuestionController.resize(estimateQuestionScene.getWidth(), estimateQuestionScene.getHeight());
        });
        estimateQuestionScene.heightProperty().addListener(e -> {
            estimateQuestionController.resize(estimateQuestionScene.getWidth(), estimateQuestionScene.getHeight());
        });
    }

    public void showLogin() {
        primaryStage.setTitle("Quizzzz!");
        primaryStage.setScene(loginScene);
        loginScene.widthProperty().addListener(e -> {
            loginController.resizeWidth(loginScene.getWidth());
        });
        loginScene.heightProperty().addListener(e -> {
            loginController.resizeHeight(loginScene.getHeight());
        });
    }

    public void showMainMenu() {
        primaryStage.setScene(mainMenuScene);
        mainMenuController.makeAnimations();
    }

    public void showMultiQuestion() {
        primaryStage.setScene(multiScene);
    }


    public void joinGame(boolean isMulti) {
        clientGameController.startPolling(isMulti);
    }

    public void quit() throws IOException {
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

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}