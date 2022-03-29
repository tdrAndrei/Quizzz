package server.api;

import commons.Messages.EmojiMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class EmojiController {

    @MessageMapping("/emoji/{gameId}") // -> /app/emoji/{gameId}
    @SendTo("/topic/emoji/{gameId}")
    public EmojiMessage getEmoji(EmojiMessage receivedMessage, @DestinationVariable long gameId) {
        return receivedMessage;
    }
}
