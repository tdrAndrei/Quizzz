package commons.Messages;

import commons.LeaderboardEntry;
import commons.Player;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShowLeaderboardMessageTest {
    private ShowLeaderboardMessage message;
    private List<Player> playerList;
    private List<LeaderboardEntry> entries;
    @BeforeEach
    public void BeforeEach() {
        User user = new User("Robert");
        User user2 = new User("Matt");
        Player p1 = new Player(user);
        Player p2 = new Player(user2);
        playerList = new ArrayList<>();
        playerList.add(p1);
        playerList.add(p2);
        entries = new ArrayList<>();
        message = new ShowLeaderboardMessage("ShowLeaderboard", "Mid", playerList, 5L);

    }

    @Test
    void getType() {
        assertEquals("ShowLeaderboard", message.getType());
    }

    @Test
    void setType() {
        message.setType("ShowLeaderboardMessage");
        assertEquals("ShowLeaderboardMessage", message.getType());

    }

    @Test
    void getPlayers() {
        assertEquals(playerList, message.getPlayers());
    }

    @Test
    void setPlayers() {
        List<Player> playerList2 = new ArrayList<>();
        playerList2.add(new Player(new User("Jon")));
        message.setPlayers(playerList2);
        assertEquals(playerList2, message.getPlayers());
    }



    @Test
    void getLeaderboardEntries() {
        List<LeaderboardEntry> entries2 = new ArrayList<>();
        User user4= new User("Robert");
        User user3 = new User("Matt");
        Player p3 = new Player(user4);
        Player p4 = new Player(user3);
        playerList = new ArrayList<>();
        playerList.add(p3);
        playerList.add(p4);
        entries2.add(new LeaderboardEntry(p3.getUser().getName(), 0));
        entries2.add(new LeaderboardEntry(p4.getUser().getName(), 0));
        assertEquals(entries2, message.getEntries());

    }
    @Test
    void setLeaderboardEntries() {
        List<LeaderboardEntry> entries2 = new ArrayList<>();
        User user4= new User("Robert");
        User user3 = new User("Matt");
        Player p3 = new Player(user4);
        Player p4 = new Player(user3);
        playerList = new ArrayList<>();
        playerList.add(p3);
        playerList.add(p4);
        entries2.add(new LeaderboardEntry(p3.getUser().getName(), 0));
        entries2.add(new LeaderboardEntry(p4.getUser().getName(), 0));
        assertEquals(entries2, message.getEntries());

    }

    @Test
    void testEquals() {
        ShowLeaderboardMessage message2 = new ShowLeaderboardMessage("ShowLeaderboard", "Mid", playerList, 5L);
        assertEquals(message2, message);
    }

    @Test
    void testToString() {
        List<LeaderboardEntry> entries2 = new ArrayList<>();
        User user = new User("Robert");
        User user2 = new User("Matt");
        Player p1 = new Player(user);
        Player p2 = new Player(user2);
        entries2.add(new LeaderboardEntry(p1.getUser().getName(), 0));
        entries2.add(new LeaderboardEntry(p2.getUser().getName(), 0));
        String s ="ShowLeaderboardMessage{" +
                "gameProgress='" + "Mid" + '\'' +
                ", players=" + playerList +
                ", entries=" + entries2 +
                '}';
        assertEquals(s, message.toString());
    }

    @Test
    void testGetGameProgress() {
        assertEquals("Mid", message.getGameProgress());
    }

    @Test
    void testSetGameProgress() {
        message.setGameProgress("Low");
        assertEquals("Low", message.getGameProgress());
    }

    @Test
    void getEntryId() {
        assertEquals(5L, message.getEntryId());
    }

    @Test
    void setEntryId() {
        message.setEntryId(6L);
        assertEquals(6L , message.getEntryId());
    }


}