package server.service;

import commons.Messages.Message;
import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.Game;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameManager {

    private final QuestionService questionService;
    private final Map<Long, Game> gameMap;
    private long idCounter = 0L;

    @Autowired
    public GameManager(QuestionService questionService){
        this.questionService = questionService;
        this.gameMap = new HashMap<>();
    }

    public long joinSolo(User user) {
        long gameId = idCounter++;
        Game newGame = new Game(gameId, questionService);
        newGame.addPlayer(user);
        gameMap.put(gameId, newGame);
        startGame(gameId);
        return gameId;
    }

    public Message getUpdate(long gameId, long userId) {
        Game game = gameMap.get(gameId);
        game.ifStageEnded();
        return game.getUpdate(userId);
    }

    public long joinMulti(User user) {
        for (Game game : gameMap.values()) {
            if (game.getStageQueue().peek().getKey().equals("Waiting")) {
                game.addPlayer(user);
                return game.getId();
            }
        }
        long gameId = idCounter++;
        Game newGame = new Game(gameId, questionService);
        newGame.addPlayer(user);
        gameMap.put(gameId, newGame);
        return gameId;
    }

    public void startGame(long gameId) {
        Game game = gameMap.get(gameId);
        //Removes current stage from head and loads new one
        game.getStageQueue().poll();
        game.initializeStage();
    }

    public void processAnswer(long userId, long userAnswer, long gameId) {
        Game game = gameMap.get(gameId);
        game.processAnswer(userAnswer, userId);
    }

    public void disconnectPlayer(long userId, long gameId) {
        Game game = gameMap.get(gameId);
        game.removePlayer(userId);
        if (game.getPlayerMap().isEmpty()){
            gameMap.remove(gameId);
        }
    }

    public void useTimeJoker(long gameId, long userId) {
        Game game = gameMap.get(gameId);
        game.halfTimeJoker(userId);
    }

    public Map<Long, Game> getGameMap() {
        return this.gameMap;
    }
}
