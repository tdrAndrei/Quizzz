package commons.Messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

/**
 * The type Message.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CorrectAnswerMessage.class, name = "ShowCorrectAnswer"),
        @JsonSubTypes.Type(value = NewPlayersMessage.class, name = "NewPlayersMessage"),
        @JsonSubTypes.Type(value = NewQuestionMessage.class, name = "NewQuestion"),
        @JsonSubTypes.Type(value = NullMessage.class, name = "None"),
        @JsonSubTypes.Type(value = ReduceTimeMessage.class, name = "ReduceTime"),
        @JsonSubTypes.Type(value = ShowLeaderboardMessage.class, name = "ShowLeaderboard"),
})public class Message {
    /**
     * The Type.
     */
    protected String type;

    /**
     * Instantiates a new Message.
     */
    public Message() {
    }

    /**
     * Instantiates a new Message.
     *
     * @param type the type
     */
    public Message(String type) {
        this.type = type;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(type, message.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                '}';
    }
}

