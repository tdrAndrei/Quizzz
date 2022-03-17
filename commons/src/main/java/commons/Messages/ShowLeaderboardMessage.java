package commons.Messages;

import com.fasterxml.jackson.annotation.JsonTypeName;
import commons.LeaderboardEntry;
import commons.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Show leaderboard message.
 */
@JsonTypeName("ShowLeaderboard")
public class ShowLeaderboardMessage extends Message {
    private String gameProgress;
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
    public ShowLeaderboardMessage(String type, String gameProgress, List<Player> players) {
        super(type);
        this.gameProgress = gameProgress;
        this.players = players;
        setLeaderboardEntries();
    }

    /**
     * Gets the game progress. This indicates which of the leaderboards is sent:
     * Mid (when sending mid-game scores), or End (when sending end-game scores).
     *
     * @return the game progress
     */
    public String getGameProgress() {
        return gameProgress;
    }

    /**
     * Sets game progress
     *
     * @param gameProgress the game progress
     */
    public void setGameProgress(String gameProgress) {
        this.gameProgress = gameProgress;
    }

    /**
     * Gets players.
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
