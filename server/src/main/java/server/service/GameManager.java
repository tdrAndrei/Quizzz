package server.service;

import commons.Messages.Message;
import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.Game;

import java.util.Map;

@Service
public class GameManager {

    private final QuestionService questionService;
    private Map<Long, Game> gameMap;
    private static long idCounter = 0L;

    @Autowired
    public GameManager(QuestionService questionService){
        this.questionService = questionService;
    }

    public Long joinSolo(User user) {
        Long gameId = idCounter++;
        Game newGame = new Game(gameId, questionService);
        newGame.addPlayer(user);
        gameMap.put(gameId, newGame);
        startGame(gameId);
        return gameId;
    }
    
    public Message getUpdate(Long gameId, Long userId) {
        Game game = gameMap.get(gameId);
        game.ifStageEnded();
        return game.getUpdate(userId);
    }

    public Long joinMulti(User user) {
        for (Game game : gameMap.values()) {
            if (game.getStageQueue().peek().getKey().equals("Waiting")) {
                game.addPlayer(user);
                return game.getId();
            }
        }
        Long gameId = idCounter++;
        Game newGame = new Game(gameId, questionService);
        newGame.addPlayer(user);
        gameMap.put(gameId, newGame);
        return gameId;
    }

    public void startGame(Long gameId) {
        Game game = gameMap.get(gameId);
        //Removes current stage from head and loads new one
        game.getStageQueue().poll();
        game.initializeStage();
    }

    public void processAnswer(Long userId, Long userAnswer, Long gameId) {
        Game game = gameMap.get(gameId);
        game.processAnswer(userAnswer, userId);
    }

    public void disconnectPlayer(Long userId, Long gameId) {
        Game game = gameMap.get(gameId);
        game.removePlayer(userId);
        if (game.getPlayerMap().isEmpty()){
            gameMap.remove(gameId);
        }
    }

    public void useTimeJoker(Long gameId, Long userId) {
        Game game = gameMap.get(gameId);
        game.halfTimeJoker(userId);
    }
}
