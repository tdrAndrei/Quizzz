package client.scenes;

import client.utils.ServerUtils;
import commons.Messages.*;
import commons.Player;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGameController {

    private MultiQuestionController multiQuestionController;
    private EstimateQuestionController estimateQuestionController;
    private LeaderboardSoloController leaderboardSoloController;
    private WaitingRoomController waitingRoomController;
    private Long gameId;

    private boolean isPlaying = false;

    private final MainCtrl mainController;
    private final ServerUtils serverUtils;

    private boolean skipQuestionJokerUsed = false;
    private boolean eliminateJokerUsed = false;
    private boolean doublePointsJokerUsed = false;
    private boolean disableJokerUsage = false;

    private Image usedJoker = new Image("/client.photos/usedJoker.png");

    private double maxTime;
    private double timeLeft;

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
        enableAllJokers();
        if (isMulti) {
            multiQuestionController.resetMulti();
            estimateQuestionController.resetMulti();
            multiQuestionController.setMulti(true);
            estimateQuestionController.setMulti(true);
            gameId = serverUtils.joinMulti(mainController.getUser());

            serverUtils.getAndSetSession();  // Getting a websocket connection
            serverUtils.registerForMessages(gameId); //Using our connection to subscribe to "/topic/emoji/{gameId}

            mainController.showWaitingRoom();
            waitingRoomController.setGameId(gameId);

            serverUtils.sendEmojiTest(gameId, mainController.getUser().getName(), 6969, mainController.getUser().getId()); //this is for testing and should be removed and abstracted
            serverUtils.sendEmojiTest(gameId, mainController.getUser().getName(), 6969, mainController.getUser().getId()); //this is for testing and should be removed and abstracted

        } else {
            multiQuestionController.resetSolo();
            estimateQuestionController.resetSolo();
            multiQuestionController.setMulti(false);
            estimateQuestionController.setMulti(false);
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
                updateWaitingRoom(newPlayersMessage);
                break;
            case "NewQuestion":
                NewQuestionMessage newQuestionMessage = (NewQuestionMessage) message;
                disableJokerUsage = false;
                if (newQuestionMessage.getQuestionType().equals("MC")) {
                    Platform.runLater(() -> {
                        prepareMCQ(newQuestionMessage);
                    });
                } else if (newQuestionMessage.getQuestionType().equals("Estimate")) {
                    Platform.runLater(() -> {
                        prepareEstimateQ(newQuestionMessage);
                    });
                }
                break;
            case "ShowLeaderboard":
                ShowLeaderboardMessage showLeaderboardMessage = (ShowLeaderboardMessage) message;
                prepareLeaderboard(showLeaderboardMessage);
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
            case "ReduceTime":
                ReduceTimeMessage reduceTimeMessage = (ReduceTimeMessage) message;
                Platform.runLater(() -> {
                    setTimeLeft(reduceTimeMessage.getNewTime());
                    multiQuestionController.showTimeReduced(reduceTimeMessage.getUserName());
                    estimateQuestionController.showTimeReduced(reduceTimeMessage.getUserName());
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

    public void skipQuestion() {
        serverUtils.useNewQuestionJoker(gameId);
    }

    public void timeJoker(long userId) {
        serverUtils.useTimeJoker(gameId, userId);
    }

    public void enableAllJokers() {
        skipQuestionJokerUsed = false;
        eliminateJokerUsed = false;
        doublePointsJokerUsed = false;
    }

    public boolean isSkipQuestionJokerUsed() {
        return skipQuestionJokerUsed;
    }

    public void setSkipQuestionJokerUsed(boolean skipQuestionJokerUsed) {
        this.skipQuestionJokerUsed = skipQuestionJokerUsed;
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

    public double getTimeLeft(){
        return this.timeLeft;
    }

    public void setTimeLeft(double time){
        this.timeLeft = time;
    }

    public double getMaxTime(){
        return this.maxTime;
    }

    public void setMaxTime(double time){
        this.maxTime = time;
    }

    public void updateTimeLeft(double seconds, double timeLeft){
        setTimeLeft(timeLeft - seconds);
    }

    public void updateProgressBar(double timeLeft, double maxTime, ProgressBar progressBar, Label timer){
        if (timeLeft >= 0){
            progressBar.setProgress(timeLeft/maxTime);
            int displayText = (int) Math.round(getTimeLeft());
            timer.setText(displayText + "S");
        } else {
            progressBar.setProgress(0.0);
            int displayText = 0;
            timer.setText(displayText + "S");
        }
    }

    public void prepareLeaderboard(ShowLeaderboardMessage showLeaderboardMessage) {
        boolean isSolo = showLeaderboardMessage.getEntryId() != null;
        if (showLeaderboardMessage.getGameProgress().equals("End")) {
            if (isSolo) {
                Platform.runLater(() -> {
                    mainController.showLeaderboardSolo();
                    Long myEntryId = showLeaderboardMessage.getEntryId();
                    leaderboardSoloController.showMine(myEntryId);
                });
            } else {
                Platform.runLater(() -> {
                    mainController.showLeaderboardMulti();
                    String userName = mainController.getUser().getName();
                    leaderboardSoloController.initMulti(showLeaderboardMessage.getEntries(), userName, "End");
                    leaderboardSoloController.showEntries();
                    leaderboardSoloController.setEndScreen();
                });
            }
        } else if (showLeaderboardMessage.getGameProgress().equals("Mid")) {
            Platform.runLater(() -> {
                mainController.showLeaderboardMulti();
                String userName = mainController.getUser().getName();
                leaderboardSoloController.initMulti(showLeaderboardMessage.getEntries(), userName, "Mid");
                leaderboardSoloController.showEntries();
                leaderboardSoloController.setMidScreen();
            });
        }
    }

    public void prepareMCQ(NewQuestionMessage newQuestionMessage){
        mainController.showMultiQuestion();
        setMaxTime(newQuestionMessage.getTime());
        setTimeLeft(newQuestionMessage.getTime());
        multiQuestionController.setJokersPic();
        multiQuestionController.showQuestion(newQuestionMessage);
        multiQuestionController.setQuestions(newQuestionMessage.getActivities(), newQuestionMessage.getImagesBytes());
    }

    public void prepareEstimateQ(NewQuestionMessage newQuestionMessage){
        mainController.showEstimate();
        setMaxTime(newQuestionMessage.getTime());
        setTimeLeft(newQuestionMessage.getTime());
        estimateQuestionController.setJokersPic();
        estimateQuestionController.showQuestion(newQuestionMessage);
    }
    public void updateWaitingRoom(NewPlayersMessage newPlayersMessage) {
        List<Player> playerList = newPlayersMessage.getPlayerList();
        String userName = mainController.getUser().getName();
        waitingRoomController.showPlayers(playerList);
        waitingRoomController.showEntries();
    }

}

