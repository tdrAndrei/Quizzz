package commons.Messages;

import commons.Question;

import java.util.Objects;

/**
 * The type New question message.
 */
public class NewQuestionMessage extends Message {
    private Question question;
    private int score;

    /**
     * Instantiates a new New question message.
     */
    public NewQuestionMessage() {
    }

    /**
     * Instantiates a new New question message.
     *
     * @param type     the type
     * @param question the question
     * @param score    the score
     */
    public NewQuestionMessage(String type, Question question, int score) {
        super(type);
        this.question = question;
        this.score = score;
    }

    /**
     * Gets question.
     *
     * @return the question
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Sets question.
     *
     * @param question the question
     */
    public void setQuestion(Question question) {
        this.question = question;
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
        NewQuestionMessage that = (NewQuestionMessage) o;
        return score == that.score && Objects.equals(question, that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), question, score);
    }

    @Override
    public String toString() {
        return "NewQuestionMessage{" +
                "type='" + type + '\'' +
                ", question=" + question +
                ", score=" + score +
                '}';
    }
}
