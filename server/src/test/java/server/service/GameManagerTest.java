package server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.*;
import commons.Messages.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {
    private GameManager gameManager;

    @BeforeEach
    public void init() throws JsonProcessingException {
        this.gameManager = new GameManager(new QuestionService(new TestActivityRepository(), null) {
            public void QuestionService() {

            }
            @Override
            public Question makeMultipleChoice(int seconds) {
                List<Activity> fakeActivities = new ArrayList<>();
                fakeActivities.add(new Activity("id-1", "/", "test_act_1", 3L,
                        "testsrc"));
                fakeActivities.add(new Activity("id-2", "/", "test_act_2", 30L,
                        "testsrc"));
                fakeActivities.add(new Activity("id-3", "/", "test_act_3", 300L,
                        "testsrc"));
                return new MultiChoiceQuestion("TestTitle", 1, fakeActivities, seconds);
            }

            @Override
            public int getIndex(List<Activity> answers, Comparator<Long> cmp) {
                return 1;
            }

            @Override
            public Question makeEstimate(int seconds) {
                List<Activity> fakeActivities = new ArrayList<>();
                fakeActivities.add(new Activity("id-1", "/", "test_act_1", 3L,
                        "/"));
                fakeActivities.add(new Activity("id-2", "/", "test_act_2", 30L,
                        "/"));
                fakeActivities.add(new Activity("id-3", "/", "test_act_3", 300L,
                        "/"));
                return new EstimateQuestion("title", 1, fakeActivities, seconds);
            }
        });
    }

    @Test
    public void joinSoloTest(){
        User user = new User(1L, "TestName");
        long response = gameManager.joinSolo(user);
        assertEquals(0L, response);

        User user2 = new User(2L, "TestName");
        long response2 = gameManager.joinSolo(user2);
        assertEquals(1L, response2);
    }

    @Test
    public void joinMultiTest() {
        User user = new User(1L, "TestName");
        User user2 = new User(2L, "TestName");
        long response = gameManager.joinMulti(user);
        long response2 = gameManager.joinMulti(user2);
        assertEquals(response, response2);
    }

    @Test
    public void startSoloGame() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinSolo(user);
        Message update = gameManager.getUpdate(response, 1L);
        assertEquals("NewQuestion", update.getType());
    }

    @Test
    public void joinMultiAfterStartTest() {
        User user = new User(1L, "TestName");
        User user2 = new User(2L, "TestName");
        long response = gameManager.joinSolo(user);
        gameManager.startGame(response);
        long response2 = gameManager.joinSolo(user2);
        assertNotEquals(response, response2);
    }

    @Test
    public void getFirstUpdateSoloTest() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinSolo(user);
        Message update = gameManager.getUpdate(response, 1L);
        assertEquals("NewQuestion", update.getType());
    }

    @Test
    public void getFirstUpdateMultiTest() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinMulti(user);
        Message update = gameManager.getUpdate(response, 1L);
        assertEquals("NewPlayers", update.getType());
    }

    @Test
    public void processCorrectAnswerTest() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinSolo(user);
        gameManager.processAnswer(1L, 1, response);
        Game game = gameManager.getGameMap().get(response);
        assertTrue(game.getPlayerMap().get(1L).getScore() != 0);
    }

    @Test
    public void processIncorrectAnswerTest() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinSolo(user);
        gameManager.processAnswer(1L, 3, response);
        Game game = gameManager.getGameMap().get(response);
        assertEquals(0, game.getPlayerMap().get(1L).getScore());
    }

    @Test
    public void disconnectSolo() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinSolo(user);
        gameManager.disconnectPlayer(1L, response);
        Game game = gameManager.getGameMap().get(response);
        assertNull(game);
    }

    @Test
    public void disconnectMulti() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinMulti(user);
        User user2 = new User(2L, "TestName2");
        long response2 = gameManager.joinMulti(user2);
        Game game = gameManager.getGameMap().get(response2);
        assertEquals(2, game.getPlayerMap().size());
        gameManager.disconnectPlayer(1L, response);
        assertEquals(1, game.getPlayerMap().size());
    }

    @Test
    public void halfTimeJokerOtherPeopleTest() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinMulti(user);
        User user2 = new User(2L, "TestName2");
        long response2 = gameManager.joinMulti(user2);
        Game game = gameManager.getGameMap().get(response2);

        Map<Long, Integer> timeMap = game.getMaxTime();
        int check1 = timeMap.get(1L);

        gameManager.useTimeJoker(response, 2L);
        int check2 = timeMap.get(1L);

        assertEquals(check2, check1 / 2);
    }

    @Test
    public void halfTimeJokerNotYourselfTest() {
        User user = new User(1L, "TestName");
        long response = gameManager.joinMulti(user);
        User user2 = new User(2L, "TestName2");
        long response2 = gameManager.joinMulti(user2);
        Game game = gameManager.getGameMap().get(response2);

        Map<Long, Integer> timeMap = game.getMaxTime();
        int check1 = timeMap.get(2L);

        gameManager.useTimeJoker(response, 2L);
        int check2 = timeMap.get(2L);

        assertEquals(check2, check1);
    }

    @Test
    public void getGameMapTest() {
        Map<Long, Game> gameMap = gameManager.getGameMap();
        assertNotNull(gameMap);
    }
}
