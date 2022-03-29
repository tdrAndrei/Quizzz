package client.scenes;

import client.EmojiChat;
import client.utils.ServerUtils;
import commons.Messages.*;
import javafx.animation.PathTransition;
import commons.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;
import javafx.util.Pair;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.scene.image.Image;

public class ClientGameController {

    private GameState status;
    private GameMode gameMode;

    public enum GameState {
        NEW_QUESTION,
        SHOW_ANSWER,
        SUBMITTED_ANSWER,
        SHOW_LEADERBOARD
    }

    public enum GameMode {
        NOT_PLAYING,
        MULTI,
        SOLO
    }

    public GameState getStatus() {
        return status;
    }

    public void setStatus(GameState status) {
        this.status = status;
    }

    private MultiQuestionController multiQuestionController;
    private EstimateQuestionController estimateQuestionController;
    private CompareQuestionController compareQuestionController;
    private LeaderboardSoloController leaderboardSoloController;
    private WaitingRoomController waitingRoomController;
    private Long gameId;
    private Color[] timebarColors;
    private boolean doublePointsForThisRound;
    private Timer progressBarThread;
    private final EmojiChat emojiChat;

    private final MainCtrl mainController;
    private final ServerUtils serverUtils;

    private List<Joker> remainingJokers;
    private EnumSet<Joker> availableJokers;

    private double maxTime;
    private double timeLeft;

    @javax.inject.Inject
    public ClientGameController(MainCtrl mainController, ServerUtils serverUtils) {
        this.mainController = mainController;
        this.serverUtils = serverUtils;

        timebarColors = new Color[]{Color.green, Color.yellow, Color.red};
        progressBarThread = new Timer();

        emojiChat = new EmojiChat();
    }

    public void initialize(Pair<MultiQuestionController, Parent> multiQuestion,
                           Pair<EstimateQuestionController, Parent> estimateQuestion,
                           Pair<CompareQuestionController, Parent> compareQuestion,
                           Pair<LeaderboardSoloController, Parent> leaderboard,
                           Pair<WaitingRoomController, Parent> waitingRoom) {
        this.multiQuestionController = multiQuestion.getKey();
        this.estimateQuestionController = estimateQuestion.getKey();
        this.compareQuestionController = compareQuestion.getKey();
        this.leaderboardSoloController = leaderboard.getKey();
        this.waitingRoomController = waitingRoom.getKey();

        emojiChat.addClient(multiQuestionController)
                .addClient(estimateQuestionController)
                .addClient(compareQuestionController);
    }

