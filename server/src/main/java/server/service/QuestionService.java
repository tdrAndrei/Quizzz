package server.service;

import commons.Activity;
import commons.EstimateQuestion;
import commons.MultiChoiceQuestion;
import commons.Question;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class QuestionService {

    private final ActivityRepository repo;
    private int numActivities;
    private final Random rm;

    public QuestionService(ActivityRepository repo, Random rm) {
        this.repo = repo;
        this.rm = rm;
        this.numActivities = (int) this.repo.count();
    }

    public Question makeMultipleChoice(double seconds) {
        List<Activity> answers = new ArrayList<>();

        int index1 = rm.nextInt(this.numActivities);
        while (processIndex(index1, answers)){
            index1 = rm.nextInt(this.numActivities);
        }

        int index2 = rm.nextInt(this.numActivities);
        while (Math.abs(index1 - index2) <= 1 || processIndex(index2, answers)) {
            index2 = rm.nextInt(this.numActivities);
        }

        int index3 = (index1 + index2) / 2;
        while (processIndex(index3, answers)){
            index3++;
            if (index3 == index2){
                index3++;
            }
        }

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

    /**
     * Method to check if an index has a valid activity associated with it i.e. there is an acitivity
     * at that index and the image path is not corrupted
     * @param index the index to check in the db, integer
     * @param answers the list of answers to add the activity to if it's valid
     * @return a boolean, true if there is a problem with it, false if no issues
     */
    public boolean processIndex(int index, List<Activity> answers){
        if (this.repo.findById((long) index).isPresent()){
            File image = new File(this.repo.findById((long) index).get().getImage_path());
            if (image.exists()){
                //if the index returns a valid item AND it has a valid image, add it
                answers.add(this.repo.findById((long) index).get());
                return false;
            } else {
                //delete entries with corrupt image paths
                this.repo.deleteById((long) index);
                this.numActivities--;
                //just for testing
                System.out.println("deleted corrupt activity");
                return true;
            }
        }

        return true;
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
        Activity a = null;
        boolean flag = false;
        long randomIndex = rm.nextInt(this.numActivities);
        while (!flag){
            if (this.repo.findById(randomIndex).isPresent()
                    && this.repo.findById(randomIndex).get().getConsumption_in_wh() > 5
                    && new File(this.repo.findById(randomIndex).get().getImage_path()).exists()){
                a = this.repo.findById(randomIndex).get();
                flag = true;
            } else if (this.repo.findById(randomIndex).isPresent()
                    && !(new File(this.repo.findById(randomIndex).get().getImage_path()).exists())){
                //if corrupt image path delete it
                this.repo.deleteById(randomIndex);
                this.numActivities--;
                //just for testing
                System.out.println("deleted corrupt activity");
            }
            randomIndex = rm.nextInt(this.numActivities);
        }
        String title = "How much energy does this activity take?";

        Long c = a.getConsumption_in_wh();

        Random rm = new Random();
        int i = 2 + rm.nextInt(5);
        int j = 2 + rm.nextInt(5);
        List<Long> bounds = new ArrayList<Long>();
        bounds.add(c - c * i / 10L);
        bounds.add(c + c * j / 10L);

        return new EstimateQuestion(title, a.getConsumption_in_wh(), List.of(a), bounds, seconds);
    }
}