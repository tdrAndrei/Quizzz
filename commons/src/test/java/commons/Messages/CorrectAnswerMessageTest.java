package commons.Messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorrectAnswerMessageTest {
    private CorrectAnswerMessage message;

    @BeforeEach
    void setUp() {
        int score = 999;
        String mess = "ShowCorrectAnswer";
        message = new CorrectAnswerMessage(mess, score,999L);
    }

    @Test
    void getType() {
        assertEquals("ShowCorrectAnswer", message.getType());
    }

    @Test
    void setType() {
        message.setType("CorrectAnswer");
        assertEquals("CorrectAnswer", message.getType());
    }

    @Test
    void testEquals() {
        CorrectAnswerMessage message2 = new CorrectAnswerMessage("ShowCorrectAnswer", 999,999L);
        assertEquals(message2, message);
    }

    @Test
    void testToString() {
        String s ="CorrectAnswerMessage{" +
                "score=" + message.getScore() +
                ", type='" + message.getType() + '\'' +
                '}';
        assertEquals(s, message.toString());
    }

    @Test
    void getScore() {
        assertEquals(999, message.getScore());
    }
}