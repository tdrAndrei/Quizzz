package client.utils;

import javafx.application.Platform;

public class JavaFXUtility {
    public JavaFXUtility() {
    }

    public void queueJFXThread(Runnable functionToEnqueue) {
        Platform.runLater(functionToEnqueue);
    }
}
