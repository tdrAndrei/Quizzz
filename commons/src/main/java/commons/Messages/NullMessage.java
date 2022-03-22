package commons.Messages;

import com.fasterxml.jackson.annotation.JsonTypeName;
import commons.Messages.Message;

@JsonTypeName("None")
public class NullMessage extends Message {

    public NullMessage() {
    }

    public NullMessage(String type) {
        super(type);

    }

}
