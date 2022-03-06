package server;

import commons.Player;

import java.util.List;
import java.util.Objects;

/**
 * The type Game.
 */
public abstract class Game {

    private List<Player> players;
    private Long id;
    //missing the list of questions but this can't be implemented yet since we don't have the questions


    /**
     * Instantiates a new Game, note that right now we are missing the list of questions
     *
     * @param players the players
     * @param id      the id
     */
    public Game(List<Player> players, Long id) {
        this.players = players;
        this.id = id;
    }

    /**
     * Gets the list of players in this game
     *
     * @return the players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the id of the current game
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the playerlist
     *
     * @param players the players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Sets the game id
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return Objects.equals(players, game.players) && Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(players, id);
    }
}
