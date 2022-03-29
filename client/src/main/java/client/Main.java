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
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import com.google.inject.Injector;
import client.scenes.MainCtrl;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        var login = FXML.load(LoginController.class, "client", "scenes", "Login.fxml");
        var mainMenu = FXML.load(MainMenuController.class, "client", "scenes", "main.fxml");
        var multiQuestion = FXML.load(MultiQuestionController.class, "client", "scenes", "multiQuestion.fxml");
        //var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        var leaderboardSolo = FXML.load(LeaderboardSoloController.class, "client", "scenes", "LeaderboardSolo.fxml");
        var estimateQuestion = FXML.load(EstimateQuestionController.class, "client", "scenes", "estimateQuestion.fxml");
        var compareQuestion = FXML.load(CompareQuestionController.class, "client", "scenes", "compareQuestion.fxml");
        var waitingRoom = FXML.load(WaitingRoomController.class, "client", "scenes", "waitingRoom.fxml");
        var clientGameController = INJECTOR.getInstance(ClientGameController.class);
        clientGameController.initialize(multiQuestion, estimateQuestion, compareQuestion, leaderboardSolo, waitingRoom);
        mainCtrl.initialize(primaryStage, login, mainMenu, multiQuestion, compareQuestion, leaderboardSolo, estimateQuestion, clientGameController, waitingRoom);

    }

}