/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import commons.Activity;
import commons.LeaderboardEntry;
import commons.Messages.EmojiMessage;
import commons.Messages.Message;
import commons.User;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

public class ServerUtils {

    private static String SERVER = "";
    private StompSession session;
    public void setUrl(String url) {
        SERVER = url;
    }

    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    public String generateWebsocketURL() {
        String intermediate;
        if (SERVER.contains("https")) {
            intermediate = SERVER.replace("https", "");
        } else {
            intermediate = SERVER.replace("http", "");
        }
        return "ws" + intermediate + "websocket";
    }

    public void getAndSetSession() {
        String websocketURL = generateWebsocketURL();
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            this.session = stomp.connect(websocketURL, new StompSessionHandlerAdapter() {}).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void registerForEmojiMessages(long gameId, Consumer<EmojiMessage> q) {
        session.subscribe("/topic/emoji/" + gameId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return EmojiMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                EmojiMessage receivedMessage = (EmojiMessage) payload;
                q.accept(receivedMessage);
            }
        });
    }

    public void sendEmojiTest(long gameId, String username, int emojiIndex, long userId) {
        session.send("/app/emoji/" + gameId, new EmojiMessage(userId, emojiIndex, username));
    }

    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    public List<LeaderboardEntry> getLeaderboardEntries() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/scores") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<LeaderboardEntry>>() {});
    }

    public List<Activity> getActivties() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/admin/display") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Activity>>() {});
    }

    public LeaderboardEntry addLeaderboardEntry(LeaderboardEntry leaderboardEntry) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/scores") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(leaderboardEntry, APPLICATION_JSON), LeaderboardEntry.class);
    }

    public void deleteSelf(User user) {
         ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user/" + user.getId()) //
                .request(TEXT_PLAIN) //
                .accept(TEXT_PLAIN) //
                .delete();
    }

    public User addUser(User user) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/user") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(user, APPLICATION_JSON), User.class);
    }

    public long joinSolo(User user){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/joinSolo")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), Long.class);
    }

    public long joinMulti(User user){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/joinMulti")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), Long.class);
    }

    public Message pollUpdate(long gameId, long userId){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/" + gameId)
                .queryParam("userId", userId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Message.class);
    }

    public void startGame(long gameId){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/start/" + gameId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
    }

    public void submitAnswer(long gameId, long userId, long userAnswer){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/submit/" + gameId)
                .queryParam("userId", userId)
                .queryParam("userAnswer", userAnswer)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
    }

    public void leaveGame(long gameId, long userId){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/leave/" + gameId)
                .queryParam("userId", userId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
    }

    public long eliminateJoker(long gameId){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/joker/eliminate/" + gameId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Long.class);
    }

    public void useNewQuestionJoker(long gameId){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/joker/new/" + gameId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
    }

    public void useTimeJoker(long gameId, long userId){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/joker/time/" + gameId)
                .queryParam("userId", userId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
    }

    public void useDoublePointsJoker(long gameId, long userId){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/joker/double/" + gameId)
                .queryParam("userId", userId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
    }
    public Activity addActivity(Activity activity) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/admin/add") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    public void updateActivity(Activity activity, String title, long consumption, String source){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/admin/edit/" + activity.getId())
                .queryParam("title", title)
                .queryParam("consumption", consumption)
                .queryParam("source", source)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    public Activity getActivityById(long id){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/admin/getById/" + id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Activity>() {});
    }

    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }
    public Message getUpdate() {
        return new Message("newMessage");
    }
}