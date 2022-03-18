package commons.Messages;

import com.fasterxml.jackson.annotation.JsonTypeName;
import commons.Activity;

import java.util.List;
import java.util.Objects;

/**
 * The type New question message.
 */
@JsonTypeName("NewQuestion")
public class NewQuestionMessage extends Message {
    private String questionType;
    private String title;
    private List<Activity> activities;
    private int time;
    private int score;

    /**
     * Instantiates a new New question message.
     */
    public NewQuestionMessage() {
    }

    /**
     * Instantiates a new New question message.
     *
     * @param type       the type
     * @param title      the title
     * @param activities the activities
     * @param time       the time
     * @param score      the score
     */
    public NewQuestionMessage(String type, String questionType, String title, List<Activity> activities, int time, int score) {
        super(type);
        this.questionType = questionType;
        this.title = title;
        this.activities = activities;
        this.time = time;
        this.score = score;
    }

    /**
     * Gets title.
     *
     * @return the question type
     */
    public String getQuestionType() {
        return questionType;
    }

    /**
     * Sets question type.
     *
     * @param questionType the title
     */
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets activities.
     *
     * @return the activities
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Sets activities.
     *
     * @param activities the activities
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(int time) {
        this.time = time;
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
        return time == that.time && score == that.score && Objects.equals(title, that.title) && Objects.equals(activities, that.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, activities, time, score);
    }

    @Override
    public String toString() {
        return "NewQuestionMessage{" +
                "type='" + type + "("+questionType+")"+'\'' +
                ", title='" + title + '\'' +
                ", activities=" + activities +
                ", time=" + time +
                ", score=" + score +
                '}';
    }
}
