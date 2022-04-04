package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import server.service.ActivityEntryService;
import server.service.TestActivityRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ActivityEntryControllerTest {

    private int size = 4;
    private ActivityEntryController controller;
    private TestActivityRepository repo;
    @Mock
    private ActivityEntryService service;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        this.repo = new TestActivityRepository();
        controller = new ActivityEntryController(service, repo);
    }

    @Test
    public void getAllEntriesTest(){
        controller.getEntries();
        verify(service, times(1)).getAllEntries();
    }

    @Test
    public void addActivitySuccessfulTest(){
        Activity a = new Activity(1L, "test", "test", 5L, "test");
        var res = controller.addActivity(a);
        size++;
        assertTrue(repo.calledMethods.contains("save"));
        assertEquals(repo.activityList.size(), size);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void addActivityUnsuccessfulTest(){
        Activity a = new Activity(1L, "test", null, 5L, "test");
        var res = controller.addActivity(a);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void updateActivitySuccessfulTest(){
        Activity a = new Activity(1L, "test", "test", 5L, "test");
        when(service.updateById(1L, "test", 5L, "test")).thenReturn(Optional.of(a));
        var res = controller.updateActivity(1L, "test", 5L, "test");
        verify(service, times(1)).updateById(1L, "test", 5L, "test");
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void updateActivityUnsuccessfulTest(){
        when(service.updateById(-3L, "test", 5L, "test")).thenReturn(Optional.empty());
        var res = controller.updateActivity(-3L, "test", 5L, "test");
        verify(service, times(1)).updateById(-3L, "test", 5L, "test");
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void getByIdValidTest(){
        Activity a = new Activity(1L, "test", "test", 5L, "test");
        when(service.findById(1L)).thenReturn(a);
        var res = controller.getById(1L);
        verify(service, times(2)).findById(1L);
        assertEquals(OK, res.getStatusCode());
        assertEquals(a, res.getBody());
    }

    @Test
    public void getByIdInvalidTest(){
        when(service.findById(-1L)).thenReturn(null);
        var res = controller.getById(-1L);
        verify(service, times(1)).findById(-1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
}
