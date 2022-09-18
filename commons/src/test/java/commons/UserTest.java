package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "Jeff");
    }

    @Test
    void setName() {
        user.setName("Jack");
        assertEquals("Jack", user.getName());
    }

    @Test
    void getId() {
        assertEquals(1, user.getId());
    }

    @Test
    void getName() {
        assertEquals("Jeff", user.getName());
    }

    @Test
    void testToString() {
        String test = "User{" +
            "id=" + user.getId()+
                    ", name='" + user.getName()+ '\'' +
                    '}';
        assertEquals(test, user.toString());
    }
}