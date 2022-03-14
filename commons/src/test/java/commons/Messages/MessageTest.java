package commons.Messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message("ThisIsNewMessage");
    }

    @Test
    void getType() {
        assertEquals("ThisIsNewMessage", message.getType());
    }

    @Test
    void setType() {
        message.setType("AnotherMessage");
        assertEquals("AnotherMessage", message.getType());
    }

    @Test
    void testEquals() {
        Message message2 = new Message("ThisIsNewMessage");
        assertEquals(message2, message);
    }
    @Test
    void testNotEquals() {
        Message message2 = new Message("ThisIsMessage");
        assertNotEquals(message2, message);
    }

    @Test
    void testToString() {
        String s = "Message{" +
                "type='" + message.getType() + '\'' +
                '}';
    }
}