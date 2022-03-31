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
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.NewQuestionMessage;
import commons.User;
import javafx.animation.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;


import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class MainCtrl {

    private Stage primaryStage;
    private User user;

    private LoginController loginController;
    private Scene loginScene;

    private MainMenuController mainMenuController;
    private Scene mainMenuScene;

    private MultiQuestionController multiCtrl;
    private Scene multiScene;

    private ChooseConsumptionController chooseConsumptionController;
    private Scene chooseConsumptionScene;

    private LeaderboardSoloController leaderboardSoloController;
    private Scene leaderboardSoloScene;

    private AdminController adminController;
    private Scene adminScene;

    private EstimateQuestionController estimateQuestionController;
    private Scene estimateQuestionScene;

    private WaitingRoomController waitingRoomController;
    private Scene waitingRoomScene;

    private AddActivityController addActivityController;
    private Scene addActivityScene;

    private EditActivityController editActivityController;
    private Scene editActivityScene;

    private ClientGameController clientGameController;

    private List<QuestionScene> questionControllers;
    private QuestionScene currentSceneController;

    @Inject
    private ServerUtils server;

    public void initialize(Stage primaryStage, Pair<LoginController, Parent> login,
                           Pair<MainMenuController, Parent> mainMenu,
                           Pair<MultiQuestionController, Parent> multiQuestion,
                           Pair<ChooseConsumptionController, Parent> chooseConsumptionQuestion,
                           Pair<LeaderboardSoloController, Parent> leaderboardSolo,
                           Pair<EstimateQuestionController, Parent> estimateQuestion,
                           ClientGameController clientGameController, Pair<WaitingRoomController, Parent> waitingRoom, Pair<AdminController, Parent> adminController,
                           Pair<AddActivityController, Parent> addActivity,
                           Pair<EditActivityController, Parent> editActivity) {

        this.primaryStage = primaryStage;

        this.loginController = login.getKey();
        this.loginScene = new Scene(login.getValue());

        this.mainMenuController = mainMenu.getKey();
        this.mainMenuScene = new Scene(mainMenu.getValue());

        this.multiCtrl = multiQuestion.getKey();
        this.multiScene = new Scene(multiQuestion.getValue());

        this.chooseConsumptionController = chooseConsumptionQuestion.getKey();
        this.chooseConsumptionScene = new Scene(chooseConsumptionQuestion.getValue());

        this.leaderboardSoloController = leaderboardSolo.getKey();
        this.leaderboardSoloScene = new Scene(leaderboardSolo.getValue());

        this.estimateQuestionController = estimateQuestion.getKey();
        this.estimateQuestionScene = new Scene(estimateQuestion.getValue());

        this.waitingRoomController = waitingRoom.getKey();
        this.waitingRoomScene = new Scene(waitingRoom.getValue());

        this.adminController = adminController.getKey();
        this.adminScene = new Scene(adminController.getValue());

        this.addActivityController = addActivity.getKey();
        this.addActivityScene = new Scene(addActivity.getValue());

        this.editActivityController = editActivity.getKey();
        this.editActivityScene = new Scene(editActivity.getValue());

        this.clientGameController = clientGameController;

        questionControllers = List.of(multiCtrl, estimateQuestionController, chooseConsumptionController);

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

    public void resetQuestionScenes(List<Joker> jokers) {
        setJokerPics(jokers);
        setPointsForAllScenes(0);
        for ( QuestionScene controller : questionControllers ) {
            controller.getQuestionsLeftLabel().setText(String.valueOf(clientGameController.getQuestionsLeft()));
            //additional logic that can't be generalized is done in this method
            //You need to override it in the specific controller
            controller.reset();
        }
    }

    public void setPointsForAllScenes(int score) {
        questionControllers.forEach( q -> q.getPointsLabel().setText(score + " pts"));
    }

    public void setJokerPics(List<Joker> jokers) {
        for ( QuestionScene controller : questionControllers ) {
            //Setting the joker images for all scenes
            int index = 0;
            for (Joker joker : jokers) {
                controller.getJokerPics().get(index).setImage(new Image(joker.getPath()));
                index++;
            }
        }
    }

    public void lockCurrentScene() {
        currentSceneController.lockAnswer();
    }

    public void prepareCurrentScene(NewQuestionMessage newQuestionMessage, int questionsLeft) {
        currentSceneController.showQuestion(newQuestionMessage);
        questionControllers.forEach(controller -> {
            Label target = controller.getQuestionsLeftLabel();
            Timeline animate = new Timeline(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(target.scaleXProperty(), 1),
                            new KeyValue(target.scaleYProperty(), 1)
                    ),
                    new KeyFrame(
                            Duration.seconds(0.4),
                            e -> target.setText(String.valueOf(questionsLeft)),
                            new KeyValue(target.scaleXProperty(), 1.3, Interpolator.EASE_OUT),
                            new KeyValue(target.scaleYProperty(), 1.3, Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(
                            Duration.seconds(0.6),
                            new KeyValue(target.scaleXProperty(), 1.3, Interpolator.EASE_OUT),
                            new KeyValue(target.scaleYProperty(), 1.3, Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(
                            Duration.seconds(0.9),
                            new KeyValue(target.scaleXProperty(), 1, Interpolator.EASE_IN),
                            new KeyValue(target.scaleYProperty(), 1, Interpolator.EASE_IN)
                    )
            );
            animate.playFromStart();
        });
    }

    public void showAnswerInCurrentScene(CorrectAnswerMessage correctAnswerMessage) {
        currentSceneController.showAnswer(correctAnswerMessage);
    }

    public void showTimeReducedInCurrentScene(String name) {
        currentSceneController.showTimeReduced(name);
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

    public void showAdmin() {
        primaryStage.setTitle("Quizzzz!");
        adminController.refresh();
        primaryStage.setScene(adminScene);
    }

    public void showAddActivity() {
        primaryStage.setTitle("Quizzzz!");
        primaryStage.setScene(addActivityScene);
    }

    public void showEditActivity(long id){
        editActivityController.setCurrent(server.getActivityById(id));
        editActivityController.resetBoxes();
        primaryStage.setTitle("Quizzzz!");
        primaryStage.setScene(editActivityScene);
    }

    public void showEstimate(){
        currentSceneController = estimateQuestionController;
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
        currentSceneController = multiCtrl;
        primaryStage.setScene(multiScene);
    }

    public void showChooseConsumption() {
        currentSceneController = chooseConsumptionController;
        primaryStage.setScene(chooseConsumptionScene);
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
            if (clientGameController.isPlaying()) {
                clientGameController.exitGame();
            }
            if (user != null) {
                server.deleteSelf(user);
            }
            primaryStage.close();
            System.exit(0);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}