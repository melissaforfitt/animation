import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PointTest {

    static Canvas canvas = new Canvas(800, 600);
    static ArrayList<Point> points;
    static Point leftHand;

    @BeforeAll
    static void setup() {

        points = new ArrayList<Point>();
        leftHand = new Point(50, 300, "Left Hand", null, null, null);

    }

    @Test
    void testArrayAdd() {

        canvas.arrayListSetup();
        canvas.addToArray(leftHand);

        assertEquals(8, points.size());

    }

    @Test
    void testUndo() {

        canvas.arrayListSetup();
        canvas.addToArray(leftHand);
        canvas.undo();

        assertEquals(7, points.size());

    }

    @Test
    void testReset() {

        canvas.arrayListSetup();
        leftHand.x = 380;
        canvas.resetPositions();

        assertEquals(50, leftHand.x);

    }

    @Test
    void testMusic() {

        boolean musicOn = false;

        canvas.playMusic();

        assertEquals(true, musicOn);

    }

}
