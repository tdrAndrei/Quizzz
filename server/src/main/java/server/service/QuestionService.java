package server.service;

import commons.Activity;
import commons.EstimateQuestion;
import commons.MultiChoiceQuestion;
import commons.Question;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class QuestionService {

    private final ActivityRepository repo;
    private final List<Activity> activityList;
    private final Random rm;

    public QuestionService(ActivityRepository repo, Random rm) {
        this.repo = repo;
        this.rm = rm;
        this.activityList = this.repo.findAll();
    }

    public Question makeMultipleChoice(int seconds) {
        List<Activity> answers = new ArrayList<>();

        int index1 = rm.nextInt(activityList.size());
        answers.add(activityList.get(index1));

        int index2 = rm.nextInt(activityList.size());
        while (Math.abs(index1 - index2) <= 1) {
            index2 = rm.nextInt(activityList.size());
        }
        answers.add(activityList.get(index2));

        int index3 = (index1 + index2) / 2;
        answers.add(activityList.get(index3));

        //decide if the answer is the biggest consumption or the lowest consumption
        int dice = rm.nextInt(2);
        int index = 0;

        String title = "";
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

    public Question makeEstimate(int seconds) {
        Activity a = activityList.get(rm.nextInt(activityList.size()));
        String title = "How much energy does this activity take?";
        return new EstimateQuestion(title, a.getConsumption_in_wh(), List.of(a), seconds);
    }
}