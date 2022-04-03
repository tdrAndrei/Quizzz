package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import server.service.TestUserRepository;
import server.service.UserService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserControllerTest {

    private int size = 0;
    private UserController controller;
    private TestUserRepository repo;
    @Mock
    private UserService service;

    @BeforeEach
    public void setup(){
        this.repo = new TestUserRepository();
        controller = new UserController(service);
    }

    @Test
    public void getUsersTest(){
        controller.getUsers();
        verify(service, times(1)).getAllUsers();
    }

    @Test
    public void registerNewUserSuccessfulTest(){
        User u = new User(1L, "marc");
        when(service.insert(u)).thenReturn(u);
        var res = controller.registerNewUser(u);
        verify(service, times(1)).insert(u);
        assertEquals(OK, res.getStatusCode());
        assertEquals(u, res.getBody());
    }

    @Test
    public void cannotRegisterNullUserTest(){
        when(service.insert(null)).thenReturn(null);
        var res = controller.registerNewUser(null);
        verify(service, times(1)).insert(null);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void deleteUserSuccessfulTest(){
        when(service.deleteById(1L)).thenReturn(true);
        var res = controller.deleteUser(1L);
        verify(service, times(1)).deleteById(1L);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void deleteUserUnsuccessfulTest(){
        when(service.deleteById(-1L)).thenReturn(false);
        var res = controller.deleteUser(-1L);
        verify(service, times(1)).deleteById(-1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void updateUserSuccessfulTest(){
        User u = new User(1L, "marc");
        when(service.updateById(1L, "marc")).thenReturn(Optional.of(u));
        var res = controller.updateUser(1L, "marc");
        verify(service, times(1)).updateById(1L, "marc");
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void updateUserUnsuccessfulTest(){
        when(service.updateById(-1L, "marc")).thenReturn(Optional.empty());
        var res = controller.updateUser(-1L, "marc");
        verify(service, times(1)).updateById(-1L, "marc");
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
}
