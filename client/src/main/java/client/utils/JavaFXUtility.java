package client.utils;

import javafx.application.Platform;

import java.util.function.Function;

public class JavaFXUtility {
    public JavaFXUtility() {
    }

    public void queueJFXThread(Runnable functionToEnqueue) {
        Platform.runLater(functionToEnqueue);
    }
}
