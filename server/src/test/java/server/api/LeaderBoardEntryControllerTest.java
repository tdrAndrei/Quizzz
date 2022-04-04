package server.api;

import commons.LeaderboardEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import server.service.LeaderBoardEntryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LeaderBoardEntryControllerTest {

    private LeaderBoardEntryController controller;
    @Mock
    private LeaderBoardEntryService service;

    @BeforeEach
    public void setup(){
        controller = new LeaderBoardEntryController(service);
    }

    @Test
    public void getAllEntriesTest(){
        controller.getEntries();
        verify(service, times(1)).getAllEntries();
    }

    @Test
    public void addLeaderBoardEntrySuccessful(){
        LeaderboardEntry l = new LeaderboardEntry(1L, "marc", 500);
        when(service.insert(l)).thenReturn(l);
        var res = controller.addLeaderboardEntry(l);
        verify(service, times(1)).insert(l);
        assertEquals(OK, res.getStatusCode());
        assertEquals(l, res.getBody());
    }

    @Test
    public void addActivityUnsuccessfulTest(){
        LeaderboardEntry l = new LeaderboardEntry(1L, "", 500);
        when(service.insert(l)).thenReturn(null);
        var res = controller.addLeaderboardEntry(l);
        verify(service, times(1)).insert(l);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
}
