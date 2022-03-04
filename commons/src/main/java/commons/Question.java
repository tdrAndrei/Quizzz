package commons;

import java.util.List;
import java.util.Objects;

public abstract class Question {

    protected String title;
    protected int answer;
    protected List<Activity> activities;
    protected int max_score;
    protected double difficulty;
    protected int time;

    public Question(String title, int answer, List<Activity> activities, int time) {
        this.title = title;
        this.answer = answer;
        this.activities = activities;
        this.time = time;
    }

    Question() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setMax_score(int max_score) {
        this.max_score = max_score;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    //Each question can implement its own logic for choosing the correct answer
    public abstract int calculateScore(int answer, int seconds);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return answer == question.answer && Objects.equals(title, question.title) && Objects.equals(activities, question.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, answer, activities);
    }

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", answer=" + answer +
                ", activities=" + activities +
                '}';
    }
}
