package client.scenes;

import client.utils.ServerUtils;
import commons.Messages.*;
import javafx.animation.PathTransition;
import commons.Player;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;
import javafx.util.Pair;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGameController {

    private GameState status;
    private GameState gameMode;

    public enum GameState {
        NOTPLAYING,
        WAITINGAREA,
        MULTI,
        SOLO,
        NEWQUESTION,
        SHOWANSWER,
        SHOWLEADERBOARD;
    }

    public GameState getStatus() {
        return status;
    }

    public void setStatus(GameState status) {
        this.status = status;
    }

    private MultiQuestionController multiQuestionController;
    private EstimateQuestionController estimateQuestionController;
    private LeaderboardSoloController leaderboardSoloController;
    private WaitingRoomController waitingRoomController;
    private Long gameId;
    private Color[] timebarColors;
    private boolean usedTimeJokerForCurrentQ;
    private Timer timer;

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
        timebarColors = new Color[]{Color.green, Color.yellow, Color.red};
        timer = new Timer();
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

        enableAllJokers();

        if (isMulti) {

            this.gameMode = GameState.MULTI;

            multiQuestionController.resetMulti();
            estimateQuestionController.resetMulti();

            //multiQuestionController.setMulti(true);
            //estimateQuestionController.setMulti(true);

            gameId = serverUtils.joinMulti(mainController.getUser());
            mainController.showWaitingRoom();
            waitingRoomController.setGameId(gameId);
        } else {

            this.gameMode = GameState.SOLO;

            multiQuestionController.resetSolo();
            estimateQuestionController.resetSolo();
            //multiQuestionController.setMulti(false);
            //estimateQuestionController.setMulti(false);
            gameId = serverUtils.joinSolo(mainController.getUser());

        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
                if (gameMode == GameState.NOTPLAYING) {
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
                    gameMode = GameState.NOTPLAYING;
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

                status = GameState.NEWQUESTION;

                disableJokerUsage = false;
                setUsedTimeJokerForCurrentQ(false);
                Platform.runLater(() -> {
                    switch (newQuestionMessage.getQuestionType()) {
                        case "MC": prepareMCQ(newQuestionMessage); break;
                        case "Estimate": prepareEstimateQ(newQuestionMessage); break;
                    }
                });
                break;

            case "ShowLeaderboard":
                ShowLeaderboardMessage showLeaderboardMessage = (ShowLeaderboardMessage) message;
                status = GameState.SHOWLEADERBOARD;
                prepareLeaderboard(showLeaderboardMessage);
                break;

            case "ShowCorrectAnswer":
                CorrectAnswerMessage correctAnswerMessage = (CorrectAnswerMessage) message;
                status = GameState.SHOWANSWER;
                disableJokerUsage = true;
                Platform.runLater(() -> {
                    estimateQuestionController.showAnswer(correctAnswerMessage);
                    multiQuestionController.showAnswer(correctAnswerMessage);
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
        }
    }

    public void exitGame() {
        gameMode = GameState.NOTPLAYING;
        timer.cancel();
        serverUtils.leaveGame(this.getGameId(), mainController.getUser().getId());
    }

    public boolean isPlaying() {
        return gameMode != GameState.NOTPLAYING;
    }

    public Long getGameId() {
        return gameId;
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
        timer.cancel();
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

    public void updateProgressBarColor(double timeLeft, double maxTime, ProgressBar progressBar){

        float[] newComponents = new float[3];
        float[] upperColor = new float[3];
        float[] lowerColor = new float[3];
        double percent = 1;

        if (timeLeft >= maxTime/2) {
            percent = 1 - (maxTime - timeLeft)/(maxTime/2);
            upperColor = timebarColors[0].getRGBColorComponents(new float[3]);
            lowerColor = timebarColors[1].getRGBColorComponents(new float[3]);
        } else {
            percent = 1 - (maxTime / 2 - timeLeft)/(maxTime/2);
            upperColor = timebarColors[1].getRGBColorComponents(new float[3]);
            lowerColor = timebarColors[2].getRGBColorComponents(new float[3]);
        }

        for (int i = 0; i < 3; i ++) {
            newComponents[i] = (float) (percent) * upperColor[i] + (float) (1 - percent) * lowerColor[i];
        }

        Color newColor = new Color(newComponents[0], newComponents[1], newComponents[2]);
        progressBar.setStyle("-fx-accent: rgb(" + newColor.getRed() + ", " +
                                                            newColor.getGreen() + ", " +
                                                            newColor.getBlue() + ");");
    }

    public void startTimer(ProgressBar progressBar, Label timeText){

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double maxTime = getMaxTime();
                double timeLeft = getTimeLeft();
                updateTimeLeft(0.1, timeLeft);
                Platform.runLater(() -> updateProgressBar(timeLeft, maxTime, progressBar, timeText));
            }
        }, 0, 100);

    }

    public void updateProgressBar(double timeLeft, double maxTime, ProgressBar progressBar, Label timer){
        if (timeLeft >= 0){
            progressBar.setProgress(timeLeft/maxTime);
            int displayText = (int) Math.round(getTimeLeft());
            timer.setText(displayText + "S");
            updateProgressBarColor(timeLeft, maxTime, progressBar);
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
        multiQuestionController.setChosenAnswer(-1);
        setMaxTime(newQuestionMessage.getTime());
        setTimeLeft(newQuestionMessage.getTime());
        multiQuestionController.setJokersPic();
        multiQuestionController.enableSubmittingAnswers();
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

    public void changeScore(int score, Label pointsLabel, Label newPoints) {

        String[] string = pointsLabel.getText().split(" ");

        int currScore = Integer.parseInt(string[0]);
        int pointsAdded = score - currScore;

        if (isUsedTimeJokerForCurrentQ())
            newPoints.setText(" + 2x " + pointsAdded / 2);
        else
            newPoints.setText(" + " + pointsAdded);

        if (pointsAdded == 0)
            newPoints.setStyle("-fx-text-fill: rgb(255,0,0);");
        else
            newPoints.setStyle("-fx-text-fill: rgb(0, 210, 28);");

        pointsLabel.setText(currScore + " pts");

        Path moveVertically = new Path();
        moveVertically.getElements().add(new MoveTo(0, 0));
        moveVertically.getElements().add(new VLineTo(-100));

        PathTransition fadeOut = new PathTransition(Duration.seconds(3), moveVertically, newPoints);
        fadeOut.play();

        pointsLabel.setText(score + " pts");

    }

    public void setUsedTimeJokerForCurrentQ(boolean usedTimeJokerForCurrentQ) {
        this.usedTimeJokerForCurrentQ = usedTimeJokerForCurrentQ;
    }

    public boolean isUsedTimeJokerForCurrentQ() {
        return usedTimeJokerForCurrentQ;
    }

    public Timer getTimer() {
        return timer;
    }

}

