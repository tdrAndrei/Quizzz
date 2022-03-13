package client.scenes;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import javafx.util.Duration;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * The type Main menu controller.
 */
public class MainMenuController implements Initializable {
    @FXML
    private AnchorPane mainMenuPane;
    @FXML
    private SVGPath rightWheelExterior;
    @FXML
    private SVGPath rightWheelInterior;
    @FXML
    private SVGPath leftWheelExterior;
    @FXML
    private SVGPath leftWheelInterior;
    @FXML
    private SVGPath lightning;
    @FXML
    private Label factQuestion;
    @FXML
    private Label factText;
    @FXML
    private Group lightBulb;
    @FXML
    private SVGPath bulb;
    @FXML
    private Group socket;
    @FXML
    private SVGPath mainSocket;
    @FXML
    private Group titleEdges;
    @FXML
    private SVGPath horizontalEdge;
    @FXML
    private SVGPath rvEdge;
    @FXML
    private SVGPath lvEdge;
    @FXML
    private GridPane grid;
    @FXML
    private Button soloButton;
    @FXML
    private Label underSoloText;
    @FXML
    private Button multiplayerButton;
    @FXML
    private Label underMultiplayerText;
    @FXML
    private VBox soloVBox;
    @FXML
    private VBox multiVBox;
    @FXML
    private Button leaderboardButton;
    @FXML
    private Button quitButton;

    private final MainCtrl mainCtrl;
    private Thread animator;
    private List<String> factsList;
    private double baseWidth;
    private double baseHeight;

    /**
     * Instantiates a new Main menu controller.
     *
     * @param mainCtrl the main ctrl
     */
    @Inject
    public MainMenuController(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        baseWidth = grid.getPrefWidth();
        baseHeight = grid.getPrefHeight();

        mainMenuPane.widthProperty().addListener(e -> {
            resizeScene(mainMenuPane.getWidth(), mainMenuPane.getHeight());
        });

        mainMenuPane.heightProperty().addListener(e -> {
            resizeScene(mainMenuPane.getWidth(), mainMenuPane.getHeight());
        });
    }

    /**
     * Quit.
     *
     * @throws IOException the io exception
     */
    public void quit() throws IOException {
        mainCtrl.quit();
    }

    /**
     * Start solo.
     * The method also stops the animation thread when it switches to the game screen
     */
    public void startSolo() {
        stopAnimatorThread();
        mainCtrl.showMultiQuestion();
    }

    /**
     * Show leaderboard.
     * The method also stops the animation thread when it switches to the leaderboard screen
     */
    public void showLeaderboard() {
        stopAnimatorThread();
        mainCtrl.showLeaderboardSolo();
    }

    public void resizeScene(double width, double height) {
        if (width < grid.getMinWidth() || height < grid.getMinHeight())
            return;
        grid.setPrefSize(width, height);

        double ratioH = height / baseHeight;
        double ratioW = width / baseWidth;
        double scaleForText = 0.5 * (ratioW + ratioH);

        if (!(ratioH > 0 && ratioH < 3 && ratioW > 0 && ratioW < 3))
            return;

        soloVBox.setPrefWidth(Math.min(ratioW * soloVBox.getMinWidth(), 1.5 * soloVBox.getMinWidth()));
        soloVBox.setPrefHeight(Math.min(ratioH * soloVBox.getMinHeight(), 1.5 * soloVBox.getMinHeight()));
        setFontSize(soloButton, 20, 30, scaleForText);
        setFontSize(underSoloText, 15, 20, scaleForText);

        multiVBox.setPrefWidth(Math.min(ratioW * multiVBox.getMinWidth(), 1.5 * multiVBox.getMinWidth()));
        multiVBox.setPrefHeight(Math.min(ratioH * multiVBox.getMinHeight(), 1.5 * multiVBox.getMinHeight()));
        setFontSize(multiplayerButton, 20, 30, scaleForText);
        setFontSize(underMultiplayerText, 15, 20, scaleForText);

        leaderboardButton.setPrefHeight(Math.min(ratioH * leaderboardButton.getMinHeight(), 1.5 * leaderboardButton.getMinHeight()));
        leaderboardButton.setPrefWidth(Math.min(ratioW * leaderboardButton.getMinWidth(), 1.5 * leaderboardButton.getMinWidth()));
        setFontSize(leaderboardButton, 20, 30, scaleForText);

        quitButton.setPrefHeight(Math.min(ratioH * quitButton.getMinHeight(), 1.5 * quitButton.getMinHeight()));
        quitButton.setPrefWidth(Math.min(ratioW * quitButton.getMinWidth(), 1.5 * quitButton.getMinWidth()));
        setFontSize(quitButton, 18, 25, scaleForText);

        setFontSize(factQuestion, 20, 30, scaleForText);
        setFontSize(factText, 18, 25, scaleForText);
    }

    /**
     * Make animations: main method which starts all the animations from the main screen and creates the factsList.
     */
    public void makeAnimations() {

        resizeSvg(rightWheelExterior, 100, 100);
        resizeSvg(rightWheelInterior, 55, 55);
        resizeSvg(leftWheelExterior, 100, 100);
        resizeSvg(leftWheelInterior, 55, 55);
        resizeSvg(bulb, 67, 73);
        resizeSvg(mainSocket, 40, 40);

        makeRotateAnimation(rightWheelExterior);
        makeRotateAnimation(leftWheelExterior);
        makeStrokeTransition();

        factsList = createFactsList();
        getAnimatingText();

    }

