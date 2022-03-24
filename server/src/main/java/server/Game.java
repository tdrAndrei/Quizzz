package server;

import commons.*;
import commons.Messages.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import server.service.LeaderBoardEntryService;
import server.service.QuestionService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * The type Game.
 */
public class Game {

    private Question currentQuestion;
    private final long id;
    private final QuestionService questionService;
    private final Map<Long, Player> playerMap = new HashMap<>();
    //private int playerLimit;
    private boolean isSolo = false;
    private Date startTime;
    private final Map<Long, Boolean> doublePointsMap = new HashMap<>();
    private final Map<Long, Double> maxTime = new HashMap<>();
    private final Queue<Pair<String, Double>> stageQueue = new LinkedList<>();
    private final Map<Long, Message> diffMap = new HashMap<>();
    private int amountAnswered = 0;
    private final LeaderBoardEntryService leaderBoardEntryService;

    public Game(long id, QuestionService questionService, LeaderBoardEntryService leaderBoardEntryService) {
        this.id = id;
        this.leaderBoardEntryService = leaderBoardEntryService;
        this.questionService = questionService;

        Random random = new Random();
        stageQueue.add(new MutablePair<>("Waiting", Double.MAX_VALUE));

        for (int i = 0; i < 20; i++) {
            int j = random.nextInt(10);
            if (j <= 2) {
                stageQueue.add(new MutablePair<>("Question", 20.0));
                stageQueue.add(new MutablePair<>("CorrectAns", 3.0));
            } else if (j <= 7) {
                stageQueue.add(new MutablePair<>("Compare", 20.0));
                stageQueue.add(new MutablePair<>("CorrectAns", 3.0));
            } else {
                stageQueue.add(new MutablePair<>("Estimate", 20.0));
                stageQueue.add(new MutablePair<>("CorrectAns", 3.0));
            }
            if (i == 9) {
                stageQueue.add(new MutablePair<>("Leaderboard", 5.0));
            }
        }
        stageQueue.add(new MutablePair<>("End", 5.0));

        initializeStage();
    }

    //When joining a game, a client sends its user object, if already in game, a client just sends it's userid
    public void addPlayer(User user) {
        Player newPlayer = new Player(user);
        playerMap.put(user.getId(), newPlayer);
        diffMap.put(user.getId(), new NullMessage("None"));
        maxTime.put(user.getId(), Double.MAX_VALUE);
        doublePointsMap.put(user.getId(), false);
        insertMessageIntoDiff(new NewPlayersMessage("NewPlayers", new ArrayList<>(playerMap.values())));
    }

    public void initializeStage() {
        amountAnswered = 0;
        Pair<String, Double> stagePair = stageQueue.peek();
        if (stagePair == null) {
            return;
        }
        String stage = stagePair.getKey();
        startTime = new Date();

        setMaxTime(stagePair.getValue());

        switch (stage) {
            case "Question":
                currentQuestion = questionService.makeMultipleChoice(stagePair.getValue());
                insertMCQQuestionIntoDiff(currentQuestion);
                break;

            case "CorrectAns":
                insertCorrectAnswerIntoDiff();
                break;

            case "Estimate":
                currentQuestion = questionService.makeEstimate(stagePair.getValue());
                insertEstimateQuestionIntoDiff(currentQuestion);
                break;

            case "Compare":
                currentQuestion = questionService.makeCompare(stagePair.getValue());
                insertCompareQQuestionIntoDiff(currentQuestion);
                break;

            case "Leaderboard":
                if (isSolo) {
                    setMaxTime(0.0);
                    break;
                }
                insertMessageIntoDiff(new ShowLeaderboardMessage("ShowLeaderboard", "Mid", new ArrayList<>(playerMap.values())));
                break;

            case "End":
                ShowLeaderboardMessage leaderboardMessage = new ShowLeaderboardMessage("ShowLeaderboard", "End", new ArrayList<>(playerMap.values()));
                if (isSolo) {
                    Player player = playerMap.entrySet().iterator().next().getValue();
                    Long myEntryId = leaderBoardEntryService.insert(new LeaderboardEntry(player.getUser().getName(), player.getScore())).getId();
                    leaderboardMessage.setEntryId(myEntryId);
                }
                insertMessageIntoDiff(leaderboardMessage);
                break;

            case "Waiting":
                break;
        }
    }

