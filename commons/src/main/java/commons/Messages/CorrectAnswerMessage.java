package commons.Messages;

import java.util.Objects;

/**
 * The type Correct answer message.
 */
public class CorrectAnswerMessage extends Message {
    private int score;

    /**
     * Instantiates a new Correct answer message.
     */
    public CorrectAnswerMessage() {

    }

    /**
     * Instantiates a new Correct answer message.
     *
     * @param type  the type
     * @param score the score
     */
    public CorrectAnswerMessage(String type, int score, Long correctAnswer) {
        super(type);
        this.score = score;
    }

    /**
     * Gets score.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets score.
     *
     * @param score the score
     */
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CorrectAnswerMessage that = (CorrectAnswerMessage) o;
        return score == that.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), score);
    }

    @Override
    public String toString() {
        return "CorrectAnswerMessage{" +
                "score=" + score +
                ", type='" + type + '\'' +
                '}';
    }
}
