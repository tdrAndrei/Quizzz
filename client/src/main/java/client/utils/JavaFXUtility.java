package client.utils;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;

public class JavaFXUtility {
    public JavaFXUtility() {
    }

    public void queueJFXThread(Runnable functionToEnqueue) {
        Platform.runLater(functionToEnqueue);
    }
    public void setStyle(Node node, String style) {
        node.setStyle(style);
    }
    public void setText(Label label, String text) {
        label.setText(text);
    }
    public void setProgress(ProgressBar progressBar, double value) {
        progressBar.setProgress(value);
    }
    public String getText(Label label) {
        return label.getText();
    }
    public void playPointsAnimation(Label newPoints) {
        Path moveVertically = new Path();
        moveVertically.getElements().add(new MoveTo(0, 0));
        moveVertically.getElements().add(new VLineTo(-100));

        PathTransition fadeOut = new PathTransition(Duration.seconds(3), moveVertically, newPoints);
        fadeOut.play();
    }
}
