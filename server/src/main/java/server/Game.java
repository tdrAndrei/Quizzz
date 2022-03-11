package server;

import commons.LeaderboardEntry;
import commons.Messages.*;
import commons.Player;
import commons.Question;
import commons.User;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import server.service.LeaderBoardEntryService;
import server.service.QuestionService;

import java.util.*;

/**
 * The type Game.
 */
public class Game {

    private Question currentQuestion;
    private final long id;
    private final QuestionService questionService;
    private final Map<Long, Player> playerMap = new HashMap<>();
    //private int playerLimit;
    private Date startTime;
    private final Map<Long, Integer> maxTime = new HashMap<>();
    private final Queue<Pair<String, Integer>> stageQueue = new LinkedList<>();
    private final Map<Long, Message> diffMap = new HashMap<>();
    private int amountAnswered = 0;
    private LeaderBoardEntryService leaderBoardEntryService;

    public Game(long id, QuestionService questionService, LeaderBoardEntryService leaderBoardEntryService) {
        this.id = id;
        this.leaderBoardEntryService = leaderBoardEntryService;
        this.questionService = questionService;
        stageQueue.add(new MutablePair<>("Waiting", Integer.MAX_VALUE));
        for (int i = 0; i < 10; i++) {
            stageQueue.add(new MutablePair<>("Question", 7));
            stageQueue.add(new MutablePair<>("CorrectAns", 3));
        }
        stageQueue.add(new MutablePair<>("Leaderboard", 7));
        for (int i = 0; i < 10; i++) {
            stageQueue.add(new MutablePair<>("Estimate", 7));
            stageQueue.add(new MutablePair<>("CorrectAns", 3));
        }
        stageQueue.add(new MutablePair<>("End", 15));

        initializeStage();
    }

    //When joining a game, a client sends its user object, if already in game, a client just sends it's userid
    public void addPlayer(User user) {
        Player newPlayer = new Player(user);
        playerMap.put(user.getId(), newPlayer);
        diffMap.put(user.getId(), new NullMessage("None"));
        maxTime.put(user.getId(), Integer.MAX_VALUE);
        insertMessageIntoDiff(new NewPlayersMessage("NewPlayers", new ArrayList<>(playerMap.values())));
    }

    public void initializeStage() {
        amountAnswered = 0;
        Pair<String, Integer> stagePair = stageQueue.peek();
        if (stagePair == null) {
            return;
        }
        String stage = stagePair.getKey();
        startTime = new Date();
        switch(stage) {
            case "Question":
                setMaxTime(stagePair.getValue());
                currentQuestion = questionService.makeMultipleChoice(stagePair.getValue());
                insertQuestionIntoDiff(currentQuestion);
                break;

            case "CorrectAns":
                setMaxTime(stagePair.getValue());
                insertCorrectAnswerIntoDiff();
                break;

            case "Estimate":
                setMaxTime(stagePair.getValue());
                currentQuestion = questionService.makeEstimate(stagePair.getValue());
                insertQuestionIntoDiff(currentQuestion);
                break;

            case "Leaderboard":
                if (playerMap.size() == 1) {
                    initializeStage();
                }
                setMaxTime(stagePair.getValue());
                insertMessageIntoDiff(new ShowLeaderboardMessage("ShowLeaderboard", new ArrayList<>(playerMap.values())));
                break;

            case "End":
                if (playerMap.size() == 1) {
                    Player player = playerMap.entrySet().iterator().next().getValue();
                    leaderBoardEntryService.insert(new LeaderboardEntry(player.getUser().getName(), player.getScore()));
                }
                setMaxTime(stagePair.getValue());
                insertMessageIntoDiff(new ShowLeaderboardMessage("EndGame", new ArrayList<>(playerMap.values())));
                break;

            case "Waiting":
                setMaxTime(stagePair.getValue());
                break;
        }
    }

    public void processAnswer(long userAnswer, long userId) {
        int elapsed = ((int) (new Date().getTime() - startTime.getTime()) / 1000);
        Player currPlayer = playerMap.get(userId);
        currPlayer.setScore(currPlayer.getScore() + currentQuestion.calculateScore(userAnswer, elapsed));
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

        //If we are still in waiting screen, inform clients of updated player list
        Pair<String, Integer> pair = stageQueue.peek();
        if (pair != null && pair.getKey().equals("Waiting")) {
            insertMessageIntoDiff(new NewPlayersMessage("NewPlayers", new ArrayList<>(playerMap.values())));
        }
    }

    public void halfTimeJoker(long jokerUserId) {
        for (long otherPlayerId : playerMap.keySet()) {
            if (otherPlayerId == (jokerUserId)) {
                continue;
            }
            maxTime.put(otherPlayerId, maxTime.get(otherPlayerId) / 2);
        }
    }

    public void ifStageEnded() {
        Date date = new Date();
        if (amountAnswered == playerMap.size()) {
            stageQueue.poll();
            initializeStage();
            return;
        }

        for (int maxTime : maxTime.values()) {
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
    public void insertQuestionIntoDiff(Question question) {
        for (long id : diffMap.keySet()) {
            NewQuestionMessage questionMessage = new NewQuestionMessage("NewQuestion", question, playerMap.get(id).getScore());
            diffMap.put(id, questionMessage);
        }
    }
    public void setMaxTime(Integer time) {
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

    public Map<Long, Integer> getMaxTime() {
        return maxTime;
    }

    public Queue<Pair<String, Integer>> getStageQueue() {
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
}
