package commons;

import java.util.List;

public class CompareQuestion extends Question {

    public CompareQuestion(String title, int answer, List<Activity> activities, double time) {
        super(title, answer, activities, time);
        setMax_score(100);
        //if you increase this, the score drops faster if you answer later
        setDifficulty(7);
    }

    CompareQuestion() {

    }

    //score = MAX_SCORE - (seconds / questionTime * difficulty) ^ 2
    @Override
    public int calculateScore(long userAnswer, double seconds) {
        if (userAnswer != this.answer)
            return 0;

        int score = (int) Math.floor(max_score - ((((double) seconds / time) * difficulty) * (((double) seconds / time) * difficulty)));
        return score;
    }

}
