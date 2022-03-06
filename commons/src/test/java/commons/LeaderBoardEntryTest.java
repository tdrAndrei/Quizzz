package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LeaderBoardEntryTest {

    private Long id = (long) 1;
    private LeaderboardEntry e;
    private LeaderboardEntry f;
    private LeaderboardEntry g;
    private LeaderboardEntry h;
    private LeaderboardEntry i;

    @BeforeEach
    public void before(){
        e = new LeaderboardEntry("name", 10);
        e.id = id;
        id++;
        f = new LeaderboardEntry("name", 10);
        f.id = id;
        id++;
        g = new LeaderboardEntry("John Doe", 10);
        g.id = id;
        id++;
        h = new LeaderboardEntry("John Doe", 25);
        h.id = id;
        id = (long) 0;
    }

    @Test
    public void constructorTest(){
        assertNotNull(e);
    }

    @Test
    public void getIdTest(){
        assertEquals(e.getId(), 1);
    }

    @Test
    public void getNameTest(){
        assertEquals(e.getName(), "name");
    }

    @Test
    public void getScoreTest(){
        assertEquals(e.getScore(), 10);
    }

    @Test
    public void setNameTest(){
        e.setName("Jeff");
        assertEquals(e.getName(), "Jeff");
    }

    @Test
    public void setScoreTest(){
        e.setScore(15);
        assertEquals(e.getScore(), 15);
    }

    @Test
    public void testToString(){
        assertEquals(e.toString(), "LeaderboardEntry{id=1, name='name', score=10}");
    }

    @Test
    public void testNotEqualsNull(){
        assertNotEquals(e, null);
    }

    @Test
    public void equalsTest(){
        //set the id else they will never be equal (test the other two properties)
        f.id = (long) 1;
        assertEquals(e, f);
    }

    @Test
    public void testNotEqualsOnName(){
        //set the id else they might not be equal because of this
        f.id = (long) 3;
        assertNotEquals(f, g);
    }

    @Test
    public void testNotEqualsOnScore(){
        //set the id else they might not be equal because of this
        g.id = (long) 4;
        assertNotEquals(g, h);
    }

    @Test
    public void testNotEqualsOnEverything(){
        assertNotEquals(e, h);
    }

    @Test
    public void equalsHashCode(){
        //set the id else they won't be equal
        e.id = (long) 2;
        assertEquals(e.hashCode(), f.hashCode());
    }

    @Test
    public void notEqualsHashCode(){
        assertNotEquals(e.hashCode(), g.hashCode());
    }
}
