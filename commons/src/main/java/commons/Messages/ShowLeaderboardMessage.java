package commons.Messages;

import commons.LeaderboardEntry;
import commons.Messages.Message;

import java.util.List;
import java.util.Objects;

/**
 * The type Show leaderboard message.
 */
public class ShowLeaderboardMessage extends Message {
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
     * @param entries the entries
     */
    public ShowLeaderboardMessage(String type, List<LeaderboardEntry> entries) {
        super(type);
        this.entries = entries;
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<LeaderboardEntry> getEntries() {
        return entries;
    }

    /**
     * Sets entries.
     *
     * @param entries the entries
     */
    public void setEntries(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ShowLeaderboardMessage that = (ShowLeaderboardMessage) o;
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entries);
    }

    @Override
    public String toString() {
        return "ShowLeaderboardMessage{" +
                "entries=" + entries +
                '}';
    }
}
