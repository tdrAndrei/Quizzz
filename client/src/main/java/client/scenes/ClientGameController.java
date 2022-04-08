package client.scenes;

import client.EmojiChat;
import client.utils.JavaFXUtility;
import client.utils.ServerUtils;
import commons.Messages.*;
import commons.Player;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Pair;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    private ChooseConsumptionController chooseConsumptionController;
    private LeaderboardSoloController leaderboardSoloController;
    private WaitingRoomController waitingRoomController;
    private Long gameId;
    private Color[] timebarColors;
    private boolean doublePointsForThisRound;
    private Timer progressBarThread;
    private EmojiChat emojiChat;
    private JavaFXUtility javaFXUtility;

    private final MainCtrl mainController;
    private final ServerUtils serverUtils;

    private List<Joker> remainingJokers;
    private EnumSet<Joker> availableJokers;

    private double maxTime;
    private double timeLeft;
    private int questionsLeft;
    private int questionsWithoutAnswer;

    @javax.inject.Inject
    public ClientGameController(MainCtrl mainController, ServerUtils serverUtils, EmojiChat emojiChat, JavaFXUtility javaFXUtility) {
        this.mainController = mainController;
        this.serverUtils = serverUtils;

        timebarColors = new Color[]{Color.green, Color.yellow, Color.red};
        progressBarThread = new Timer();

        this.emojiChat = emojiChat;
        this.javaFXUtility = javaFXUtility;
        this.gameMode = GameMode.NOT_PLAYING;
    }

    public void initialize(Pair<MultiQuestionController, Parent> multiQuestion,
                           Pair<EstimateQuestionController, Parent> estimateQuestion,
                           Pair<ChooseConsumptionController, Parent> chooseConsumption,
                           Pair<LeaderboardSoloController, Parent> leaderboard,
                           Pair<WaitingRoomController, Parent> waitingRoom) {
        this.multiQuestionController = multiQuestion.getKey();
        this.estimateQuestionController = estimateQuestion.getKey();
        this.chooseConsumptionController = chooseConsumption.getKey();
        this.leaderboardSoloController = leaderboard.getKey();
        this.waitingRoomController = waitingRoom.getKey();

        emojiChat.addClient(multiQuestionController)
                .addClient(estimateQuestionController)
                .addClient(chooseConsumptionController);
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

        questionsLeft = serverUtils.getNumberOfQuestionsInGame(gameId);
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
                gameMode = GameMode.NOT_PLAYING;
                pollingThread.cancel();
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
                List<Player> playerList = newPlayersMessage.getPlayerList();
                waitingRoomController.showPlayers(playerList);
                waitingRoomController.showEntries();
                break;

            case "NewQuestion":
                NewQuestionMessage newQuestionMessage = (NewQuestionMessage) message;

                status = GameState.NEW_QUESTION;
                determineAvailableJokers();

                setDoublePointsForThisRound(false);
                javaFXUtility.queueJFXThread(() -> {
                    switch (newQuestionMessage.getQuestionType()) {
                        case "MC":
                        case "Compare":
                            mainController.showMultiQuestion(); break;
                        case "Estimate": mainController.showEstimate(); break;
                        case "ChooseConsumption": mainController.showChooseConsumption(); break;
                    }

                    setMaxTime(newQuestionMessage.getTime());
                    setTimeLeft(newQuestionMessage.getTime());
                    mainController.prepareCurrentScene(newQuestionMessage, questionsLeft);
                });
                break;

            case "ShowLeaderboard":
                ShowLeaderboardMessage showLeaderboardMessage = (ShowLeaderboardMessage) message;
                status = GameState.SHOW_LEADERBOARD;
                prepareLeaderboard(showLeaderboardMessage);
                break;

            case "ShowCorrectAnswer":
                CorrectAnswerMessage correctAnswerMessage = (CorrectAnswerMessage) message;
                if (status != GameState.SUBMITTED_ANSWER)
                    incrementNotAnswered();

                status = GameState.SHOW_ANSWER;
                progressBarThread.cancel();
                availableJokers.clear();
                javaFXUtility.queueJFXThread(() -> {
                    mainController.showAnswerInCurrentScene(correctAnswerMessage);
                });
                questionsLeft --;
                break;

            case "ReduceTime":
                ReduceTimeMessage reduceTimeMessage = (ReduceTimeMessage) message;
                availableJokers.remove(Joker.REDUCETIME);   ///If time is reduced for me, I cannot use time joker this  round
                javaFXUtility.queueJFXThread(() -> {
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

    public void determineAvailableJokers() {
        remainingJokers.forEach(joker -> {
            if (joker != Joker.USED)
                availableJokers.add(joker);
        });
    }

    public void exitGame() {
        gameMode = GameMode.NOT_PLAYING;
        progressBarThread.cancel();
        if (this.getGameId() != null){
            serverUtils.leaveGame(this.getGameId(), mainController.getUser().getId());
        }
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
        resetNotAnswered();
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
        javaFXUtility.setStyle(progressBar, "-fx-accent: rgb(" + newColor.getRed() + ", " +
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
                javaFXUtility.queueJFXThread(() -> {
                    updateProgressBar(timeLeft, maxTime, progressBar, timeText);
                });
            }
        }, 0, 100);
    }

    public void updateProgressBar(double timeLeft, double maxTime, ProgressBar progressBar, Label timer){
        if (timeLeft > 0){
            javaFXUtility.setProgress(progressBar, timeLeft/maxTime);
            int displayText = (int) Math.round(timeLeft);
            javaFXUtility.setText(timer, displayText + "S");
            updateProgressBarColor(timeLeft, maxTime, progressBar);
        } else {
            javaFXUtility.setProgress(progressBar, 0.0);
            mainController.lockCurrentScene();
            int displayText = 0;
            javaFXUtility.setText(timer, displayText + "S");
        }
    }

    public void prepareLeaderboard(ShowLeaderboardMessage showLeaderboardMessage) {
        boolean isSolo = showLeaderboardMessage.getEntryId() != null;
        if (showLeaderboardMessage.getGameProgress().equals("End")) {
            if (isSolo) {
                javaFXUtility.queueJFXThread(() -> {
                    mainController.showLeaderboardSolo();
                    Long myEntryId = showLeaderboardMessage.getEntryId();
                    leaderboardSoloController.showMine(myEntryId);
                });
            } else {
                javaFXUtility.queueJFXThread(() -> {
                    mainController.showLeaderboardSolo();
                    mainController.showLeaderboardMulti();
                    String userName = mainController.getUser().getName();
                    leaderboardSoloController.initMulti(showLeaderboardMessage.getEntries(), userName, "End");
                    leaderboardSoloController.showEntries();
                    leaderboardSoloController.setEndScreen();
                });
            }
        } else if (showLeaderboardMessage.getGameProgress().equals("Mid")) {
            javaFXUtility.queueJFXThread(() -> {
                mainController.showLeaderboardMulti();
                String userName = mainController.getUser().getName();
                leaderboardSoloController.initMulti(showLeaderboardMessage.getEntries(), userName, "Mid");
                leaderboardSoloController.showEntries();
                leaderboardSoloController.setMidScreen();
            });
        }
    }

    public void changeScore(int score, Label pointsLabel, Label newPoints) {

        String[] string = javaFXUtility.getText(pointsLabel).split(" ");

        int currScore = Integer.parseInt(string[0]);
        int pointsAdded = score - currScore;

        if (hasDoublePointsForThisRound())
            javaFXUtility.setText(newPoints, " + 2x " + pointsAdded / 2);
        else
            javaFXUtility.setText(newPoints, " + " + pointsAdded);

        if (pointsAdded == 0)
            javaFXUtility.setStyle(newPoints, "-fx-text-fill: rgb(255,0,0);");
        else
            javaFXUtility.setStyle(newPoints, "-fx-text-fill: rgb(0, 210, 28);");

        javaFXUtility.playPointsAnimation(newPoints);
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

    public int getQuestionsLeft() {
        return this.questionsLeft;
    }

    public MultiQuestionController getMultiQuestionController() {
        return multiQuestionController;
    }

    public EstimateQuestionController getEstimateQuestionController() {
        return estimateQuestionController;
    }

    public ChooseConsumptionController getChooseConsumptionController() {
        return chooseConsumptionController;
    }

    public LeaderboardSoloController getLeaderboardSoloController() {
        return leaderboardSoloController;
    }

    public WaitingRoomController getWaitingRoomController() {
        return waitingRoomController;
    }

    public void setLeaderboardSoloController(LeaderboardSoloController leaderboardSoloController) {
        this.leaderboardSoloController = leaderboardSoloController;
    }

    public void setWaitingRoomController(WaitingRoomController waitingRoomController) {
        this.waitingRoomController = waitingRoomController;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setQuestionsLeft(int questionsLeft) {
        this.questionsLeft = questionsLeft;
    }

    public void setRemainingJokers(List<Joker> remainingJokers) {
        this.remainingJokers = remainingJokers;
    }

    public void setAvailableJokers(EnumSet<Joker> availableJokers) {
        this.availableJokers = availableJokers;
    }
    public void incrementNotAnswered() {
        if (gameMode == GameMode.MULTI) {
            questionsWithoutAnswer++;
            if (questionsWithoutAnswer == 3) {
                javaFXUtility.queueJFXThread(() -> {
                    exitGame();
                    try {
                        mainController.kickAlert();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public void resetNotAnswered() {
        setQuestionsWithoutAnswer(0);
    }
    public void setQuestionsWithoutAnswer(int questionsWithoutAnswer) {
        this.questionsWithoutAnswer = questionsWithoutAnswer;
    }
}

