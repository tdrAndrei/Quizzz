package client.scenes;


import client.utils.ServerUtils;
import commons.Messages.*;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.util.Pair;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGameController {

    private MultiQuestionController multiQuestionController;
    private EstimateQuestionController estimateQuestionController;
    private LeaderboardSoloController leaderboardSoloController;
    private WaitingRoomController waitingRoomController;
    private Long gameId;

    private boolean isPlaying = true;

    private final MainCtrl mainController;
    private final ServerUtils serverUtils;

    private boolean timeJokerUsed = false;
    private boolean eliminateJokerUsed = false;
    private boolean doublePointsJokerUsed = false;
    private boolean disableJokerUsage = false;
    private Image usedJoker = new Image("/client.photos/usedJoker.png");

    @javax.inject.Inject
    public ClientGameController(MainCtrl mainController, ServerUtils serverUtils) {
        this.mainController = mainController;
        this.serverUtils = serverUtils;
    }

    public void initialize(Pair<MultiQuestionController, Parent> multiQuestion,
                           Pair<EstimateQuestionController, Parent> estimateQuestion,
                           Pair<LeaderboardSoloController, Parent> leaderboard,
                           Pair<WaitingRoomController, Parent> waitingRoom) {
        this.multiQuestionController = multiQuestion.getKey();
        this.estimateQuestionController = estimateQuestion.getKey();
        this.leaderboardSoloController = leaderboard.getKey();
        this.waitingRoomController = waitingRoom.getKey();
    }

    public void startPolling(boolean isMulti) {
        isPlaying = true;
        multiQuestionController.reset();
        estimateQuestionController.reset();
        if (isMulti) {
            gameId = serverUtils.joinMulti(mainController.getUser());
            mainController.showWaitingRoom();
        } else {
            gameId = serverUtils.joinSolo(mainController.getUser());
            mainController.showMultiQuestion();
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
                if (!isPlaying) {
                    timer.cancel();
                    return;
                }
                Message message = serverUtils.pollUpdate(gameId, mainController.getUser().getId());
                System.out.println(message);
                try {
                    interpretMessage(message);
                } catch (InterruptedException ignored) {
                }
                if (message instanceof ShowLeaderboardMessage && ((ShowLeaderboardMessage) message).getGameProgress().equals("End")) {
                    timer.cancel();
                }
            }
        } , 0, 500);
    }

    public void interpretMessage(Message message) throws InterruptedException {
        switch (message.getType()) {
            case "None":
                NullMessage nullMessage = (NullMessage) message;
                break;
            case "NewPlayers":
                NewPlayersMessage newPlayersMessage = (NewPlayersMessage) message;
                break;
            case "NewQuestion":
                NewQuestionMessage newQuestionMessage = (NewQuestionMessage) message;
                disableJokerUsage = false;
                if (newQuestionMessage.getQuestionType().equals("MC")) {
                    Platform.runLater(() -> {
                        mainController.showMultiQuestion();
                        multiQuestionController.showQuestion(newQuestionMessage);
                        multiQuestionController.setQuestions(newQuestionMessage.getActivities());
                    });
                } else if (newQuestionMessage.getQuestionType().equals("Estimate")) {
                    Platform.runLater(() -> {
                        mainController.showEstimate();
                        estimateQuestionController.showQuestion(newQuestionMessage);
                    });
                }
                break;
            case "ShowLeaderboard":
                ShowLeaderboardMessage showLeaderboardMessage = (ShowLeaderboardMessage) message;
                if (showLeaderboardMessage.getGameProgress().equals("End")) {
                    Platform.runLater(() -> {
                        mainController.showLeaderboardSolo();
                        Long myEntryId = showLeaderboardMessage.getEntryId();
                        leaderboardSoloController.showMine(myEntryId);
                    });
                } else if (showLeaderboardMessage.getGameProgress().equals("Mid")) {
                    Platform.runLater(() -> {
                    //
                    });
                }
                break;
            case "ShowCorrectAnswer":
                CorrectAnswerMessage correctAnswerMessage = (CorrectAnswerMessage) message;
                disableJokerUsage = true;
                Platform.runLater(() -> {
                    estimateQuestionController.showAnswer(correctAnswerMessage);
                    multiQuestionController.showAnswer(correctAnswerMessage);
                    multiQuestionController.changeScore(correctAnswerMessage.getScore());
                });
                break;
            default:
        }
    }

    public void exitGame() {
        isPlaying = false;
        serverUtils.leaveGame(this.getGameId(), mainController.getUser().getId());
    }

    public Long getGameId() {
        return gameId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void submitAnswer(long answer) {
        serverUtils.submitAnswer(getGameId(), mainController.getUser().getId(), answer);
    }

    public void doublePoint() {
        serverUtils.useDoublePointsJoker(getGameId(), mainController.getUser().getId());
    }

    public long eliminateJoker() {
        return serverUtils.eliminateJoker(getGameId());
    }

    public void enableAllJokers() {
        timeJokerUsed = false;
        eliminateJokerUsed = false;
        doublePointsJokerUsed = false;
    }

    public boolean isTimeJokerUsed() {
        return timeJokerUsed;
    }

    public void setTimeJokerUsed(boolean timeJokerUsed) {
        this.timeJokerUsed = timeJokerUsed;
    }

    public boolean isEliminateJokerUsed() {
        return eliminateJokerUsed;
    }

    public void setEliminateJokerUsed(boolean eliminateJokerUsed) {
        this.eliminateJokerUsed = eliminateJokerUsed;
    }

    public boolean isDoublePointsJokerUsed() {
        return doublePointsJokerUsed;
    }

    public void setDoublePointsJokerUsed(boolean doublePointsJokerUsed) {
        this.doublePointsJokerUsed = doublePointsJokerUsed;
    }

    public boolean isDisableJokerUsage() {
        return disableJokerUsage;
    }

    public void setDisableJokerUsage(boolean disableJokerUsage) {
        this.disableJokerUsage = disableJokerUsage;
    }

    public Image getUsedJoker() {
        return usedJoker;
    }

}

