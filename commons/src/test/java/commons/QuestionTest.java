package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing MultiChoiceQuestion and EstimateQuestion
 */
class QuestionTest {

    public static int TIME;
    public Question multi;
    public Question estimate;
    public Question compare;

    @BeforeEach
    public void init() {
        TIME = 20;
        multi = new MultiChoiceQuestion("Choose between 3", 2, null, TIME);
        estimate = new EstimateQuestion("How much energy it takes to watch netflix?", 800, null, null, TIME);
        compare= new CompareQuestion("What takes the most energy", 2, null, 20.0 );
    }

    @Test
    public void scoreMultiZero() {
        assertEquals(0, multi.calculateScore(1, 5));
    }

    @Test
    public void scoreMultiGoodAnswerEarly() {
        //easy difficulty
        assertEquals(96, multi.calculateScore(2, 5));

        //hard difficulty
        multi.setDifficulty(8.5);
        assertEquals(95, multi.calculateScore(2, 5));
    }

    @Test
    public void scoreMultiGoodAnswerAverage() {
        //easy difficulty
        assertEquals(87, multi.calculateScore(2, 10));

        //hard difficulty
        multi.setDifficulty(8.5);
        assertEquals(81, multi.calculateScore(2, 10));
    }

    @Test
    public void scoreMultiGoodAnswerLate() {
        //easy difficulty
        assertEquals(72, multi.calculateScore(2, 15));

        //hard difficulty
        multi.setDifficulty(8.5);
        assertEquals(59, multi.calculateScore(2, 15));
    }

    @Test
    public void scoreEstimateBadAnswer() {
        assertEquals(0, estimate.calculateScore(100, 3));
    }

    @Test
    public void scoreEstimateGoodAnswer() {
        assertEquals(87, estimate.calculateScore(830, 5));

        assertEquals(78, estimate.calculateScore(830, 10));

        assertEquals(64, estimate.calculateScore(830, 15));
    }

    @Test
    public void scoreEstimateAverageAnswer() {
        assertEquals(78, estimate.calculateScore(860, 5));

        assertEquals(69, estimate.calculateScore(860, 10));

        assertEquals(55, estimate.calculateScore(860, 15));
    }

    @Test
    public void scoreCompareGoodAnswerAverage() {
        //easy difficulty
        assertEquals(87, compare.calculateScore(2, 10));

        //hard difficulty
        compare.setDifficulty(8.5);
        assertEquals(81, compare.calculateScore(2, 10));
    }

    @Test
    public void scoreCompareGoodAnswerLate() {
        //easy difficulty
        assertEquals(72, compare.calculateScore(2, 15));

        //hard difficulty
        compare.setDifficulty(8.5);
        assertEquals(59, compare.calculateScore(2, 15));
    }

    @Test
    public void scoreCompareGoodAnswerEarly() {
        //easy difficulty
        assertEquals(96, multi.calculateScore(2, 5));

        //hard difficulty
        multi.setDifficulty(8.5);
        assertEquals(95, multi.calculateScore(2, 5));
    }

    @Test
    public void scoreCompareZero() {
        assertEquals(0, multi.calculateScore(1, 5));
    }

    @Test
    public void testGetMagnitude() {
        assertEquals(10, ((EstimateQuestion) estimate).getMagnitude(11));
    }

    @Test
    public void GetBounds() {
        List<Long> list = new ArrayList<>();
        ((EstimateQuestion) estimate).setBounds(list);
        assertEquals(list, ((EstimateQuestion) estimate).getBounds());
    }

    @Test
    void getTitle() {
        assertEquals("Choose between 3", multi.getTitle());

        assertEquals("How much energy it takes to watch netflix?", estimate.getTitle());
    }

    @Test
    public void setTitle() {
        multi.setTitle("Choose between 2");
        assertEquals("Choose between 2", multi.getTitle());

        estimate.setTitle("How much energy it takes to make coffee");
        assertEquals("How much energy it takes to make coffee", estimate.getTitle());
    }

    @Test
    public void getActivities() {
        List<Activity> list = new ArrayList<>();
        multi.setActivities(list);
        estimate.setActivities(list);
        assertEquals(list, multi.getActivities());
        assertEquals(list, estimate.getActivities());
    }
    @Test
    public void addActivity() {
        List<Activity> list = new ArrayList<>();
        multi.setActivities(list);
        Activity activity = new Activity(0L, "00/smartphone.png", "Charging your smartphone at night",
                10L, "https://9to5mac.com/2021/09/16/iphone-13-battery-life/");
        multi.addActivity(activity);
        List<Activity> list2 = new ArrayList<>();
        estimate.setActivities(list2);
        estimate.addActivity(activity);
        assertEquals(activity, multi.getActivities().get(0));
        assertEquals(activity, estimate.getActivities().get(0));
    }
    @Test
    public void setActivities() {
        List<Activity> list = new ArrayList<>();
        multi.setActivities(list);
        estimate.setActivities(list);
        assertEquals(list, multi.getActivities());
        assertEquals(list, estimate.getActivities());
    }

    @Test
    public void getTime() {
        assertEquals(TIME, multi.getTime());
        assertEquals(TIME, estimate.getTime());
    }

    @Test
    public void getAnswer() {
        assertEquals(2, multi.getAnswer());
        assertEquals(800, estimate.getAnswer());
    }


}