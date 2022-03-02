package commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActivityTest {

    private Activity activity;
    private List<Activity> activityList;

    @BeforeEach
    public void before() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        String json = "{\n" +
                "        \"id\": \"00-shower\",\n" +
                "        \"image_path\": \"00/shower.png\",\n" +
                "        \"title\": \"Taking a hot shower for 6 minutes\",\n" +
                "        \"consumption_in_wh\": 4000,\n" +
                "        \"source\": \"https://www.quora.com/How-can-I-estimate-the-kWh-of-electricity-when-I-take-a-shower\"\n" +
                "    }\n";

        activity = objectMapper.readValue(json, Activity.class);

        String json2 = "[\n" +
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
                "    }\n" +
                "]\n";

        activityList = objectMapper.readValue(json2, new TypeReference<List<Activity>>(){});

    }

    @Test
    void testEquals(){

        assertEquals(activity, activityList.get(0));

    }

    @Test
    void testEquals2(){

        Activity activity2 = new Activity("00-smartphone", "00/smartphone.png", "Charging your smartphone at night", 10L, "https://9to5mac.com/2021/09/16/iphone-13-battery-life/");
        assertEquals(activity2, activityList.get(1));

    }

    @Test
    void getId() {

        assertEquals("00-shower", activity.getId());

    }

    @Test
    void getImage_path() {

        assertEquals("00/shower.png", activity.getImage_path());

    }

    @Test
    void getTitle() {

        assertEquals("Taking a hot shower for 6 minutes", activity.getTitle());

    }

    @Test
    void getConsumption_in_wh() {

        assertEquals(4000, activity.getConsumption_in_wh());

    }

    @Test
    void getSource() {

        assertEquals("https://www.quora.com/How-can-I-estimate-the-kWh-of-electricity-when-I-take-a-shower", activity.getSource());

    }

}