package commons;

import java.util.List;

public class EstimateQuestion extends Question {

    private List<Long> bounds;

    public EstimateQuestion(String title, long answer, List<Activity> activities, List<Long> bounds, double time) {
        super(title, answer, activities, time);
        this.bounds = bounds;

        setMax_score(100);

        //Don't set this above MAX_SCORE/2. Signifies how much the score is affected by the user's error
        setDifficulty(30);
    }

    EstimateQuestion() {
    }

    @Override
    public int calculateScore(long userAnswer, double seconds) {
        long margin = getMagnitude(userAnswer);
        long error = Math.abs(userAnswer - this.answer);
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

    public void setBounds(List<Long> bounds) {
        this.bounds = bounds;
    }

    public List<Long> getBounds() {
        return bounds;
    }
}
