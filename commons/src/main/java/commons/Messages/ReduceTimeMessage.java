package commons.Messages;

import com.fasterxml.jackson.annotation.JsonTypeName;
import commons.Messages.Message;

@JsonTypeName("ReduceTime")
public class ReduceTimeMessage extends Message {

    String userName; // player who did this to you.
    double newTime;

    public ReduceTimeMessage() {
    }

    public ReduceTimeMessage(String type, String user, double newTime) {
        super(type);
        this.userName = user;
        this.newTime = newTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getNewTime() {
        return newTime;
    }

    public void setNewTime(Integer newTime) {
        this.newTime = newTime;
    }
}
