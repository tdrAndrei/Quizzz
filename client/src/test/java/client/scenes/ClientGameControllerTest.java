package client.scenes;

import client.EmojiChat;
import client.utils.JavaFXUtility;
import client.utils.ServerUtils;
import commons.Activity;
import commons.Messages.*;
import commons.User;
import javafx.scene.Parent;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        clientGameController.setQuestionsLeft(20);
        clientGameController.setGameId(1L);
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
    public void interpretMessageNewQuestionTest() throws InterruptedException {
        List<Activity> testActivityList = new ArrayList<>();
        List<byte[]> testImageBytes = new ArrayList<>();
        testImageBytes.add(new byte[]{Byte.parseByte("2")});
        testActivityList.add(new Activity(1L, "/", "test-activity-title", 30L, "/"));
        NewQuestionMessage newQuestionMessage = new NewQuestionMessage("NewQuestion", "MC", "test-title", testActivityList, 20.0, 200, null, testImageBytes);
        clientGameController.setRemainingJokers(List.of(Joker.REDUCETIME));
        clientGameController.setAvailableJokers(EnumSet.of(Joker.REDUCETIME));
        clientGameController.interpretMessage(newQuestionMessage);

        assertEquals(ClientGameController.GameState.NEW_QUESTION, clientGameController.getStatus());
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
    }


    @Test
    public void interpretMessageShowLeaderboardEndSoloTest() throws InterruptedException {
        clientGameController.interpretMessage(new ShowLeaderboardMessage("ShowLeaderboard", "End", List.of(), 1L));

        assertEquals(ClientGameController.GameState.SHOW_LEADERBOARD, clientGameController.getStatus());
        verifyNoInteractions(serverUtils, mainCtrl);
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
    }

    @Test
    public void interpretMessageShowLeaderboardEndMultiTest() throws InterruptedException {
        clientGameController.interpretMessage(new ShowLeaderboardMessage("ShowLeaderboard", "End", List.of()));

        assertEquals(ClientGameController.GameState.SHOW_LEADERBOARD, clientGameController.getStatus());
        verifyNoInteractions(serverUtils, mainCtrl);
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
    }

    @Test
    public void interpretMessageShowLeaderboardMidMultiTest() throws InterruptedException {
        clientGameController.interpretMessage(new ShowLeaderboardMessage("ShowLeaderboard", "Mid", List.of(), 1L));

        assertEquals(ClientGameController.GameState.SHOW_LEADERBOARD, clientGameController.getStatus());
        verifyNoInteractions(serverUtils, mainCtrl);
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
    }

    @Test
    public void interpretMessageShowCorrectAnswerTest() throws InterruptedException {
        // Preparation
        clientGameController.setAvailableJokers(EnumSet.of(Joker.REDUCETIME));
        //Method invocation
        clientGameController.interpretMessage(new CorrectAnswerMessage("ShowCorrectAnswer", 200, 1L));
        //Tests
        assertEquals(ClientGameController.GameState.SHOW_ANSWER, clientGameController.getStatus());
        assertEquals(clientGameController.getQuestionsLeft(), 19);
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
    }

    @Test
    public void interpretMessageReduceTimeTest() throws InterruptedException {
        // Preparation
        clientGameController.setAvailableJokers(EnumSet.of(Joker.REDUCETIME));
        //Method invocation
        clientGameController.interpretMessage(new ReduceTimeMessage("ReduceTime", "test-user-2", 4.00));
        //Tests
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
        verifyNoInteractions(mainCtrl, serverUtils, emojiChat);
    }

    @Test
    public void useJokerNoAvailableTest () {
        // Preparation
        clientGameController.setAvailableJokers(EnumSet.noneOf(Joker.class));
        // Invocation
        clientGameController.useJoker(Joker.REDUCETIME, Assertions::fail);
        verifyNoInteractions(serverUtils, javaFXUtility, emojiChat, mainCtrl);
    }

    @Test
    public void useJokerAvailableTest() {
        // Preparation
        clientGameController.setAvailableJokers(EnumSet.of(Joker.REDUCETIME));
        clientGameController.setRemainingJokers(List.of(Joker.REDUCETIME));
        // Invocation
        clientGameController.useJoker(Joker.REDUCETIME, () -> {});

        assertTrue(clientGameController.getAvailableJokers().isEmpty());
        assertEquals(clientGameController.getRemainingJokers().get(0), Joker.USED);

        verify(mainCtrl, times(1)).setJokerPics(List.of(Joker.USED));
    }

    @Test
    public void exitGameTest() {
        clientGameController.exitGame();

        assertEquals(ClientGameController.GameMode.NOT_PLAYING, clientGameController.getGameMode());

        verify(serverUtils, times(1)).leaveGame(1L, 1L);
        verify(mainCtrl, times(1)).showMainMenu();
    }

    @Test
    public void isPlayingTest() {
        assertFalse(clientGameController.isPlaying());
    }

    @Test
    public void isPlayingFalseTest() {
        clientGameController.setGameMode(ClientGameController.GameMode.MULTI);

        assertTrue(clientGameController.isPlaying());
    }

    @Test
    public void submitAnswerTest() {
        clientGameController.setStatus(ClientGameController.GameState.NEW_QUESTION);
        clientGameController.setTimeLeft(20.00);
        clientGameController.setAvailableJokers(EnumSet.of(Joker.ELIMINATE));

        assertFalse(clientGameController.getAvailableJokers().isEmpty());

        clientGameController.submitAnswer(1L);

        assertEquals(ClientGameController.GameState.SUBMITTED_ANSWER, clientGameController.getStatus());
        assertTrue(clientGameController.getAvailableJokers().isEmpty());
        verify(mainCtrl, times(1)).lockCurrentScene();
        verify(serverUtils, times(1)).submitAnswer(1L, 1L, 1L);
    }

    @Test
    public void submitAnswerNoNewQuestionStateTest() {
        clientGameController.setTimeLeft(20.00);
        clientGameController.setAvailableJokers(EnumSet.of(Joker.ELIMINATE));

        assertFalse(clientGameController.getAvailableJokers().isEmpty());

        clientGameController.submitAnswer(1L);
        verifyNoInteractions(mainCtrl, serverUtils, emojiChat, javaFXUtility);
    }

    @Test
    public void doublePointTest() {
        clientGameController.doublePoint();
        
        verify(serverUtils, times(1)).useDoublePointsJoker(1L, 1L);
    }
    
    @Test
    public void eliminateJokerTest() {
        clientGameController.eliminateJoker();
        verify(serverUtils, times(1)).eliminateJoker(1L);
    }

    @Test
    public void skipQuestionTest() {
        clientGameController.skipQuestion();
        verify(serverUtils, times(1)).useNewQuestionJoker(1L);
    }

    @Test
    public void timeJokerTest() {
        clientGameController.timeJoker();
        verify(serverUtils, times(1)).useTimeJoker(1L, 1L);
    }

    @Test
    public void updateTimeLeftTest() {
        clientGameController.updateTimeLeft(3, 14.00);
        assertEquals(clientGameController.getTimeLeft(), 11.00);
    }

    @Test
    public void updateProgressBarColorTest() {
        clientGameController.updateProgressBarColor(15.00, 20.00, null);
        verify(javaFXUtility, times(1)).setStyle(any(), eq("-fx-accent: rgb(128, 255, 0);"));
    }

    @Test
    public void updateProgressBarColorLessTimeTest() {
        clientGameController.updateProgressBarColor(9.00, 20.00, null);
        verify(javaFXUtility, times(1)).setStyle(any(), eq("-fx-accent: rgb(255, 230, 0);"));
    }

    @Test
    public void startTimeTest() throws InterruptedException {
        clientGameController.startTimer(null, null);
        Thread.sleep(50);
        verify(javaFXUtility, times(1)).queueJFXThread(any(Runnable.class));
    }

    @Test
    public void updateProgressBarTest() {
        clientGameController.updateProgressBar(15.00, 20.00, null, null);

        verify(javaFXUtility, times(1)).setStyle(any(), eq("-fx-accent: rgb(128, 255, 0);"));
        verify(javaFXUtility, times(1)).setProgress(any(), eq(0.75d));
        verify(javaFXUtility, times(1)).setText(any(), eq("15S"));
    }

    @Test
    public void updateProgressBarNoTimeLeftTest() {
        clientGameController.updateProgressBar(0.0, 20.00, null, null);

        verify(javaFXUtility, times(1)).setProgress(any(), eq(0.0d));
        verify(javaFXUtility, times(1)).setText(any(), eq("0S"));
        verify(mainCtrl, times(1)).lockCurrentScene();
    }

    @Test
    public void changeScoreTest() {
        when(javaFXUtility.getText(any())).thenReturn("200 pts");
        ArgumentCaptor<String> captorSetText = ArgumentCaptor.forClass(String.class);
        clientGameController.changeScore(220, null, null);
        verify(javaFXUtility, times(1)).setText(any(), captorSetText.capture());
        verify(javaFXUtility, times(1)).setStyle(any(), eq("-fx-text-fill: rgb(0, 210, 28);"));
        verify(javaFXUtility, times(1)).playPointsAnimation(any());
        verify(mainCtrl, times(1)).setPointsForAllScenes(220);
        assertEquals(" + 20", captorSetText.getAllValues().get(0));
    }
}



