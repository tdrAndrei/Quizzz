package server.api;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class EmojiController {
    @MessageMapping("/emoji/{gameId}") // -> app/emoji/{gameId}
    @SendTo("/topic/emoji/{gameId}")
    public Integer getEmoji(int a, @DestinationVariable long gameId) {
        return a;
    }
}
