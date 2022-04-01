package client.scenes;

import client.EmojiChat;
import client.utils.JavaFXUtility;
import client.utils.ServerUtils;
import commons.Activity;
import commons.Messages.NewPlayersMessage;
import commons.Messages.NewQuestionMessage;
import commons.Messages.NullMessage;
import commons.Messages.ShowLeaderboardMessage;
import commons.User;
import javafx.scene.Parent;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClientGameControllerTest {

    private ClientGameController clientGameController;

    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ServerUtils serverUtils;
    @Mock
    public EmojiChat emojiChat;
    @Mock
    public JavaFXUtility javaFXUtility;


    @BeforeEach
    void setUp() {
        when(serverUtils.joinMulti(any(User.class))).thenReturn(1L);
        when(serverUtils.getNumberOfQuestionsInGame(any(Long.class))).thenReturn(20);
        when(mainCtrl.getUser()).thenReturn(new User(1L, "fake-person :("));
        when(serverUtils.joinSolo(any(User.class))).thenReturn(1L);

        this.clientGameController = new ClientGameController(mainCtrl, serverUtils, emojiChat, javaFXUtility);
        clientGameController.setWaitingRoomController(new WaitingRoomController(serverUtils, clientGameController, mainCtrl));
        clientGameController.setLeaderboardSoloController(new LeaderboardSoloController(serverUtils, mainCtrl));

    }

    @Test
    public void testInitialize() {
        when(emojiChat.addClient(any())).thenReturn(emojiChat);
        Pair<MultiQuestionController, Parent> multiQuestion = new Pair<>(new MultiQuestionController(mainCtrl, clientGameController, serverUtils), null);
        Pair<EstimateQuestionController, Parent> estimateQuestion = new Pair<>(new EstimateQuestionController(mainCtrl, clientGameController, serverUtils), null);
        Pair<ChooseConsumptionController, Parent> chooseConsumption = new Pair<>(new ChooseConsumptionController(mainCtrl, clientGameController, serverUtils), null);
        Pair<LeaderboardSoloController, Parent> leaderboard = new Pair<>(new LeaderboardSoloController(serverUtils, mainCtrl), null);
        Pair<WaitingRoomController, Parent> waitingRoom = new Pair<>(new WaitingRoomController(serverUtils, clientGameController, mainCtrl), null);

        clientGameController.initialize(multiQuestion, estimateQuestion, chooseConsumption, leaderboard, waitingRoom);
        assertNotNull(clientGameController.getMultiQuestionController());
        assertNotNull(clientGameController.getEstimateQuestionController());
        assertNotNull(clientGameController.getChooseConsumptionController());
        assertNotNull(clientGameController.getLeaderboardSoloController());
        assertNotNull(clientGameController.getWaitingRoomController());
    }

    @Test
    public void testCalledAddClient() {
        when(emojiChat.addClient(any())).thenReturn(emojiChat);
        Pair<MultiQuestionController, Parent> multiQuestion = new Pair<>(new MultiQuestionController(mainCtrl, clientGameController, serverUtils), null);
        Pair<EstimateQuestionController, Parent> estimateQuestion = new Pair<>(new EstimateQuestionController(mainCtrl, clientGameController, serverUtils), null);
        Pair<ChooseConsumptionController, Parent> chooseConsumption = new Pair<>(new ChooseConsumptionController(mainCtrl, clientGameController, serverUtils), null);
        Pair<LeaderboardSoloController, Parent> leaderboard = new Pair<>(new LeaderboardSoloController(serverUtils, mainCtrl), null);
        Pair<WaitingRoomController, Parent> waitingRoom = new Pair<>(new WaitingRoomController(serverUtils, clientGameController, mainCtrl), null);

        clientGameController.initialize(multiQuestion, estimateQuestion, chooseConsumption, leaderboard, waitingRoom);
        verify(emojiChat, times(3)).addClient(any());
    }

    @Test
    public void startPollingMultiTrueTest() {
        //Prepared variables
        when(serverUtils.pollUpdate(eq(1L), any(Long.class))).thenReturn(new NullMessage("None"));

        //When
        clientGameController.startPolling(true);

        //Then (assertions)
        assertEquals(1L, clientGameController.getGameId());
        assertEquals(20, clientGameController.getQuestionsLeft());
        assertEquals(3, clientGameController.getAvailableJokers().size());
        assertTrue(clientGameController.getAvailableJokers().contains(Joker.REDUCETIME));
        assertTrue(clientGameController.getAvailableJokers().contains(Joker.ELIMINATE));
        assertTrue(clientGameController.getAvailableJokers().contains(Joker.DOUBLEPOINTS));

        assertEquals(ClientGameController.GameMode.MULTI, clientGameController.getGameMode());

        //Then (verification)
        verify(serverUtils, times(1)).getAndSetSession();
        verify(serverUtils, times(1)).registerForEmojiMessages(anyLong(), any());
        verify(emojiChat, times(1)).setVisibility(true);
        verify(emojiChat, times(1)).resetChat();
    }

    @Test
    public void startPollingEndGameMessageTest() throws InterruptedException {
        when(serverUtils.pollUpdate(eq(1L), any(Long.class))).thenReturn(new ShowLeaderboardMessage("RandomStuff", "End", List.of()));
        clientGameController.startPolling(true);
        Thread.sleep(501);
        assertEquals(ClientGameController.GameMode.NOT_PLAYING, clientGameController.getGameMode());
    }

    @Test
    public void startPollingSoloTest() {
        //Prepared variables
        when(serverUtils.pollUpdate(eq(1L), any(Long.class))).thenReturn(new NullMessage("None"));

        //When
        clientGameController.startPolling(false);

        //Then (assertions)
        assertEquals(1L, clientGameController.getGameId());
        assertEquals(20, clientGameController.getQuestionsLeft());
        assertEquals(ClientGameController.GameMode.SOLO, clientGameController.getGameMode());
        assertEquals(3, clientGameController.getAvailableJokers().size());
        assertTrue(clientGameController.getAvailableJokers().contains(Joker.SKIPQUESTION));
        assertTrue(clientGameController.getAvailableJokers().contains(Joker.ELIMINATE));
        assertTrue(clientGameController.getAvailableJokers().contains(Joker.DOUBLEPOINTS));

        //Then (verification)
        verify(serverUtils, times(0)).getAndSetSession();
        verify(serverUtils, times(0)).registerForEmojiMessages(anyLong(), any());
        verify(emojiChat, times(1)).setVisibility(false);
        verify(emojiChat, times(0)).resetChat();
    }

    @Test
    public void sendEmojiTest() {
        clientGameController.setGameId(1L);
        clientGameController.sendEmoji(1);
        verify(serverUtils, times(1)).sendEmojiTest(1L, "fake-person :(", 1, 1L);
    }

    @Test
    public void interpretMessageNoneTest() throws InterruptedException {
        clientGameController.interpretMessage(new NullMessage("None"));
        verifyNoInteractions(serverUtils, mainCtrl);
    }

    @Test
    public void interpretMessageNewPlayersTest() throws InterruptedException {
        WaitingRoomController waitingRoomControllerMock = mock(WaitingRoomController.class);
        clientGameController.setWaitingRoomController(waitingRoomControllerMock);
        clientGameController.interpretMessage(new NewPlayersMessage("NewPlayers", List.of()));

        verify(waitingRoomControllerMock, times(1)).showPlayers(List.of());
        verify(waitingRoomControllerMock, times(1)).showEntries();
    }

    @Test
    public void interpretMessageNewMCQuestionTest() throws InterruptedException {
        List<Activity> testActivityList = new ArrayList<>();
        List<byte[]> testImageBytes = new ArrayList<>();
        testImageBytes.add(new byte[]{Byte.parseByte("2")});
        testActivityList.add(new Activity(1L, "/", "test-activity-title", 30L, "/"));
        NewQuestionMessage newQuestionMessage = new NewQuestionMessage("NewQuestion", "MC", "test-title", testActivityList, 20.0, 200, null, testImageBytes);
        clientGameController.setRemainingJokers(List.of(Joker.REDUCETIME));
        clientGameController.setAvailableJokers(EnumSet.of(Joker.REDUCETIME));
        clientGameController.setQuestionsLeft(20);
        clientGameController.interpretMessage(newQuestionMessage);

        assertEquals(ClientGameController.GameState.NEW_QUESTION, clientGameController.getStatus());
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
    }
}



