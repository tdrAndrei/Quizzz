package commons.Messages;

import commons.Player;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewPlayersMessageTest {

    private NewPlayersMessage message;
    private List<Player> playerList;

    @BeforeEach
    void setUp() {
        playerList = new ArrayList<>();
        User user = new User("Robert");
        User user2 = new User("Matt");
        Player p1 = new Player(user);
        Player p2 = new Player(user2);
        playerList = new ArrayList<>();
        playerList.add(p1);
        playerList.add(p2);
        message = new NewPlayersMessage("NewPlayer", playerList);
    }

    @Test
    void getType() {
        assertEquals("NewPlayer", message.getType());
    }

    @Test
    void setType() {
        message.setType("NewPlayerAdded");
        assertEquals("NewPlayerAdded", message.getType());
    }

    @Test
    void testEquals() {
        NewPlayersMessage message2;
        message2 = new NewPlayersMessage("NewPlayer", playerList);
        assertEquals(message2, message);

    }
    @Test
    void testNotEquals() {
        NewPlayersMessage message2;
        List<Player> playerList2 = new ArrayList<>();
        User user3 = new User("Robert");
        User user4 = new User("Matt");
        Player p3 = new Player(user3);
        Player p4 = new Player(user4);
        playerList2 = new ArrayList<>();
        playerList2.add(p3);
        playerList2.add(p4);
        message2 = new NewPlayersMessage("NewPlayerAdded", playerList2);
        assertNotEquals(message2, message);

    }

    @Test
    void testToString() {
        String s = "NewPlayersMessage{" +
                "playerList=" + message.getPlayerList() +
                '}';
        assertEquals(s, message.toString());
    }

    @Test
    void getPlayerList() {
        assertEquals(playerList, message.getPlayerList());
    }

    @Test
    void setPlayerList() {
        List<Player> playerList2 = new ArrayList<>();
        User user3 = new User("Robercik");
        User user4 = new User("Matt");
        Player p3 = new Player(user3);
        Player p4 = new Player(user4);
        playerList2 = new ArrayList<>();
        playerList2.add(p3);
        playerList2.add(p4);
        message.setPlayerList(playerList2);
        assertEquals(playerList2, message.getPlayerList());
    }
}