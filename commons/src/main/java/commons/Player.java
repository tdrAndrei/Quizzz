package commons;

import java.util.Objects;

/**
 * The type Player, it is a wrapper class for the user so we can store the score in a game
 */
public class Player {

    private User user;
    private int score;


    public Player() {

    }

    /**
     * Instantiates a new Player
     *
     * @param user the user
     */
    public Player(User user){
        this.user = user;
        this.score = 0;
    }

    /**
     * Gets the user attribute
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the score attribute
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the user attribute
     *
     * @param user the user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets the score attribute
     *
     * @param score the score
     */
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return score == player.score && Objects.equals(user, player.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, score);
    }
}
