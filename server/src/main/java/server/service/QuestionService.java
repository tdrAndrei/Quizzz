package server.service;

import commons.Activity;
import commons.EstimateQuestion;
import commons.MultiChoiceQuestion;
import commons.Question;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Comparator;

@Service
public class QuestionService {

    private final ActivityRepository repo;
    private final int numActivities;
    private final Random rm;

    public QuestionService(ActivityRepository repo, Random rm) {
        this.repo = repo;
        this.rm = rm;
        this.numActivities = (int) this.repo.count();
    }

    public Question makeMultipleChoice(double seconds) {
        List<Activity> answers = new ArrayList<>();

        int index1 = rm.nextInt(this.numActivities);
        while (this.repo.findById((long) index1).isEmpty()){
            index1 = rm.nextInt(this.numActivities);
        }
        answers.add(this.repo.findById((long) index1).get());

        int index2 = rm.nextInt(this.numActivities);
        while (Math.abs(index1 - index2) <= 1 || this.repo.findById((long) index2).isEmpty()) {
            index2 = rm.nextInt(this.numActivities);
        }
        answers.add(this.repo.findById((long) index2).get());

        int index3 = (index1 + index2) / 2;
        while (this.repo.findById((long) index3).isEmpty()){
            index3++;
            if (index3 == index2){
                index3++;
            }
        }
        answers.add(this.repo.findById((long) index3).get());

        //decide if the answer is the biggest consumption or the lowest consumption
        int dice = rm.nextInt(2);
        int index;

        String title;
        if (dice == 0) {
            title = "What consumes the most energy?";
            index = getIndex(answers, Comparator.naturalOrder());
        } else {
            title = "What consumes the least energy?";
            index = getIndex(answers, Comparator.reverseOrder());
        }

        return new MultiChoiceQuestion(title, index, answers, seconds);
    }

    public int getIndex(List<Activity> answers, Comparator<Long> cmp) {
        int index = 0;
        long toCompare = 0;
        if (cmp.equals(Comparator.reverseOrder()))
            toCompare = Long.MAX_VALUE;

        for (int i = 0; i < answers.size(); i++) {
            if (cmp.compare(answers.get(i).getConsumption_in_wh(), toCompare) > 0) {
                toCompare = answers.get(i).getConsumption_in_wh();
                index = i;
            }
        }

        return index;
    }

    public Question makeEstimate(double seconds) {
        long randomIndex = rm.nextInt(this.numActivities);
        Optional<Activity> a = this.repo.findById(randomIndex);
        while (a.isEmpty() || a.get().getConsumption_in_wh() <= 5){
            randomIndex = rm.nextInt(this.numActivities);
        }
        a = this.repo.findById(randomIndex);
        String title = "How much energy does this activity take?";
        return new EstimateQuestion(title, a.get().getConsumption_in_wh(), List.of(a.get()), seconds);
    }
}