package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing MultiChoiceQuestion and EstimateQuestion
 */
class QuestionTest {

    public static int TIME;
    public Question multi;
    public Question estimate;

    @BeforeEach
    public void init() {
        TIME = 20;
        multi = new MultiChoiceQuestion("Choose between 3", 2, null, TIME);
        estimate = new EstimateQuestion("How much energy it takes to watch netflix?", 800, null, TIME);
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
}