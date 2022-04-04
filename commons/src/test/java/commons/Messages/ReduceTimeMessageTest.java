package commons.Messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReduceTimeMessageTest {
    private ReduceTimeMessage reduceTimeMessage;


    @BeforeEach
    void setUp() {
        reduceTimeMessage = new ReduceTimeMessage("ReduceTime", "User1", 10);
    }

    @Test
    void getType() {
        assertEquals("ReduceTime", reduceTimeMessage.getType());
    }

    @Test
    void setType() {
        reduceTimeMessage.setType("ReduceTime2");
        assertEquals("ReduceTime2", reduceTimeMessage.getType());
    }

    @Test
    void testEquals() {
        ReduceTimeMessage reduceTimeMessage2 = new ReduceTimeMessage("ReduceTime", "User1", 10);
        assertEquals(reduceTimeMessage2, reduceTimeMessage);
    }

    @Test
    void testNotEquals() {
        ReduceTimeMessage reduceTimeMessage2 = new ReduceTimeMessage("ReduceTime2", "User2", 10);
        assertNotEquals(reduceTimeMessage2, reduceTimeMessage);
    }

    @Test
    void testToString() {
        String test = "Message{" +
                "type='" + reduceTimeMessage.getType() + '\'' +
                '}';
        assertEquals(test, reduceTimeMessage.toString());
    }

    @Test
    void getUserName() {
        assertEquals("User1", reduceTimeMessage.getUserName());
    }

    @Test
    void setUserName() {
        reduceTimeMessage.setUserName("User2");
        assertEquals("User2", reduceTimeMessage.getUserName());
    }

    @Test
    void getNewTime() {
        assertEquals(10, reduceTimeMessage.getNewTime());
    }

    @Test
    void setNewTime() {
        reduceTimeMessage.setNewTime(5);
        assertEquals(5, reduceTimeMessage.getNewTime());
    }
}