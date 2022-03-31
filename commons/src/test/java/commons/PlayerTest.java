package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        User user  = new User(21, "Jeff");
        player = new Player(user);
    }

    @Test
    void getUser() {
        User user  = new User(21, "Jeff");
        Player player2 = new Player(user);
        assertEquals(user, player2.getUser());
    }

    @Test
    void getScore() {
        assertEquals(0, player.getScore());
    }

    @Test
    void setUser() {
        User user2  = new User(22, "Jeff");
        player.setUser(user2);
        assertEquals(user2, player.getUser());
    }

    @Test
    void setScore() {
        player.setScore(100);
        assertEquals(100, player.getScore());
    }

    @Test
    void testEquals() {
        User user  = new User(21, "Jeff");
        Player player2 = new Player(user);
        player.setUser(user);
        assertEquals(player2, player);
    }
    @Test
    void testNotEquals() {
        User user  = new User(21, "Jeff");
        Player player2 = new Player(user);
        assertNotEquals(player2, player);
    }
}