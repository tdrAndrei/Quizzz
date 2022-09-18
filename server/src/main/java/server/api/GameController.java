package server.api;

import commons.Messages.Message;
import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Long> joinSolo(@RequestBody User user) {
        long handler = gameManager.joinSolo(user);
        if (handler < 0){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(handler);
    }

    @PostMapping(path = "/joinMulti")
    public ResponseEntity<Long> joinMulti(@RequestBody User user) {
        long handler = gameManager.joinMulti(user);
        if (handler < 0){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(handler);
    }

    @GetMapping(path = "{gameId}")
    public ResponseEntity<Message> pollUpdate(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId) {
        if (gameId < 0 || userId < 0){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameManager.getUpdate(gameId, userId));
    }

    @GetMapping(path = "/howManyQuestions/{gameId}")
    public ResponseEntity<Integer> getNumberOfQuestionsInGame(@PathVariable("gameId") long gameId) {
        if (gameId < 0){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameManager.getNumberOfQuestionsInGame(gameId));
    }

    @GetMapping(path = "/start/{gameId}")
    public ResponseEntity<?> startGame(@PathVariable("gameId") long gameId){
        if (gameId < 0){
            return ResponseEntity.badRequest().build();
        } else {
            gameManager.startGame(gameId);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(path = "/submit/{gameId}")
    public ResponseEntity<?> submitAnswer(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId, @RequestParam(required = true) long userAnswer) {
        if (gameId < 0 || userId < 0 || userAnswer < -1){
            return ResponseEntity.badRequest().build();
        } else {
            gameManager.processAnswer(userId, userAnswer, gameId);
            return ResponseEntity.ok().build();
        }

    }
    
    @GetMapping(path = "/leave/{gameId}")
    public ResponseEntity<?> leaveGame(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId) {
        if (gameId < 0 || userId < 0){
            return ResponseEntity.badRequest().build();
        } else {
            gameManager.disconnectPlayer(userId, gameId);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(path = "/joker/eliminate/{gameId}")
    public ResponseEntity<Long> useEliminateJoker(@PathVariable("gameId") long gameId) {
        if (gameId < 0){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(gameManager.useEliminateAnswer(gameId));
    }

    @GetMapping(path = "/joker/new/{gameId}")
    public ResponseEntity<?> useNewQuestionJoker(@PathVariable("gameId") long gameId) {
        if (gameId < 0){
            return ResponseEntity.badRequest().build();
        } else {
            gameManager.useNewQuestionJoker(gameId);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(path = "/joker/double/{gameId}")
    public ResponseEntity<?> useDoublePointsJoker(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId) {
        if (gameId < 0 || userId < 0){
            return ResponseEntity.badRequest().build();
        } else {
            gameManager.useDoublePointsJoker(gameId, userId);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(path = "/joker/time/{gameId}")
    public ResponseEntity<?> useTimeJoker(@PathVariable("gameId") long gameId, @RequestParam(required = true) long userId) {
        if (gameId < 0 || userId < 0){
            return ResponseEntity.badRequest().build();
        } else {
            gameManager.useTimeJoker(gameId, userId);
            return ResponseEntity.ok().build();
        }
    }
}

