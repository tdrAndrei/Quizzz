package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import commons.Messages.Message;
import commons.Messages.NullMessage;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import server.service.GameManager;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GameControllerTest {

    private GameController controller;
    @Mock
    private GameManager manager;

    @BeforeEach
    public void setup(){
        controller = new GameController(manager);
    }

    @Test
    public void joinSoloSuccessTest(){
        User u = new User(1L, "test");
        when(manager.joinSolo(u)).thenReturn(1L);
        var res = controller.joinSolo(u);
        verify(manager, times(1)).joinSolo(u);
        assertEquals(OK, res.getStatusCode());
        assertEquals(1L, res.getBody());
    }

    @Test
    public void joinSoloUnsuccessfulTest(){
        when(manager.joinSolo(null)).thenReturn(-1L);
        var res = controller.joinSolo(null);
        verify(manager, times(1)).joinSolo(null);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void joinMultiSuccessTest(){
        User u = new User(1L, "test");
        when(manager.joinMulti(u)).thenReturn(1L);
        var res = controller.joinMulti(u);
        verify(manager, times(1)).joinMulti(u);
        assertEquals(OK, res.getStatusCode());
        assertEquals(1L, res.getBody());
    }

    @Test
    public void joinMultiUnsuccessfulTest(){
        when(manager.joinMulti(null)).thenReturn(-1L);
        var res = controller.joinMulti(null);
        verify(manager, times(1)).joinMulti(null);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void pollUpdateTest(){
        Message m = new NullMessage("None");
        when(manager.getUpdate(1L, 1L)).thenReturn(m);
        var res = controller.pollUpdate(1L, 1L);
        verify(manager, times(1)).getUpdate(1L, 1L);
        assertEquals(OK, res.getStatusCode());
        assertEquals(m, res.getBody());
    }

    @Test
    public void pollUpdateTestInvalidID(){
        when(manager.getUpdate(-1L, 1L)).thenReturn(null);
        var res = controller.pollUpdate(-1L, 1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void getNumberOfQuestionsInGameTest(){
        when(manager.getNumberOfQuestionsInGame(1L)).thenReturn(20);
        var res = controller.getNumberOfQuestionsInGame(1L);
        verify(manager, times(1)).getNumberOfQuestionsInGame(1L);
        assertEquals(OK, res.getStatusCode());
        assertEquals(20, res.getBody());
    }

    @Test
    public void getNumberOfQuestionsInGameTestInvalidID(){
        var res = controller.getNumberOfQuestionsInGame(-1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void startGameTest(){
        var res = controller.startGame(1L);
        verify(manager, times(1)).startGame(1L);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void startGameTestInvalidID(){
        var res = controller.startGame(-1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void submitAnswerTest(){
        var res = controller.submitAnswer(1L, 1L, 1L);
        verify(manager, times(1)).processAnswer(1L, 1L, 1L);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void submitAnswerTestInvalidID(){
        var res = controller.submitAnswer(-1L, 1L, 1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void leaveGameTest(){
        var res = controller.leaveGame(1L, 1L);
        verify(manager, times(1)).disconnectPlayer(1L, 1L);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void leaveGameTestInvalidID(){
        var res = controller.leaveGame(-1L, 1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void useEliminateJokerTest(){
        when(manager.useEliminateAnswer(1L)).thenReturn(2L);
        var res = controller.useEliminateJoker(1L);
        verify(manager, times(1)).useEliminateAnswer(1L);
        assertEquals(OK, res.getStatusCode());
        assertEquals(2L, res.getBody());
    }

    @Test
    public void useEliminateJokerTestInvalidID(){
        var res = controller.useEliminateJoker(-1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void useNewQuestionJokerTest(){
        var res = controller.useNewQuestionJoker(1L);
        verify(manager, times(1)).useNewQuestionJoker(1L);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void useNewQuestionJokerTestInvalidID(){
        var res = controller.useNewQuestionJoker(-1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void useDoublePointsJokerTest(){
        var res = controller.useDoublePointsJoker(1L, 1L);
        verify(manager, times(1)).useDoublePointsJoker(1L, 1L);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void useDoublePointsJokerTestInvalidID(){
        var res = controller.useDoublePointsJoker(-1L, 1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void useTimeJokerTest() {
        var res = controller.useTimeJoker(1L, 1L);
        verify(manager, times(1)).useTimeJoker(1L, 1L);
        assertEquals(OK, res.getStatusCode());
    }

    @Test
    public void useTimeJokerTestInvalidID(){
        var res = controller.useTimeJoker(-1L, 1L);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
}