    public void processAnswer(long userAnswer, long userId) {
        int elapsed = ((int) (new Date().getTime() - startTime.getTime()) / 1000);
        Player currPlayer = playerMap.get(userId);
        if (maxTime.get(userId) < elapsed) {
            return;
        }
        if (doublePointsMap.get(userId)) {
            currPlayer.setScore(currPlayer.getScore() + (2 * (currentQuestion.calculateScore(userAnswer, elapsed))));
            doublePointsMap.put(userId, false);
        } else {
            currPlayer.setScore(currPlayer.getScore() + currentQuestion.calculateScore(userAnswer, elapsed));
        }
        maxTime.put(userId, 0.0); // to make sure maxtime is not high when joker used by player who answered
        amountAnswered++;
    }

    public Message getUpdate(long userId) {
        Message update = diffMap.get(userId);
        diffMap.put(userId, new NullMessage("None"));
        return update;
    }

    public void removePlayer(long userId) {
        playerMap.remove(userId);
        diffMap.remove(userId);
        maxTime.remove(userId);
        doublePointsMap.remove(userId);
        //If we are still in waiting screen, inform clients of updated player list
        Pair<String, Double> pair = stageQueue.peek();
        if (pair != null && pair.getKey().equals("Waiting")) {
            insertMessageIntoDiff(new NewPlayersMessage("NewPlayers", new ArrayList<>(playerMap.values())));
        }
    }

    public void halfTimeJoker(long jokerUserId) {
        for (long otherPlayerId : playerMap.keySet()) {
            if (otherPlayerId == (jokerUserId)) {
                continue;
            }
            // new max = max - half*left
            // left = max - elapsed
            // new max = max - half*(max-elapsed)
            double curTime = (double) new Date().getTime() / 1000;
            double elapsed = curTime - (double) startTime.getTime() / 1000;
            double maxTimeVal = maxTime.get(otherPlayerId);
            double newMaxTimeVal = maxTimeVal - 0.5 * (maxTimeVal - elapsed);

            double newTimeLeft = newMaxTimeVal - elapsed;
            insertReduceTimeMessageIntoDiff(jokerUserId, otherPlayerId, newTimeLeft);
            maxTime.put(otherPlayerId, newMaxTimeVal);
        }
    }

    public long eliminateJoker() {
        long correctAns = currentQuestion.getAnswer();
        Random rand = new Random();
        long randomVal = rand.nextInt(4);
        if (correctAns == randomVal) {
            return (randomVal + 1) % 3;
        }
        return randomVal;
    }

    public void doublePointsJoker(long userId) {
        doublePointsMap.put(userId, true);
    }

    public void newQuestionJoker() {
        initializeStage();
    }

    public void ifStageEnded() {
        Date date = new Date();
        if (amountAnswered == playerMap.size()) {
            stageQueue.poll();
            initializeStage();
            return;
        }

        for (double maxTime : maxTime.values()) {
            double elapsed = ((double) date.getTime() - (double) startTime.getTime()) / 1000;
            if (!(elapsed > maxTime)) {
                return;
            }
        }
        stageQueue.poll();
        initializeStage();
    }

    public void insertCorrectAnswerIntoDiff() {
        for (long id : diffMap.keySet()) {
            CorrectAnswerMessage answerMessage = new CorrectAnswerMessage("ShowCorrectAnswer", playerMap.get(id).getScore(), currentQuestion.getAnswer());
            diffMap.put(id, answerMessage);
        }

    }
    public void insertMessageIntoDiff(Message message) {
        diffMap.replaceAll((i, v) -> message);
    }

