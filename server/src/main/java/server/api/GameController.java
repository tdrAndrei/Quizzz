package server.api;

import commons.Messages.Message;
import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.service.GameManager;

@RestController
@RequestMapping(path = "api/game")
public class GameController {

    private final GameManager gameManager;

    @Autowired
    public GameController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @PostMapping(path = "/joinSolo")
    public long joinSolo(@RequestBody User user) {
        return gameManager.joinSolo(user);
    }

    @PostMapping(path = "/joinMulti")
    public long joinMulti(@RequestBody User user) {
        return gameManager.joinMulti(user);
    }

    @GetMapping(path = "{gameId}")
    public Message pollUpdate(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId) {
        return gameManager.getUpdate(gameId, userId);
    }

    @PostMapping(path = "/start/{gameId}")
    public void startGame(@PathVariable("gameId") long gameId){
        gameManager.startGame(gameId);
    }

    @PostMapping(path = "/submit/{gameId}")
    public void checkAnswer(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId, @RequestParam(required = true) long userAnswer) {
        gameManager.processAnswer(userId, userAnswer, gameId);
    }
    
    @PostMapping(path = "/leave/{gameId}")
    public void leaveGame(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId) {
        gameManager.disconnectPlayer(userId, gameId);
    }
    
    @PostMapping(path = "/joker/time/{gameId}")
    public void useTimeJoker(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId) {
        gameManager.useTimeJoker(gameId, userId);
    }
}