    public void startPolling(boolean isMulti) {

        if (isMulti) {
            this.gameMode = GameMode.MULTI;
            remainingJokers = List.of(Joker.ELIMINATE, Joker.DOUBLEPOINTS, Joker.REDUCETIME);

            gameId = serverUtils.joinMulti(mainController.getUser());

            serverUtils.getAndSetSession();  // Getting a websocket connection
            serverUtils.registerForEmojiMessages(gameId, emojiChat::emojiHandler); //Using our connection to subscribe to "/topic/emoji/{gameId}
            emojiChat.setVisibility(true);
            emojiChat.resetChat();

            mainController.showWaitingRoom();
            waitingRoomController.setGameId(gameId);

        } else {
            this.gameMode = GameMode.SOLO;
            remainingJokers = List.of(Joker.ELIMINATE, Joker.DOUBLEPOINTS, Joker.SKIPQUESTION);

            gameId = serverUtils.joinSolo(mainController.getUser());
            emojiChat.setVisibility(false);
        }

        availableJokers = EnumSet.copyOf(remainingJokers);
        mainController.resetQuestionScenes(remainingJokers);


        Timer pollingThread = new Timer();
        pollingThread.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
            if (gameMode == GameMode.NOT_PLAYING) {
                pollingThread.cancel();
                return;
            }
            Message message = serverUtils.pollUpdate(gameId, mainController.getUser().getId());
            try {
                interpretMessage(message);
            } catch (InterruptedException ignored) {
            }
            if (message instanceof ShowLeaderboardMessage && ((ShowLeaderboardMessage) message).getGameProgress().equals("End")) {
                pollingThread.cancel();
                gameMode = GameMode.NOT_PLAYING;
            }
            }
        } , 0, 500);
    }

    public void sendEmoji(int emojiIndex) {
        serverUtils.sendEmojiTest(gameId, mainController.getUser().getName(), emojiIndex, mainController.getUser().getId());
    }

    public void interpretMessage(Message message) throws InterruptedException {
        switch (message.getType()) {
            case "None":
                break;

            case "NewPlayers":
                NewPlayersMessage newPlayersMessage = (NewPlayersMessage) message;
                updateWaitingRoom(newPlayersMessage);
                break;

            case "NewQuestion":
                NewQuestionMessage newQuestionMessage = (NewQuestionMessage) message;

                status = GameState.NEW_QUESTION;

                remainingJokers.forEach(joker -> {
                    if (joker != Joker.USED)
                        availableJokers.add(joker);
                });
                
                setDoublePointsForThisRound(false);
                Platform.runLater(() -> {
                    switch (newQuestionMessage.getQuestionType()) {
                        case "MC": mainController.showMultiQuestion(); break;
                        case "Estimate": mainController.showEstimate(); break;
                        case "Compare": mainController.showCompare(); break;
                    }

                    setMaxTime(newQuestionMessage.getTime());
                    setTimeLeft(newQuestionMessage.getTime());
                    mainController.prepareCurrentScene(newQuestionMessage);
                });
                break;

            case "ShowLeaderboard":
                ShowLeaderboardMessage showLeaderboardMessage = (ShowLeaderboardMessage) message;
                status = GameState.SHOW_LEADERBOARD;
                prepareLeaderboard(showLeaderboardMessage);
                break;

            case "ShowCorrectAnswer":
                CorrectAnswerMessage correctAnswerMessage = (CorrectAnswerMessage) message;
                status = GameState.SHOW_ANSWER;
                progressBarThread.cancel();

                availableJokers.clear();

                Platform.runLater(() -> {
                    mainController.showAnswerInCurrentScene(correctAnswerMessage);
                });
                break;

            case "ReduceTime":
                ReduceTimeMessage reduceTimeMessage = (ReduceTimeMessage) message;
                Platform.runLater(() -> {
                    setTimeLeft(reduceTimeMessage.getNewTime());
                    mainController.showTimeReducedInCurrentScene(reduceTimeMessage.getUserName());
                });
                break;
        }
    }

    public void useJoker(Joker joker, Runnable onSuccess) {
        if ( availableJokers.contains(joker) ) {

            availableJokers.remove(joker);
            remainingJokers = remainingJokers.stream().map(x -> {
               if (x == joker)
                   return Joker.USED;
               else
                   return x;
            }).collect(Collectors.toList());
            
            mainController.setJokerPics(remainingJokers);
            onSuccess.run();
        }
    }

    public void exitGame() {
        gameMode = GameMode.NOT_PLAYING;
        progressBarThread.cancel();
        serverUtils.leaveGame(this.getGameId(), mainController.getUser().getId());
        mainController.showMainMenu();
    }

    public boolean isPlaying() {
        return gameMode != GameMode.NOT_PLAYING;
    }

    public Long getGameId() {
        return gameId;
    }

    public void submitAnswer(long answer) {
        if (status != GameState.NEW_QUESTION || timeLeft <= 0)
            return;

        status = GameState.SUBMITTED_ANSWER;
        mainController.lockCurrentScene();
        serverUtils.submitAnswer(getGameId(), mainController.getUser().getId(), answer);
        availableJokers.remove(Joker.ELIMINATE);
    }

    public void doublePoint() {
        serverUtils.useDoublePointsJoker(gameId, mainController.getUser().getId());
        setDoublePointsForThisRound(true);
    }

    public long eliminateJoker() {
        return serverUtils.eliminateJoker(gameId);
    }

    public void skipQuestion() {
        serverUtils.useNewQuestionJoker(gameId);
        progressBarThread.cancel();
    }

    public void timeJoker() {
        serverUtils.useTimeJoker(gameId, mainController.getUser().getId());
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
        float[] upperColor;
        float[] lowerColor;
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
        progressBarThread = new Timer();
        progressBarThread.scheduleAtFixedRate(new TimerTask() {
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
            mainController.lockCurrentScene();
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

    public void updateWaitingRoom(NewPlayersMessage newPlayersMessage) {
        List<Player> playerList = newPlayersMessage.getPlayerList();
        waitingRoomController.showPlayers(playerList);
        waitingRoomController.showEntries();
    }

    public void changeScore(int score, Label pointsLabel, Label newPoints) {

        String[] string = pointsLabel.getText().split(" ");

        int currScore = Integer.parseInt(string[0]);
        int pointsAdded = score - currScore;

        if (hasDoublePointsForThisRound())
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

        mainController.setPointsForAllScenes(score);
    }

    public void setDoublePointsForThisRound(boolean doublePointsForThisRound) {
        this.doublePointsForThisRound = doublePointsForThisRound;
    }

    public boolean hasDoublePointsForThisRound() {
        return doublePointsForThisRound;
    }

    public Timer getTimer() {
        return progressBarThread;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public List<Joker> getRemainingJokers() {
        return remainingJokers;
    }

    public EnumSet<Joker> getAvailableJokers() {
        return availableJokers;
    }

}

