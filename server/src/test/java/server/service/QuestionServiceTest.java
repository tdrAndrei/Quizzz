package server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.Activity;
import commons.Question;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class QuestionServiceTest {

    public static TestActivityRepository repo;

    @BeforeAll
    public static void before() throws JsonProcessingException {
        repo = new TestActivityRepository();
    }

    @Test
    void makeMultipleChoiceHighest() {

        QuestionService qs = new QuestionService(repo, new Rand(0));
        Activity a = repo.findAll().get(0);
        Question q = qs.makeMultipleChoice(20);
        int expectedIndex = q.getActivities().indexOf(a);
        assertNotEquals(0, q.calculateScore((long) expectedIndex, 0));

    }

    @Test
    void makeMultipleChoiceLowest() {

        QuestionService qs = new QuestionService(repo, new Rand(1));
        Activity a = repo.findAll().get(1);
        Question q = qs.makeMultipleChoice(20);
        int expectedIndex = q.getActivities().indexOf(a);
        assertNotEquals(0, q.calculateScore((long) expectedIndex, 0));

    }

    @Test
    void getIndexHighest() {

        QuestionService qs = new QuestionService(repo, new Rand(0));
        assertEquals(0, qs.getIndex(repo.activityList, Comparator.naturalOrder()));

    }

    @Test
    void getIndexLowest() {

        QuestionService qs = new QuestionService(repo, new Rand(1));
        assertEquals(1, qs.getIndex(repo.activityList, Comparator.reverseOrder()));

    }

    @Test
    void makeEstimate() {

        QuestionService qs = new QuestionService(repo, new Rand(0));
        Activity expected = repo.findAll().get(0);
        assertEquals(expected, qs.makeEstimate(20).getActivities().get(0));

    }

    @Test
    void makeCompare() {
        QuestionService qs = new QuestionService(repo, new Rand(0));
        Activity a = repo.findAll().get(1);
        Question q = qs.makeCompare(20);

        Activity b = q.getActivities().stream()
                .filter(x -> x.getTitle().startsWith(a.getTitle()))
                .filter(x -> x.getTitle().endsWith(" times"))
                .collect(Collectors.toList()).get(0);
        int expectedIndex = q.getActivities().indexOf(b);

        assertNotEquals(0, q.calculateScore((long) expectedIndex, 0));
    }

    private static class Rand extends Random {

        int predetermined;
        int next;

        public Rand(int predetermined) {
            super();
            this.predetermined = predetermined;
            this.next = 0;
        }

        @Override
        public int nextInt(int bound) {
            if (next + 1 >= bound)
                next--;
            if (bound == 2)
                return predetermined;
            else if (bound == 1)
                return predetermined;
            else
                return next++;
        }

    }

}