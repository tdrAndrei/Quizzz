package commons;

import java.util.List;

public class EstimateQuestion extends Question {

    public EstimateQuestion(String title, long answer, List<Activity> activities, int time) {
        super(title, answer, activities, time);

        setMax_score(100);

        //Don't set this above MAX_SCORE/2. Signifies how much the score is affected by the user's error
        setDifficulty(30);
    }

    EstimateQuestion() {
    }

    @Override
    public int calculateScore(long answer, int seconds) {
        long margin = getMagnitude(answer);
        long error = Math.abs(answer - this.answer);
        if (error > margin)
            return 0;

        //How much time matters
        double timeCoefficient = max_score * Math.cos((double) seconds / time);

        //How much the error matters
        double accuracyCoefficient = ((double) error / margin) * difficulty;

        return (int) Math.floor(timeCoefficient - accuracyCoefficient);
    }

    public long getMagnitude(long n) {
        long p10 = 1;
        while (p10 <= n)
            p10 *= 10;
        return p10 / 10L;
    }
}