    /**
     * Make fill transition.
     * Fill transition starts when one clicks on the bulb vector / socket vector, and it acts upon both groups the vectors take part in
     */
    public void makeFillTransition() {

        FillTransition fill;

        for (Node shape : lightBulb.getChildren()) {
            fill = new FillTransition(Duration.millis(4000), (SVGPath) shape, Color.BLACK, Color.YELLOW);
            fill.setCycleCount(2);
            fill.setAutoReverse(true);
            fill.play();
        }

        for (Node shape : socket.getChildren()) {
            fill = new FillTransition(Duration.millis(4000), (SVGPath) shape, Color.BLACK, Color.YELLOW);
            fill.setCycleCount(2);
            fill.setAutoReverse(true);
            fill.play();
        }

    }

    /**
     * Make stroke transition.
     */
    public void makeStrokeTransition() {

        StrokeTransition stroke = new StrokeTransition(Duration.millis(3000), lightning, Color.BLACK, Color.YELLOW);
        stroke.setCycleCount(Animation.INDEFINITE);
        stroke.setAutoReverse(true);
        stroke.play();

    }

    /**
     * Get randomly animating text.
     * Each time a text is shown on the screen, it is taken randomly from the factsList.
     * The method calls animateTypingText() where the actual animation takes place.
     */
    public void getAnimatingText() {

        Random getRandomText = new Random();
        int i = getRandomText.nextInt(factsList.size());
        animateTypingText(factsList.get(i));

    }

    /**
     * Animate typing text.
     * The animation plays in a new thread (animator)
     *
     * @param text the text
     */
    public void animateTypingText(String text) {

        Random random = new Random();
        animator = new Thread(() -> {
            try {
                for (int i = 0; i <= text.length(); i++) {

                    String textAtThisPoint;
                    if (i < text.length())
                        textAtThisPoint = text.substring(0, i) + "_";
                    else
                        textAtThisPoint = text.substring(0, i);
                    int randomNr = random.nextInt(200);
                    Platform.runLater(() -> {
                        factText.setText(textAtThisPoint);
                    });
                    Thread.sleep(100 + randomNr);                   //in order to mimic a person's typing
                }
            } catch (InterruptedException ignored) {
                return; //exit thread
            }

            try {
                Thread.sleep(2000);                                //text which has just been written stays on screen for 2 seconds before another one is generated
            } catch (InterruptedException ignored) {
                return; //exit thread
            }

            Platform.runLater(() -> {                                  //proceed to get another random text
                getAnimatingText();
            });
            stopAnimatorThread();                                     //interrupts the thread

        });
        animator.start();

    }

    /**
     * Interrupts the animator thread.
     */
    public void stopAnimatorThread() {
        if (animator != null)
            animator.interrupt();
    }

    /**
     * Makes the rotation animation of the gears vectors
     *
     * @param svg
     */
    public void makeRotateAnimation(SVGPath svg) {

        RotateTransition rotate = new RotateTransition(Duration.seconds(3), svg);
        rotate.setCycleCount(Animation.INDEFINITE);  //set the rotation period to infinity
        rotate.setInterpolator(Interpolator.LINEAR); //makes the rotation run linearly
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.play();

    }

    /**
     * Method to resize SVGs
     *
     * @param svg
     * @param width  width to resize to
     * @param height height to resize to
     */
    public void resizeSvg(SVGPath svg, double width, double height) {

        double originalWidth = svg.prefWidth(-1);
        double originalHeight = svg.prefHeight(originalWidth);

        double scaleX = width / originalWidth;
        double scaleY = height / originalHeight;

        svg.setScaleX(scaleX);
        svg.setScaleY(scaleY);

    }

    public void setFontSize(Labeled object, double minSize, double maxSize, double scale) {
        object.setStyle("-fx-font-size: " + Math.min(scale * minSize, maxSize) + "px");
    }

    /**
     * Create factsList list.
     * The list has some hardcoded facts about energy consumption, which will be randomly generated on the screen
     *
     * @return the list
     */
    public List<String> createFactsList() {

        factsList = new ArrayList<>();
        String text = "Our demand for energy grows by about 3% per year?";
        factsList.add(text);
        text = "We spend 10% of our electricity bills on lighting?";
        factsList.add(text);
        text = "A single lightning bolt unleashes five times more heat than the sun?";
        factsList.add(text);
        text = "60 minutes of solar energy could power the Earth for a year?";
        factsList.add(text);
        text = "10 Google searches can power a 60-watt lightbulb?";
        factsList.add(text);
        text = "A single wind turbine can power 1400 homes?";
        factsList.add(text);
        text = "We can get energy from trash and sewage?";
        factsList.add(text);
        text = "A water heater can consume around up to 13.5 kWh per day, or 405 kWh per month?";
        factsList.add(text);
        text = "More than 20% of energy consumed worldwide is used for transportation?";
        factsList.add(text);
        text = "Oil is the most consumed primary energy fuel in the world?";
        factsList.add(text);
        text = "China is by far the largest electricity-generating country in the world?";
        factsList.add(text);
        text = "Germany produces so much free electricity, it pays people to use it?";
        factsList.add(text);
        text = "The energy problem that receives most attention is the link between energy access and greenhouse gas emissions?";
        factsList.add(text);
        text = "Natural gas is arguably the most important energy source in the Netherlands?";
        factsList.add(text);
        text = "Approximately 30% of energy used in buildings is used inefficiently or unnecessarily?";
        factsList.add(text);
        text = "Food contains energy, which is measured in calories or joules?";
        factsList.add(text);
        return factsList;

    }
}

