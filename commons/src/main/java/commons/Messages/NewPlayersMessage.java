package commons.Messages;

import commons.Player;

import java.util.List;
import java.util.Objects;

/**
 * The type New players message.
 */
public class NewPlayersMessage extends Message {

    private List<Player> playerList;

    /**
     * Instantiates a new New players message.
     */
    public NewPlayersMessage() {
    }

    /**
     * Instantiates a new New players message.
     *
     * @param type       the type
     * @param playerList the player list
     */
    public NewPlayersMessage(String type, List<Player> playerList) {
        super(type);
        this.playerList = playerList;
    }

    /**
     * Gets player list.
     *
     * @return the player list
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Sets player list.
     *
     * @param playerList the player list
     */
    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NewPlayersMessage that = (NewPlayersMessage) o;
        return playerList.equals(that.playerList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerList);
    }

    @Override
    public String toString() {
        return "NewPlayersMessage{" +
                "playerList=" + playerList +
                '}';
    }
}