    public void insertReduceTimeMessageIntoDiff(long userId, long otherId, double newTime) {
        ReduceTimeMessage timeMessage = new ReduceTimeMessage("ReduceTime", playerMap.get(userId).getUser().getName(), newTime);
        diffMap.put(otherId, timeMessage);
    }

    public void insertMCQQuestionIntoDiff(Question question) {
        for (long id : diffMap.keySet()) {
            List<byte[]> imagesBytes = getImageBytesList(question);
            NewQuestionMessage questionMessage = new NewQuestionMessage("NewQuestion", "MC", question.getTitle(), question.getActivities(), question.getTime(), playerMap.get(id).getScore(), null, imagesBytes);
            diffMap.put(id, questionMessage);
        }
    }

    public void insertCompareQQuestionIntoDiff(Question question) {
        for (long id : diffMap.keySet()) {
            List<byte[]> imagesBytes = getImageBytesList(question);
            NewQuestionMessage questionMessage = new NewQuestionMessage("NewQuestion", "Compare", question.getTitle(), question.getActivities(), question.getTime(), playerMap.get(id).getScore(), null, imagesBytes);
            diffMap.put(id, questionMessage);
        }
    }

    public List<byte[]> getImageBytesList(Question question) {
        List<byte[]> imagesBytes = new ArrayList<>();
        for (Activity activity : question.getActivities()) {
            try {
                FileInputStream fis = new FileInputStream(activity.getImage_path());
                byte[] imageArr = fis.readAllBytes();
                imagesBytes.add(imageArr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imagesBytes;
    }
    public void insertEstimateQuestionIntoDiff(Question question) {
        for (long id : diffMap.keySet()) {
            List<byte[]> imagesBytes = getImageBytesList(question);
            NewQuestionMessage questionMessage = new NewQuestionMessage("NewQuestion", "Estimate", question.getTitle(), question.getActivities(), question.getTime(), playerMap.get(id).getScore(), getBoundsForEstimate(question.getAnswer()), imagesBytes);
            diffMap.put(id, questionMessage);
        }
    }


    public List<Long> getBoundsForEstimate(long answer) {
        Random rm = new Random();
        int i = 2 + rm.nextInt(5);
        int j = 2 + rm.nextInt(5);
        List<Long> bounds = new ArrayList<Long>();
        bounds.add(Long.valueOf(answer - answer * i / 10L));
        bounds.add(Long.valueOf(answer + answer * j / 10L));
        return bounds;
    }

    public void setMaxTime(Double time) {
        maxTime.replaceAll((i, v) -> time);
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public long getId() {
        return id;
    }

    public QuestionService getQuestionService() {
        return questionService;
    }

    public Map<Long, Player> getPlayerMap() {
        return playerMap;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Map<Long, Double> getMaxTime() {
        return maxTime;
    }

    public Queue<Pair<String, Double>> getStageQueue() {
        return stageQueue;
    }

    public Map<Long, Message> getDiffMap() {
        return diffMap;
    }

    public int getAmountAnswered() {
        return amountAnswered;
    }

    public void setAmountAnswered(int amountAnswered) {
        this.amountAnswered = amountAnswered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(currentQuestion, game.currentQuestion) && Objects.equals(id, game.id) && Objects.equals(questionService, game.questionService) && Objects.equals(playerMap, game.playerMap) && Objects.equals(startTime, game.startTime) && Objects.equals(maxTime, game.maxTime) && Objects.equals(stageQueue, game.stageQueue) && Objects.equals(diffMap, game.diffMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentQuestion, id, questionService, playerMap, startTime, maxTime, stageQueue, diffMap);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", playerMap=" + playerMap +
                ", startTime=" + startTime +
                ", maxTime=" + maxTime +
                ", stageQueue=" + stageQueue +
                ", diffMap=" + diffMap +
                '}';
    }

    public void setSolo(boolean solo) {
        isSolo = solo;
    }
}
