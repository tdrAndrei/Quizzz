package client.scenes;


import client.utils.ServerUtils;
import commons.Messages.Message;
import javafx.scene.Parent;
import javafx.util.Pair;

import java.util.Timer;
import java.util.TimerTask;

public class ClientGameController {

    private MultiQuestionController multiQuestionController;
    private EstimateQuestionController estimateQuestionController;
    private Long gameId;


    private final MainCtrl mainController;
    private final ServerUtils serverUtils;

    @javax.inject.Inject
    public ClientGameController(MainCtrl mainController, ServerUtils serverUtils) {
        this.mainController = mainController;
        this.serverUtils = serverUtils;
    }

    public void initialize(Pair<MultiQuestionController, Parent> multiQuestion,
                           Pair<EstimateQuestionController, Parent> estimateQuestion) {
        this.multiQuestionController = multiQuestion.getKey();
        this.estimateQuestionController = estimateQuestion.getKey();
    }

    public void startPolling() {
        gameId = serverUtils.joinSolo(mainController.getUser());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
                Message message = serverUtils.pollUpdate(gameId, mainController.getUser().getId());
                interpretMessage(message);
            }
        } , 0, 500);
    }

    public void interpretMessage(Message message) {
        switch (message.getType()) {
            case "None":
                break;
            case "NewPlayers":
                break;
            case "NewQuestionMC":
                break;
            case "NewQuestionEstimate":
                break;
            case "ShowLeaderBoard":
                break;
            case "EndGame":
                break;
            case "ShowCorrectAnswer":
                break;
            default:
        }


        }

    }

