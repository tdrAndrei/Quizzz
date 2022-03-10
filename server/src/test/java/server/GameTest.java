package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.Messages.CorrectAnswerMessage;
import commons.Messages.Message;
import commons.Messages.NewPlayersMessage;
import commons.Messages.NewQuestionMessage;
import commons.MultiChoiceQuestion;
import commons.Player;
import commons.Question;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.QuestionService;
import server.service.TestActivityRepository;

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
        Rand rand = new Rand(0);
        this.questionService = new QuestionService(testActivityRepository, rand);
        game = new Game(1L, questionService);
        game.addPlayer(userOne);
        game.addPlayer(userTwo);
        game.addPlayer(userThree);
    }

    @Test
    public void constructorTest() {
        assertNotNull(game);
        //TODO
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
        game.setMaxTime(37);
        Integer[] testTimeArr = new Integer[game.getMaxTime().size()];
        Arrays.fill(testTimeArr, 37);
        assertArrayEquals(game.getMaxTime().values().toArray(), testTimeArr);
    }

    @Test
    public void halfTimeJokerTest() {
        game.setMaxTime(36);
        game.halfTimeJoker(userTwo.getId());
        assertEquals(game.getMaxTime().get(userTwo.getId()), 36);
        assertEquals(game.getMaxTime().get(userThree.getId()), 18);
    }

    @Test
    void initializeStageTest() {
        game.initializeStage();
        //TODO
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
        //TODO ?
    }

    @Test
    void removePlayerTest() {
        game.removePlayer(3L);
        assertNull(game.getPlayerMap().get(3L));
    }

    @Test
    void ifStageEndedTest() {
        game.setMaxTime(1000);
        game.setStartTime(new Date());
        game.ifStageEnded();
        //TODO
    }

    @Test
    void insertCorrectAnswerIntoDiffTest() {
        Question testMCQ = new MultiChoiceQuestion("Test", 2, new ArrayList<>(), 10);
        game.setCurrentQuestion(testMCQ);
        game.insertCorrectAnswerIntoDiff();
        boolean allEqual = true;
        for(Message msg : game.getDiffMap().values()) {
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
        for(Message msg : game.getDiffMap().values()) {
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
        game.insertQuestionIntoDiff(testMCQ);
        boolean allEqual = true;
        for(Message msg : game.getDiffMap().values()) {
            if (!(msg instanceof NewQuestionMessage) || !((NewQuestionMessage) msg).getQuestion().equals(testMCQ)) {
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
    void getQuestionServiceTest() {
        return;
        //TODO
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
        game.setMaxTime(137);
        boolean allEqual = true;
        for(Integer time: game.getMaxTime().values()) {
            if (time != 137) {
                allEqual = false;
                break;
            }
        }
        assertTrue(allEqual);
    }

    @Test
    void getStageQueueTest() {
        assertNotNull(game.getStageQueue());
        //TODO
    }

    @Test
    void getDiffMapTest() {
        return;
        //TODO
    }

    @Test
    void getAmountAnsweredTest() {
        game.setAmountAnswered(3);
        assertEquals(game.getAmountAnswered(), 3);
    }

    @Test
    void equalsTest() {
        return;
        //TODO
    }

    @Test
    void hashCodeTest() {
        return;
        //TODO
    }

    @Test
    void toStringTest() {
        return;
        //TODO ? (String is huge!)
    }

    private static class Rand extends Random {

        int predetermined;
        int next;

        public Rand(int predetermined) {
            super();
            this.predetermined = predetermined;
            this.next = 0;
        }

        @Override
        public int nextInt(int bound) {
            if (bound == 2)
                return predetermined;
            else
                return next++;
        }

    }
}