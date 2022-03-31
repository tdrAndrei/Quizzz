package commons.Messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullMessageTest {

    private NullMessage nullMessage;

    @BeforeEach
    void setUp() {
        nullMessage = new NullMessage("None");
    }

    @Test
    void getType() {
        assertEquals("None", nullMessage.getType());
    }

    @Test
    void setType() {
        nullMessage.setType("None2");
        assertEquals("None2", nullMessage.getType());
    }

    @Test
    void testEquals() {
        NullMessage nullMessage2 = new NullMessage("None");
        assertEquals(nullMessage2, nullMessage);
    }

    @Test
    void testToString() {
        String test = "Message{" +
                "type='" + nullMessage.getType() + '\'' +
                '}';
        assertEquals(test, nullMessage.toString());
    }
}