package commons.Messages;

import commons.EstimateQuestion;
import commons.MultiChoiceQuestion;
import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewQuestionMessageTest {
    private NewQuestionMessage message;
    public static int TIME;
    private Question multi;
    private Question estimate;

    @BeforeEach
    void setUp() {

        TIME = 20;
        multi = new MultiChoiceQuestion("Choose between 3", 2, null, TIME);
        estimate = new EstimateQuestion("How much energy it takes to watch netflix?", 800, null, TIME);
        int score = 999;
        message = new NewQuestionMessage("NewQuestionMessage", "MC", multi.getTitle(), multi.getActivities(), multi.getTime(), score);
    }

    @Test
    void getType() {
        assertEquals("NewQuestionMessage", message.getType());
    }

    @Test
    void setType() {
        message.setType("NewQuestion");
        assertEquals("NewQuestion", message.getType());
    }

    @Test
    void testEquals() {
        NewQuestionMessage message2 = new NewQuestionMessage("NewQuestionMessage", "MC", multi.getTitle(), multi.getActivities(), multi.getTime(), 999);
        assertEquals(message2, message);
    }
    @Test
    void testNotEquals() {
        NewQuestionMessage message2 = new NewQuestionMessage("NewQuestionMessage", "MC", multi.getTitle(), multi.getActivities(), multi.getTime(), 997);
        assertNotEquals(message2, message);
    }

    @Test
    void testToString() {
        String s = "NewQuestionMessage{" +
                "type='" + message.getType() +"("+message.getQuestionType()+")"+ '\'' +
                ", title='" + message.getTitle() + '\'' +
                ", activities=" + message.getActivities() +
                ", time=" + message.getTime() +
                ", score=" + message.getScore() +
                '}';
        assertEquals(s, message.toString());
    }

    @Test
    void getActivities() {
        assertEquals(multi.getActivities(), message.getActivities());
    }

    @Test
    void setActivities() {
        message.setActivities(estimate.getActivities());
        assertEquals(estimate.getActivities(), message.getActivities());
    }

    @Test
    void getScore() {
        assertEquals(999, message.getScore());
    }

    @Test
    void setScore() {
        message.setScore(997);
        assertEquals(997, message.getScore());
    }
}