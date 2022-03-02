package server.service;

import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserServiceTest {

    private TestUserRepository repo;
    private UserService sut;

    @BeforeEach
    public void setup() {
        repo = new TestUserRepository();
        sut = new UserService(repo);
    }

    @Test
    public void cannotAddNullUser(){
        User user = sut.insert(null);
        assertNull(user);
    }

    @Test
    public void cannotAddNoNameUser(){
        User user = sut.insert(getUser(""));
        assertNull(user);
    }


    @Test
    public void insertUsesDatabase(){
        User user = sut.insert(getUser("test"));
        assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    public void insertSavesValidUser(){
        User insertedUser = sut.insert(getUser("test"));
        assertEquals(insertedUser.getName(), repo.users.get(0).getName());
    }

    @Test
    public void getAllUsersUsesDatabase(){
        List<User> users = sut.getAllUsers();
        assertTrue(repo.calledMethods.contains("findAll"));
    }

    @Test
    public void getAllUsersEmptyDB(){
        List<User> users = sut.getAllUsers();
        assertEquals(users, new ArrayList<User>());
    }

    @Test
    public void getAllUsersTwoUsers(){
        sut.insert(getUser("test1"));
        sut.insert(getUser("test2"));
        List<User> users = sut.getAllUsers();
        assertEquals(users.get(0).getName(), "test1");
        assertEquals(users.get(1).getName(), "test2");
    }

    @Test
    public void getByIdUsesDBWithInvalidId(){
        sut.getById((long) 0);
        assertTrue(repo.calledMethods.contains("existsById"));
    }

    @Test
    public void getByIdUsesDBWithValidId(){
        User insertedUser = sut.insert(getUser("testUser"));
        sut.getById((long) 1);
        assertTrue(repo.calledMethods.contains("existsById"));
        assertTrue(repo.calledMethods.contains("findById"));
    }

    @Test
    public void getByIdFindsExistingUser(){
        User insertedUser = sut.insert(getUser("test"));
        Optional<User> optionalUser = sut.getById((long) 1);
        assertTrue(optionalUser.isPresent());
        assertEquals(optionalUser.get().getName(), insertedUser.getName());
    }

    @Test
    public void getByIdFindsNonExistingUser(){
        Optional<User> optionalUser = sut.getById((long) 1);
        assertFalse(optionalUser.isPresent());
    }


    @Test
    public void deleteByIdUsesDBWithInvalidId(){
        sut.deleteById((long) 0);
        assertTrue(repo.calledMethods.contains("existsById"));
    }

    @Test
    public void deleteByIdUsesDBWithValidId(){
        User insertedUser = sut.insert(getUser("testUser"));
        sut.deleteById((long) 1);
        assertTrue(repo.calledMethods.contains("existsById"));
        assertTrue(repo.calledMethods.contains("deleteById"));
    }

    @Test
    public void deleteByIdDeletesExistingUser(){
        User insertedUser = sut.insert(getUser("test"));

        Optional<User> optionalUserBefore = sut.getById((long) 1);
        assertTrue(optionalUserBefore.isPresent());

        sut.deleteById((long) 1);

        Optional<User> optionalUserAfter = sut.getById((long) 1);
        assertFalse(optionalUserAfter.isPresent());
    }

    @Test
    public void deleteByIdDoesNothingIfUserNonExistent(){
        User insertedUser = sut.insert(getUser("test"));
        assertEquals(1, repo.users.size());

        sut.deleteById((long) 24);

        assertEquals(1, repo.users.size());
    }

    @Test
    public void updateByIdUsesGetById(){
        sut.insert(getUser("testUser"));
        Optional<User> optionalUser = sut.updateById((long) 1, "newName");
        assertTrue(repo.calledMethods.contains("existsById"));
        assertTrue(repo.calledMethods.contains("findById"));
    }

    @Test
    public void updateByIdUsesDB(){
        sut.insert(getUser("testUser"));
        Optional<User> optionalUser = sut.updateById((long) 1, "newName");
        assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    public void updateByIdNonExistentUser(){
        Optional<User> optionalUser = sut.updateById((long) 1, "newName");
        assertTrue(optionalUser.isEmpty());
    }

    @Test
    public void updateByExistentUser(){
        sut.insert(getUser("testUser"));
        assertEquals(sut.getById((long) 1).get().getName(), "testUser");

        Optional<User> optionalUser = sut.updateById((long) 1, "newName");

        assertTrue(optionalUser.isPresent());
        assertEquals(optionalUser.get().getName(), "newName");
    }


    private static User getUser(String name) {
        return new User(name);
    }
}