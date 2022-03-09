package server;

import commons.Messages.Message;
import commons.Messages.NewPlayersMessage;
import commons.Messages.NewQuestionMessage;
import commons.Messages.ShowLeaderboardMessage;
import commons.Player;
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

    private Long id;
    private QuestionService questionService;
    private Map<Long, Player> playerMap;
    private int playerLimit;
    private Date startTime;
    public Map<Long, Integer> maxTime;
    private Queue<Pair<String, Integer>> stageQueue;
    private Map<Long, Message> diffMap;

    @Autowired
    public Game(Long id, QuestionService questionService) {
        this.id = id;
        this.questionService = questionService;
        this.playerMap = new HashMap<>();
        this.maxTime = new HashMap<>();
        this.diffMap = new HashMap<>();
        this.stageQueue = new LinkedList<>();
        stageQueue.add(new MutablePair<String, Integer>("Waiting", Integer.MAX_VALUE));
    }

    public void addPlayer(User user){
        Player newPlayer = new Player(user);
        playerMap.put(user.getId(), newPlayer);
        insertMessageIntoDiff(new NewPlayersMessage("NewPlayers", new ArrayList<>(playerMap.values())));
    }

    public void initializeStage(){
        Pair<String, Integer> stagePair = stageQueue.poll();
        if (stagePair == null){
            return;
        }
        String stage = stagePair.getKey();
        startTime = new Date();
        if (stage.equals("Waiting")){
            setMaxTime(stagePair.getValue());
            return;
        }
        if (stage.equals("Leaderboard")){
            setMaxTime(stagePair.getValue());
            insertMessageIntoDiff(new ShowLeaderboardMessage("ShowLeaderboard", new ArrayList<>(playerMap.values())));
        }
        if (stage.equals("Question")){
            setMaxTime(stagePair.getValue());
            insertMessageIntoDiff(new NewQuestionMessage());
        }
        if (stage.equals("End")){
            setMaxTime(stagePair.getValue());
            //update diff with final leaderboard
        }
    }

    //public Message getUpdate(Long id){
    //    return diff.get(id);
    //}

    public void ifStageEnded(){

    }

    public void insertMessageIntoDiff(Message message){
        for (Long id : diffMap.keySet()){
            diffMap.put(id, message);
        }
    }
    public void setMaxTime(Integer time){
        for (Long id : maxTime.keySet()){
            maxTime.put(id, time);
        }
    }

}
