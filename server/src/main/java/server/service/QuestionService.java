package server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private List<Activity> activityList;
    private final Random rm;


    public final ObjectMapper objectMapper;

    public String json2 = "[\n" +
            "    {\n" +
            "        \"id\": \"00-shower\",\n" +
            "        \"image_path\": \"00/shower.png\",\n" +
            "        \"title\": \"Taking a hot shower for 6 minutes\",\n" +
            "        \"consumption_in_wh\": 4000,\n" +
            "        \"source\": \"https://www.quora.com/How-can-I-estimate-the-kWh-of-electricity-when-I-take-a-shower\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"00-smartphone\",\n" +
            "        \"image_path\": \"00/smartphone.png\",\n" +
            "        \"title\": \"Charging your smartphone at night\",\n" +
            "        \"consumption_in_wh\": 10,\n" +
            "        \"source\": \"https://9to5mac.com/2021/09/16/iphone-13-battery-life/\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"...\",\n" +
            "        \"image_path\": \"xx/image.jpg\",\n" +
            "        \"title\": \"other questions follow in a similar way\",\n" +
            "        \"consumption_in_wh\": 42,\n" +
            "        \"source\": \"https://cse1105.pages.ewi.tudelft.nl/2021-2022/course-website\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"...\",\n" +
            "        \"image_path\": \"xx/image.jpg\",\n" +
            "        \"title\": \"kek\",\n" +
            "        \"consumption_in_wh\": 43,\n" +
            "        \"source\": \"https://cse1105.pages.ewi.tudelft.nl/2021-2022/course-website\"\n" +
            "    }\n" +
            "]\n";


    public QuestionService(ActivityRepository repo, Random rm) {
        this.repo = repo;
        this.rm = rm;
        this.activityList = null;
        //this.activityList = this.repo.findAll();
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            this.activityList = objectMapper.readValue(json2, new TypeReference<List<Activity>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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

    public ActivityRepository getRepo() {
        return repo;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public Random getRm() {
        return rm;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getJson2() {
        return json2;
    }
}