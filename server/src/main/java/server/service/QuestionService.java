package server.service;

import commons.*;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

import java.util.*;

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

    public Question makeCompare(double seconds) {
        List<Activity> answers = new ArrayList<>();
        List<Activity> equalCons = new ArrayList<>();

        while (equalCons.size() < 2) {
            int indexRand = rm.nextInt(activityList.size());
            Activity rand = activityList.get(indexRand);
            Long consumption = rand.getConsumption_in_wh();
            equalCons = this.repo.findByConsumption(consumption);
        }
        Collections.shuffle(equalCons);
        Activity main = equalCons.get(0);
        try {
            int idx = 1;
            while (equalCons.get(idx).getSource().equals(main.getSource()) || equalCons.get(idx).getTitle().equals(main.getTitle())) {
                idx++;
            }
        } catch (IndexOutOfBoundsException e) {
            return makeCompare(seconds);
        }
        answers.add(equalCons.get(1));
        while (answers.size() < 3) {
            Activity wrongAnswer = activityList.get(rm.nextInt(activityList.size()));
            if (!equalCons.contains(wrongAnswer)) {
                answers.add(wrongAnswer);
            }
        }
        Collections.shuffle(answers);
        answers.add(main);
        int answer = answers.indexOf(equalCons.get(1));
        return new CompareQuestion("What can you do instead of ... ?", answer, answers, seconds);
    }

    public Question makeMultipleChoice(double seconds) {
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

    public Question makeEstimate(double seconds) {
        Activity a = activityList.get(rm.nextInt(activityList.size()));
        String title = "How much energy does this activity take?";
        return new EstimateQuestion(title, a.getConsumption_in_wh(), List.of(a), seconds);
    }
}