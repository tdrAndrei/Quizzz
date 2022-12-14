package server.service;

import commons.*;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

import java.io.File;
import java.util.*;

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

    public Question makeChooseConsumption(double seconds) {

        List<Activity> answers = new ArrayList<>();

        int index = rm.nextInt(numActivities);

        while (processIndex(index, answers)){
            index = rm.nextInt(numActivities);
        }

        Activity main = answers.get(0);
        long mainConsumption = main.getConsumption_in_wh();
        List<Long> consumptions = new ArrayList<>();

        while (consumptions.size() < 2){

            long p = 1;
            while (p <= mainConsumption)
                p *= 10L;

            p/=10;

            int c = rm.nextInt(4);
            if (c == 0)
                p = p/2L;
            if (c == 1)
                p = p/4L;
            if (c == 2)
                p = p/5L;
            if (c == 3)
                p = p/10L;

            p = Math.max(1, p);

            int dice = rm.nextInt(2);
            if (dice == 0)
                p = p + mainConsumption;
            else
                p = mainConsumption - p;

            if (!consumptions.contains(p))
                consumptions.add(p);

        }

        consumptions.add(mainConsumption);

        Collections.shuffle(consumptions);
        long answer = consumptions.indexOf(mainConsumption);

        return new ChooseConsumptionQuestion("How much energy does it take?", answer, answers, seconds, consumptions);
    }

    public Question makeCompare(double seconds) {
        List<Activity> answers = new ArrayList<>();
        List<Activity> equalCons = new ArrayList<>();

        Activity mainActivity = null;
        int factor = 1;

        while (equalCons.size() < 2) {
            Activity activityRand = getRandomActivity();
            Long consumption = activityRand.getConsumption_in_wh();

            equalCons = this.repo.findByConsumption(consumption);

            mainActivity = getRandActivityFromList(equalCons);
            if (mainActivity == null) {
                return makeCompare(seconds);
            }

            if (rm.nextInt(equalCons.size()) == 0) {
                ArrayList<Long> consumptions = new ArrayList<>();

                long x = 2;
                while (x <= 100 && consumption/x >= Math.sqrt(consumption)) {
                    if ((double) consumption/x % 1 == 0) {
                        consumptions.add(consumption/x);
                        if (consumption/x <= Integer.MAX_VALUE) {
                            consumptions.add(x);
                        }
                    }
                    x++;
                }
                List<Activity> matches = this.repo.findByConsumption(consumptions);

                Activity activityAnsRand = getRandActivityFromList(matches);
                if (activityAnsRand == null) {
                    return makeCompare(seconds);
                }

                factor = (int) (consumption/activityAnsRand.getConsumption_in_wh());
                Activity correctAns = new Activity(null, activityAnsRand.getImage_path(), activityAnsRand.getTitle() + ", " +
                        (factor) + " times", activityAnsRand.getConsumption_in_wh(), activityAnsRand.getSource());
                equalCons.add(1, correctAns);
            } else {
                checkUniqueness(equalCons, mainActivity);
            }
        }

        answers.add(equalCons.get(1));

        while (answers.size() < 3) {
            Activity wrongAnswer = getRandomActivity();
            int wrongFactor = rm.nextInt(factor * 2) + 2;
            int rand = rm.nextInt(2);
            if (!answers.contains(wrongAnswer) && !equalCons.contains(wrongAnswer) && (rand == 0 || wrongAnswer.getConsumption_in_wh() * wrongFactor != mainActivity.getConsumption_in_wh())) {
                if (rand == 1 && !wrongAnswer.getTitle().contains(" times")) {
                    wrongAnswer.setTitle(wrongAnswer.getTitle() + ", " + wrongFactor + " times");
                }
                answers.add(wrongAnswer);
            }
        }
        Collections.shuffle(answers);
        answers.add(mainActivity);

        String title = lowerCaseTitle(mainActivity.getTitle());
        return new CompareQuestion("Instead of " + title + ", I can try:", answers.indexOf(equalCons.get(1)), answers, seconds);
    }

    public Activity getRandActivityFromList(List<Activity> activities) {
        Activity act = null;
        Collections.shuffle(activities);
        int idx = 0;
        if (activities.size() > 0) {
            act = activities.get(idx);
        }
        while (idx < activities.size() && checkIndex(act.getId().intValue())) {
            act = activities.get(idx);
            idx++;
        }
        return act;
    }

    public void checkUniqueness(List<Activity> activities, Activity target) {
        try {
            int idx = 1;
            while (activities.get(idx).getSource().equals(target.getSource()) || activities.get(idx).getTitle().equals(target.getTitle())) {
                idx++;
            }
            activities.set(1, activities.get(idx));
        } catch (IndexOutOfBoundsException e) { //this means all other activities have the same source or title
            activities.clear();
        }
    }

    public Activity getRandomActivity() {
        int index = rm.nextInt(numActivities);
        while (checkIndex(index)) {
            index = rm.nextInt(numActivities);
        }
        return this.repo.findById((long) index).get();
    }

    public Question makeMultipleChoice(double seconds) {
        List<Activity> answers = new ArrayList<>();

        int index1 = rm.nextInt(numActivities);
        while (processIndex(index1, answers)){
            index1 = rm.nextInt(numActivities);
        }

        int index2 = rm.nextInt(numActivities);
        while (Math.abs(index1 - index2) <= 1 || processIndex(index2, answers)) {
            index2 = rm.nextInt(numActivities);
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
     * Method to check if an index has a valid activity associated with it i.e. there is an activity
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

    public boolean checkIndex(int index) {
        if (this.repo.findById((long) index).isPresent()){
            File image = new File(this.repo.findById((long) index).get().getImage_path());
            if (image.exists()){
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
        long randomIndex = rm.nextInt(numActivities);
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
            randomIndex = rm.nextInt(numActivities);
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

    public static String lowerCaseTitle(String title) {
        char[] c = title.toCharArray();
        if (Character.toUpperCase(c[1]) != c[1]) {
            c[0] = Character.toLowerCase(c[0]);
        }
        return new String(c);
    }
}