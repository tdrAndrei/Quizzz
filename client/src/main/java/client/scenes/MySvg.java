package client.scenes;

import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class MySvg {

    private SVGPath svg;
    private double scaleX;
    private double scaleY;

    public MySvg(SVGPath svg){
        this.svg = svg;
        scaleX = svg.getScaleX();
        scaleY = svg.getScaleY();
    }

    public MySvg(SVGPath svg, double width, double height) {

        this.svg = svg;

        double originalWidth = svg.prefWidth(-1);
        double originalHeight = svg.prefHeight(originalWidth);

        scaleX = width / originalWidth;
        scaleY = height / originalHeight;

        svg.setScaleX(scaleX);
        svg.setScaleY(scaleY);

    }

    public SVGPath getSvg() {
        return svg;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void resize(double ratioW, double ratioH){

        svg.setScaleX(Math.min(ratioW * scaleX, 1.5 * scaleX));
        svg.setScaleY(Math.min(ratioH * scaleY, 1.5 * scaleY));

    }

    public void makeRotateAnimation() {

        RotateTransition rotate = new RotateTransition(Duration.seconds(3), this.svg);
        rotate.setCycleCount(Animation.INDEFINITE);  //set the rotation period to infinity
        rotate.setInterpolator(Interpolator.LINEAR); //makes the rotation run linearly
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.play();

    }

    public void makeStrokeTransition() {

        StrokeTransition stroke = new StrokeTransition(Duration.millis(3000), this.svg, Color.BLACK, Color.YELLOW);
        stroke.setCycleCount(Animation.INDEFINITE);
        stroke.setAutoReverse(true);
        stroke.play();

    }

    public void fillTransition(){

        FillTransition fill = new FillTransition(Duration.millis(4000), this.svg, Color.BLACK, Color.YELLOW);
        fill.setCycleCount(2);
        fill.setAutoReverse(true);
        fill.play();

    }

}
