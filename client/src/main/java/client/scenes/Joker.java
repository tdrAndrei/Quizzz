package client.scenes;

import javafx.scene.image.Image;

public enum Joker {

    ELIMINATE("/client.photos/jokerOneAnswer.png"),

    DOUBLEPOINTS("/client.photos/doubleJoker.png"),

    SKIPQUESTION("/client.photos/skipJoker.png"),

    REDUCETIME("/client.photos/timeJoker.png"),

    USED("/client.photos/usedJoker.png");

    private String path;
    private Image pic;
    Joker(String path) {
        this.path = path;
        this.pic = new Image(path);
    }

    public String getPath() {
        return this.path;
    }
    public Image getPic() {
        return this.pic;
    }
}
