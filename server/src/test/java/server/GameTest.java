package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.Messages.*;
import commons.MultiChoiceQuestion;
import commons.Player;
import commons.Question;
import commons.User;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.LeaderBoardEntryService;
import server.service.QuestionService;
import server.service.TestActivityRepository;
import server.service.TestLeaderboardEntryRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private User userOne = new User(1, "UserOne");
    private User userTwo = new User(2, "UserTwo");
    private User userThree = new User(3, "UserThree");
    private TestActivityRepository testActivityRepository;
    private QuestionService questionService;

    @BeforeEach
    public void before() throws JsonProcessingException {
        this.testActivityRepository = new TestActivityRepository();
        Random rand = new Random();
        this.questionService = new QuestionService(testActivityRepository, rand);
        game = new Game(1L, questionService, new LeaderBoardEntryService(new TestLeaderboardEntryRepo()));
        game.addPlayer(userOne);
        game.addPlayer(userTwo);
        game.addPlayer(userThree);
    }

    @Test
    public void constructorTest() {
        assertNotNull(game);
        assertNotEquals(game.getStageQueue().size(), 0);
    }

    @Test
    public void addPlayerTest() {
        game.addPlayer(new User(4L, "TestAddPlayer"));
        assertNotNull(game.getPlayerMap().get(4L));
        assertNotNull(game.getDiffMap().get(4L));
        assertNotNull(game.getMaxTime().get(4L));
    }

    @Test
    public void setMaxTimeTest() {
        game.setMaxTime(37.0);
        Double[] testTimeArr = new Double[game.getMaxTime().size()];
        Arrays.fill(testTimeArr, 37.0);
        assertArrayEquals(game.getMaxTime().values().toArray(), testTimeArr);
    }

    @Test
    public void halfTimeJokerTest() {
        game.setMaxTime(36.0);
        game.halfTimeJoker(userTwo.getId());
        assertEquals(game.getMaxTime().get(userTwo.getId()), 36.0);
        assertEquals(Math.round(game.getMaxTime().get(userThree.getId())), 18.0);
    }

    @Test
    void initializeStageTest() {
        game.getStageQueue().clear();
        Question testMCQ = new MultiChoiceQuestion("Test", 2, new ArrayList<>(), 10);
        game.setCurrentQuestion(testMCQ);
        game.getStageQueue().offer(new MutablePair<>("Question", 1.0));
        game.getStageQueue().offer(new MutablePair<>("CorrectAns", 1.0));
        game.getStageQueue().offer(new MutablePair<>("Leaderboard", 1.0));
        game.getStageQueue().offer(new MutablePair<>("Estimate", 1.0));
        game.getStageQueue().offer(new MutablePair<>("CorrectAns", 1.0));
        game.getStageQueue().offer(new MutablePair<>("End", 1.0));
        game.setStartTime(new Date());
        int countDown = 6;
        while (!game.getStageQueue().isEmpty()) {
            game.setAmountAnswered(game.getPlayerMap().size());
            game.ifStageEnded();
            countDown--;
            assertEquals(countDown, game.getStageQueue().size());
        }
    }

    @Test
    void processAnswerTestCorrect() {
        game.setStartTime(new Date());
        Question testMCQ = new MultiChoiceQuestion("Test", 2, new ArrayList<>(), 10);
        game.setCurrentQuestion(testMCQ);
        game.getPlayerMap().get(1L).setScore(0);
        game.setAmountAnswered(0);
        game.processAnswer(2L, 1L);
        assertEquals(game.getAmountAnswered(), 1);
        assertNotEquals(game.getPlayerMap().get(1L).getScore(), 0);
    }

    @Test
    void processAnswerTestIncorrect() {
        game.setStartTime(new Date());
        Question testMCQ = new MultiChoiceQuestion("Test", 2, new ArrayList<>(), 10);
        game.setCurrentQuestion(testMCQ);
        game.getPlayerMap().get(1L).setScore(0);
        game.processAnswer(1L, 1L);
        assertEquals(game.getPlayerMap().get(1L).getScore(), 0);
    }

    @Test
    void getUpdateTest() {
        game.addPlayer(userThree);
        assertTrue(game.getUpdate(3L) instanceof NewPlayersMessage);
        assertTrue(game.getUpdate(3L) instanceof NullMessage);
        insertQuestionIntoDiffTest();
        assertTrue(game.getUpdate(3L) instanceof NewQuestionMessage);
        assertTrue(game.getUpdate(3L) instanceof NullMessage);
        insertCorrectAnswerIntoDiffTest();
        assertTrue(game.getUpdate(3L) instanceof CorrectAnswerMessage);
        assertTrue(game.getUpdate(3L) instanceof NullMessage);
    }

    @Test
    void removePlayerTest() {
        game.removePlayer(3L);
        assertNull(game.getPlayerMap().get(3L));
    }

    @Test
    void ifStageNotEndedTest() {
        Pair<String, Double> nextStage = game.getStageQueue().peek();
        game.setMaxTime(1000.0);
        game.setStartTime(new Date());
        game.ifStageEnded();
        assertEquals(nextStage, game.getStageQueue().peek());
    }

    @Test
    void ifStageEndedTest() throws InterruptedException {
        Pair<String, Double> nextStage = game.getStageQueue().peek();
        game.setMaxTime(1.0);
        game.setStartTime(new Date());
        Thread.sleep(1001);
        game.ifStageEnded();
        assertNotEquals(nextStage, game.getStageQueue().peek());
    }

    @Test
    void insertCorrectAnswerIntoDiffTest() {
        Question testMCQ = new MultiChoiceQuestion("Test", 2, new ArrayList<>(), 10);
        game.setCurrentQuestion(testMCQ);
        game.insertCorrectAnswerIntoDiff();
        boolean allEqual = true;
        for (Message msg : game.getDiffMap().values()) {
            if (!(msg instanceof CorrectAnswerMessage)) {       // check answer if changed in Message class?
                allEqual = false;
                break;
            }
        }
        assertTrue(allEqual);
    }

    @Test
    void insertMessageIntoDiffTest() {
        Message message = new Message("Test");
        game.insertMessageIntoDiff(message);
        boolean allEqual = true;
        for (Message msg : game.getDiffMap().values()) {
            if (!msg.equals(message)) {
                allEqual = false;
                break;
            }
        }
        assertTrue(allEqual);
    }

    @Test
    void insertQuestionIntoDiffTest() {
        Question testMCQ = new MultiChoiceQuestion("Test", 2, new ArrayList<>(), 10);
        game.insertMCQQuestionIntoDiff(testMCQ);
        boolean allEqual = true;
        for (Message msg : game.getDiffMap().values()) {
            if (!(msg instanceof NewQuestionMessage) || !((NewQuestionMessage) msg).getActivities().equals(testMCQ.getActivities())) {
                allEqual = false;
                break;
            }
        }
        assertTrue(allEqual);
    }

    @Test
    void getCurrentQuestionTest() {
        Question testMCQ = new MultiChoiceQuestion("Test", 2, new ArrayList<>(), 10);
        game.setCurrentQuestion(testMCQ);
        assertEquals(testMCQ, game.getCurrentQuestion());
    }

    @Test
    void getIdTest() {
        assertEquals(game.getId(), 1L);
    }

    @Test
    void getPlayerMapTest() {
        Map<Long, Player> playerMap = game.getPlayerMap();
        Map<Long, Player> testPlayerMap = new HashMap<>();
        testPlayerMap.put(userOne.getId(), new Player(userOne));
        testPlayerMap.put(userTwo.getId(), new Player(userTwo));
        testPlayerMap.put(userThree.getId(), new Player(userThree));
        assertEquals(testPlayerMap, playerMap);
    }

    @Test
    void getStartTimeTest() {
        Date now = new Date();
        game.setStartTime(now);
        assertEquals(game.getStartTime(), now);
    }

    @Test
    void getMaxTimeTest() {
        game.setMaxTime(137.0);
        boolean allEqual = true;
        for (Double time: game.getMaxTime().values()) {
            if (time != 137) {
                allEqual = false;
                break;
            }
        }
        assertTrue(allEqual);
    }

    @Test
    void getAmountAnsweredTest() {
        game.setAmountAnswered(3);
        assertEquals(game.getAmountAnswered(), 3);
    }
}