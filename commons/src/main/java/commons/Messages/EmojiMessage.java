package commons.Messages;

public class EmojiMessage {
    private long userId;
    private int emojiIndex;
    private String username;

    public EmojiMessage(long userId, int emojiIndex, String username) {
        this.userId = userId;
        this.emojiIndex = emojiIndex;
        this.username = username;
    }

    public EmojiMessage() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getEmojiIndex() {
        return emojiIndex;
    }

    public void setEmojiIndex(int emojiIndex) {
        this.emojiIndex = emojiIndex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "EmojiMessage{" +
                "userId=" + userId +
                ", emojiIndex=" + emojiIndex +
                ", username='" + username + '\'' +
                '}';
    }
}
