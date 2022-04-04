package commons.Messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmojiMessageTest {

    private EmojiMessage emojiMessage;

    @BeforeEach
    void setUp() {
        emojiMessage = new EmojiMessage(2137, 2, "User1");
    }

    @Test
    void getUserId() {
        assertEquals(2137, emojiMessage.getUserId());
    }

    @Test
    void setUserId() {
        emojiMessage.setUserId(2138);
        assertEquals(2138, emojiMessage.getUserId());
    }

    @Test
    void getEmojiIndex() {
        assertEquals(2, emojiMessage.getEmojiIndex());
    }

    @Test
    void setEmojiIndex() {
        emojiMessage.setEmojiIndex(0);
        assertEquals(0, emojiMessage.getEmojiIndex());
    }

    @Test
    void getUsername() {
        assertEquals("User1", emojiMessage.getUsername());
    }

    @Test
    void setUsername() {
        emojiMessage.setUsername("User2");
        assertEquals("User2", emojiMessage.getUsername());
    }

    @Test
    void testToString() {
        String test = "EmojiMessage{" +
                "userId=" + emojiMessage.getUserId() +
                ", emojiIndex=" + emojiMessage.getEmojiIndex() +
                ", username='" + emojiMessage.getUsername() + '\'' +
                '}';
        assertEquals(test, emojiMessage.toString());
    }
}