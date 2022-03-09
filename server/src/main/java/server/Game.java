package server;

import commons.Messages.*;
import commons.Player;
import commons.Question;
import commons.User;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import server.service.QuestionService;

import java.util.*;

/**
 * The type Game.
 */
public class Game {

    private Question currentQuestion;
    private final Long id;
    private final QuestionService questionService;
    private final Map<Long, Player> playerMap = new HashMap<>();
    //private int playerLimit;
    private Date startTime;
    public Map<Long, Integer> maxTime = new HashMap<>();
    private final Queue<Pair<String, Integer>> stageQueue = new LinkedList<>();
    private final Map<Long, Message> diffMap = new HashMap<>();
    private static int amountAnswered = 0;

    @Autowired
    public Game(Long id, QuestionService questionService) {
        this.id = id;
        this.questionService = questionService;
        stageQueue.add(new MutablePair<>("Waiting", Integer.MAX_VALUE));
        for (int i = 0; i < 10; i++) {
            stageQueue.add(new MutablePair<>("Question", 20));
            stageQueue.add(new MutablePair<>("CorrectAns", 3));
        }
        for (int i = 0; i < 10; i++) {
            stageQueue.add(new MutablePair<>("Estimate", 20));
            stageQueue.add(new MutablePair<>("CorrectAns", 3));
        }
    }

    public void addPlayer(User user){
        Player newPlayer = new Player(user);
        playerMap.put(user.getId(), newPlayer);
        insertMessageIntoDiff(new NewPlayersMessage("NewPlayers", new ArrayList<>(playerMap.values())));
    }

    public void initializeStage(){
        amountAnswered = 0;
        Pair<String, Integer> stagePair = stageQueue.poll();
        if (stagePair == null){
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
                setMaxTime(stagePair.getValue());
                insertMessageIntoDiff(new ShowLeaderboardMessage("EndGame", new ArrayList<>(playerMap.values())));
                break;

            case "Waiting":
                setMaxTime(stagePair.getValue());
                break;
        }
    }

    public void processAnswer(Long userAnswer, Long userId){
        int elapsed = ((int) (new Date().getTime() - startTime.getTime()) / 1000);
        Player currPlayer = playerMap.get(userId);
        currPlayer.setScore(currPlayer.getScore() + currentQuestion.calculateScore(userAnswer, elapsed));
        amountAnswered++;
    }

    public Message getUpdate(Long id){
        Message update = diffMap.get(id);
        diffMap.put(id, new NullMessage("None"));
        return update;
    }

    public void halfTimeJoker(Long jokerUserId){
        for (Long otherPlayerId : playerMap.keySet()){
            if (otherPlayerId.equals(jokerUserId)) {
                continue;
            }
            maxTime.put(otherPlayerId, maxTime.get(otherPlayerId) / 2);
        }
    }

    public void ifStageEnded(){
        Date date = new Date();
        if (amountAnswered == playerMap.size()){
            initializeStage();
            return;
        }

        for (int maxTime : maxTime.values()){
            if (!(date.getTime() - startTime.getTime() > maxTime)){
                break;
            }
            initializeStage();
        }
    }

    public void insertCorrectAnswerIntoDiff(){
        for (Long id : diffMap.keySet()){
            CorrectAnswerMessage answerMessage = new CorrectAnswerMessage("ShowCorrectAnswer", playerMap.get(id).getScore(), currentQuestion.getAnswer());
            diffMap.put(id, answerMessage);
        }

    }
    public void insertMessageIntoDiff(Message message){
        diffMap.replaceAll((i, v) -> message);
    }
    public void insertQuestionIntoDiff(Question question){
        for (Long id : diffMap.keySet()){
            NewQuestionMessage questionMessage = new NewQuestionMessage("NewQuestion", question, playerMap.get(id).getScore());
            diffMap.put(id, questionMessage);
        }
    }
    public void setMaxTime(Integer time){
        maxTime.replaceAll((i, v) -> time);
    }

}
