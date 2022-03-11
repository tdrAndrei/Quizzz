package commons.Messages;

import commons.LeaderboardEntry;
import commons.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Show leaderboard message.
 */
public class ShowLeaderboardMessage extends Message {
    private List<Player> players;
    private List<LeaderboardEntry> entries;

    /**
     * Instantiates a new Show leaderboard message.
     */
    public ShowLeaderboardMessage() {
    }

    /**
     * Instantiates a new Show leaderboard message.
     *
     * @param type    the type
     * @param players the players
     */
    public ShowLeaderboardMessage(String type, List<Player> players) {
        super(type);
        this.players = players;
        setLeaderboardEntries();
    }

    /**
     * Gets playes.
     *
     * @return the players
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Sets players.
     *
     * @param players the players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setLeaderboardEntries(){
        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
        for (Player player : players) {
            leaderboardEntries.add(new LeaderboardEntry(player.getUser().getName(), player.getScore()));
        }
        this.entries = leaderboardEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShowLeaderboardMessage)) return false;
        if (!super.equals(o)) return false;
        ShowLeaderboardMessage that = (ShowLeaderboardMessage) o;
        return Objects.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), players);
    }

    @Override
    public String toString() {
        return "ShowLeaderboardMessage{" +
                "players=" + players +
                '}';
    }

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }
}
