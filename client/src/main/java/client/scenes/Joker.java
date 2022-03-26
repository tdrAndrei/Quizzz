package client.scenes;

public enum Joker {

    ELIMINATE("/client.photos/jokerOneAnswer.png"),

    DOUBLEPOINTS("/client.photos/doubleJoker.png"),

    SKIPQUESTION("/client.photos/skipJoker.png"),

    REDUCETIME("/client.photos/timeJoker.png"),

    USED("/client.photos/usedJoker.png");

    private String path;

    Joker(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
