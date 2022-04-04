package commons;

import java.util.List;

public class ChooseConsumptionQuestion extends Question{

    private List<Long> answerConsumptions;

    public ChooseConsumptionQuestion(String title, long answer, List<Activity> activities, double time, List<Long> answerConsumptions) {
        super(title, answer, activities, time);
        setMax_score(100);
        setDifficulty(7);
        this.answerConsumptions = answerConsumptions;
    }

    ChooseConsumptionQuestion() {
    }

    public List<Long> getAnswerConsumptions() {
        return answerConsumptions;
    }

    @Override
    public int calculateScore(long userAnswer, double seconds) {
        if (userAnswer != this.answer)
            return 0;

        int score = (int) Math.floor(max_score - ((((double) seconds / time) * difficulty) * (((double) seconds / time) * difficulty)));
        return score;
    }
}
