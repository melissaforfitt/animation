
import java.util.ArrayList;

import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class Play {

    public Play() {

    }

    public void start(ArrayList<Point> inner, ArrayList<ArrayList<Point>> outer) {

        // Take all of the screenshots and put them together to create an animation
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);

        // Gets each position of point at each frame and translates accordingly
        for (ArrayList<Point> in : outer) {
            for (Point x : in) {

                // Using translation to move lines of stickman
                TranslateTransition translateTransition = new TranslateTransition();
                translateTransition.setToX(x.x);
                translateTransition.setToX(x.y);
                translateTransition.setDuration(Duration.millis(3000));

            }
        }

        timeline.play();

    }

}
