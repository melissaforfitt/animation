import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/** Group which we can draw our figure on */
public class Canvas extends Group {

    // Set up all array lists to be used for constraints and storing positions
    ArrayList<Point> movements = new ArrayList<Point>();
    ArrayList<Point> inner = new ArrayList<Point>();
    ArrayList<ArrayList<Point>> outer = new ArrayList<ArrayList<Point>>();
    ArrayList<Circle> nodes = new ArrayList<Circle>();
    ArrayList<Point> points = new ArrayList<Point>();
    ArrayList<Line> lines = new ArrayList<Line>();
    ArrayList<Image> images = new ArrayList<Image>();
    ArrayList<ImageView> viewers = new ArrayList<ImageView>();

    /* Create some lines to draw it with */
    Line leftArm = new Line();
    Line rightArm = new Line();
    Line neck = new Line();
    Line back = new Line();
    Line leftLeg = new Line();
    Line rightLeg = new Line();

    /* And a circle for the head */
    Circle headCircle = new Circle(25, Color.GREY);

    /* Add some nodes to drag the body around with */
    Circle headNode = new Circle(8, Color.RED);
    Circle leftHandNode = new Circle(8, Color.RED);
    Circle rightHandNode = new Circle(8, Color.RED);
    Circle shoulderNode = new Circle(8, Color.RED);
    Circle waistNode = new Circle(8, Color.RED);
    Circle leftFootNode = new Circle(8, Color.RED);
    Circle rightFootNode = new Circle(8, Color.RED);

    /* Set up some initial positions for the parts of the stick figure */
    Point shoulder = new Point(100, 300, "Shoulder", null, shoulderNode, back);
    Point leftHand = new Point(50, 300, "Left Hand", shoulder, leftHandNode, leftArm);
    Point rightHand = new Point(150, 300, "Right Hand", shoulder, rightHandNode, rightArm);
    Point head = new Point(100, 250, "Head", shoulder, headNode, neck);
    Point waist = new Point(100, 350, "Waist", null, waistNode, back);
    Point leftFoot = new Point(65, 400, "Left Foot", waist, leftFootNode, leftLeg);
    Point rightFoot = new Point(135, 400, "Right Foot", waist, rightFootNode, rightLeg);

    // Elements for creating animation
    ScrollBar scroller = new ScrollBar();
    Button addButton = new Button("Frame");
    Button undoButton = new Button("Undo");
    Button resetButton = new Button("Reset");
    Button musicOnButton = new Button("Music On");
    Button musicOffButton = new Button("Music Off");
    Button textButton = new Button("Text");
    Button removeTextButton = new Button("Remove Text");
    BorderPane border = new BorderPane();
    HBox creationPane = new HBox();
    GridPane toolbarPane = new GridPane();
    HBox imageBox = new HBox();
    ScrollPane imagePane = new ScrollPane();
    Text text;

    // Set up variables to be used in adding frame method
    ImageView viewer;
    Image img;

    // Set up variable for increasing number of screenshots
    File frameShot;
    int count = 0;

    // Set up variables to be used in undo method
    double oldX;
    double oldY;
    String type;
    Point temp;
    double length;

    // Variables to be used for playing music
    MediaPlayer player;
    boolean musicOn = false;

    // Set up variables for drop down box
    public ComboBox<String> colourComboBox = new ComboBox<String>();
    String selectedColour;
    public ComboBox<String> stickmanColour = new ComboBox<String>();
    String selectedStickmanColour;

    boolean moveMade = false;
    boolean added = false;

    public Canvas(int width, int height) {

        // Call relevant methods to set up the canvas
        windowSetup();
        arrayListSetup();
        pointsToShapes();
        initialPosition();
        toolbar();
        move();

    }

    private void windowSetup() {

        // Set up main window layout and style
        getChildren().add(border);
        getChildren().add(creationPane);
        getChildren().add(imagePane);
        border.setTop(imagePane);
        border.setCenter(creationPane);
        border.setBottom(toolbarPane);
        border.setPrefSize(800, 600);
        imagePane.setStyle("-fx-background-color: #404040");
        imagePane.setPrefHeight(100);
        toolbarPane.setStyle("-fx-background-color: #404040");
        toolbarPane.setPrefHeight(20);
        toolbarPane.setHgap(10);
        creationPane.setStyle("-fx-background-color: #D3D3D3");

        // Set up scrollbar so that it can be used to scroll through frames
        imagePane.setHbarPolicy(ScrollBarPolicy.ALWAYS); // Always show the scrollbar
        imagePane.setContent(imageBox);
        scroller.setLayoutX(800);
        scroller.setMin(0);
        scroller.setMax(8);
        scroller.setOrientation(Orientation.HORIZONTAL);
        scroller.setPrefWidth(800);
        scroller.setPrefHeight(20);

        scroller.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                imagePane.setLayoutX(-newValue.doubleValue());
            }
        });

        // Add stick man to screen
        border.getChildren().add(leftArm);
        border.getChildren().add(rightArm);
        border.getChildren().add(neck);
        border.getChildren().add(back);
        border.getChildren().add(leftLeg);
        border.getChildren().add(rightLeg);
        border.getChildren().add(headCircle);

        // Add nodes to screen
        border.getChildren().add(headNode);
        border.getChildren().add(leftHandNode);
        border.getChildren().add(rightHandNode);
        border.getChildren().add(shoulderNode);
        border.getChildren().add(waistNode);
        border.getChildren().add(leftFootNode);
        border.getChildren().add(rightFootNode);

        // Add buttons and features to toolbar
        toolbarPane.add(addButton, 0, 0);
        toolbarPane.add(undoButton, 1, 0);
        toolbarPane.add(resetButton, 3, 0);
        toolbarPane.add(musicOnButton, 4, 0);
        toolbarPane.add(musicOffButton, 5, 0);
        toolbarPane.add(colourComboBox, 6, 0);
        toolbarPane.add(stickmanColour, 7, 0);
        toolbarPane.add(textButton, 8, 0);
        toolbarPane.add(removeTextButton, 9, 0);

    }

    public void move() {

        moveMade = true;

        // Generic node click that works for each outer-body node
        for (Point point : points) {

            Circle currentNode = point.node;
            Line currentLine = point.line;

            currentNode.setOnMouseDragged(event -> {

                // Constrain lines (by rotation) so that they are the same length
                Point mousePoint = new Point(event.getX(), event.getY(), "Angle", point.connectsTo, currentNode,
                        currentLine);
                Point connectPoint = new Point((point.connectsTo).x, (point.connectsTo).y, "Angle", point.connectsTo,
                        currentNode, currentLine);

                length = Math.hypot(connectPoint.x - point.x, connectPoint.y - point.y);

                point.x = pivot(mousePoint, connectPoint, length).x;
                point.y = pivot(mousePoint, connectPoint, length).y;

                pointsToShapes();

                currentNode.setOnMouseReleased(event2 -> {

                    // Add new position of node to array list
                    Point temp = new Point(point.x, point.y, point.type, point.connectsTo, currentNode, currentLine);
                    addToArray(temp);
                });
            });

        }

        // When shoulder node is clicked, move whole body
        shoulderNode.setOnMouseDragged(event -> {

            double initialPositionX = shoulder.x;
            double initialPositionY = shoulder.y;

            double distanceX = event.getX() - initialPositionX;
            double distanceY = event.getY() - initialPositionY;

            for (Point point : points) {

                // Update each point to new location in respect to shoulders
                point.x = point.x + distanceX;
                point.y = point.y + distanceY;

                pointsToShapes();

            }

            // Add new position of node to array list
            shoulderNode.setOnMouseReleased(event2 -> {
                for (Point point : points) {
                    temp = new Point(point.x, point.y, point.type, point.connectsTo, point.node, point.line);
                    addToArray(temp);
                }
            });
        });

        // When waist node is clicked, move lower-half of stickman's body
        waistNode.setOnMouseDragged(event -> {

            // Constrain lines (by rotation) so that they are the same length
            Point mousePoint = new Point(event.getX(), event.getY(), "Angle", waist.connectsTo, waistNode, back);
            Point connectPoint = new Point(head.x, head.y, "Angle", head.connectsTo, headNode, back);

            length = Math.hypot(connectPoint.x - waist.x, connectPoint.y - waist.y);

            waist.x = pivot(mousePoint, connectPoint, length).x;
            waist.y = pivot(mousePoint, connectPoint, length).y;

            pointsToShapes();

            waistNode.setOnMouseReleased(event2 -> {
                // Add new position of node to array list
                temp = new Point(waist.x, waist.y, waist.type, waist.connectsTo, waist.node, waist.line);
                addToArray(temp);
            });
        });

    }

    // Method to take a screenshot of the stickman to be displayed in the window
    private void takeFrameShot() {

        WritableImage image = border.snapshot(new SnapshotParameters(), null);
        File frameShot = new File("screenshot" + count + ".png");

        count = count + 1;

        // Take screenshot of window with relevant name
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", frameShot);
        } catch (IOException e) {
        }

        img = new Image(frameShot.toURI().toString());
        viewer = new ImageView(img);
        viewer.setFitHeight(100);
        viewer.setFitWidth(100);
        viewers.add(viewer);

        // Add image to the top pane in the window
        imageBox.getChildren().add(viewer);

        for (ImageView view : viewers) {
            view.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DropShadow dropShadow = new DropShadow(10, Color.RED);
                view.setEffect(dropShadow);
                event.consume();
            });
        }

        // Add image to array list so it can be recalled elsewhere
        images.add(img);

    }

    // Method for stickman to go back to previous position in the array list
    public void undo() {

        if (movements.size() > 7) {

            // Remove latest movement from arraylist
            movements.remove(movements.size() - 1);
            oldX = movements.get(movements.size() - 1).x;
            oldY = movements.get(movements.size() - 1).y;
            type = movements.get(movements.size() - 1).type;

            // According to what type of node was previously changed, move back to position
            if (type == "Head") {
                head.x = oldX;
                head.y = oldY;
            } else if (type == "Shoulder") {
                shoulder.x = oldX;
                shoulder.y = oldY;
            } else if (type == "Left Hand") {
                leftHand.x = oldX;
                leftHand.y = oldY;
            } else if (type == "Right Hand") {
                rightHand.x = oldX;
                rightHand.y = oldY;
            } else if (type == "Waist") {
                waist.x = oldX;
                waist.y = oldY;
            } else if (type == "Left Foot") {
                leftFoot.x = oldX;
                leftFoot.y = oldY;
            } else if (type == "Right Foot") {
                rightFoot.x = oldX;
                rightFoot.y = oldY;
            }
        } else {
            // If user has pressed undo back to beginning of history, reset node positions
            resetPositions();
        }
        pointsToShapes();
    }

    // Method to add the original stickman position to array list (for undo)
    private void initialPosition() {

        movements.add(head);
        movements.add(shoulder);
        movements.add(leftHand);
        movements.add(rightHand);
        movements.add(waist);
        movements.add(leftFoot);
        movements.add(rightFoot);

    }

    // Reset all nodes back to their original positions
    public void resetPositions() {

        head.x = 100;
        head.y = 250;
        shoulder.x = 100;
        shoulder.y = 300;
        leftHand.x = 50;
        leftHand.y = 300;
        rightHand.x = 150;
        rightHand.y = 300;
        waist.x = 100;
        waist.y = 350;
        leftFoot.x = 65;
        leftFoot.y = 400;
        rightFoot.x = 135;
        rightFoot.y = 400;

    }

    public void addToArray(Point node) {

        movements.add(movements.size() - 1, node);

    }

    // Helper method to set up a line using two Points
    private void setupLine(Line line, Point start, Point end) {

        line.setStartX(start.x);
        line.setStartY(start.y);
        line.setEndX(end.x);
        line.setEndY(end.y);

    }

    // Set up our shapes to the current positions of our points
    private void pointsToShapes() {

        // Create lines between nodes
        setupLine(leftArm, leftHand, shoulder);
        setupLine(rightArm, rightHand, shoulder);
        setupLine(neck, shoulder, head);
        setupLine(back, shoulder, waist);
        setupLine(leftLeg, waist, leftFoot);
        setupLine(rightLeg, waist, rightFoot);

        // Set points at correct positions on stickman
        headCircle.setCenterX(head.x);
        headCircle.setCenterY(head.y);

        headNode.setCenterX(head.x);
        headNode.setCenterY(head.y);

        leftHandNode.setCenterX(leftHand.x);
        leftHandNode.setCenterY(leftHand.y);

        rightHandNode.setCenterX(rightHand.x);
        rightHandNode.setCenterY(rightHand.y);

        shoulderNode.setCenterX(shoulder.x);
        shoulderNode.setCenterY(shoulder.y);

        waistNode.setCenterX(waist.x);
        waistNode.setCenterY(waist.y);

        leftFootNode.setCenterX(leftFoot.x);
        leftFootNode.setCenterY(leftFoot.y);

        rightFootNode.setCenterX(rightFoot.x);
        rightFootNode.setCenterY(rightFoot.y);

    }

    public void playMusic() {

        // Find background music file and play it
        final Media music = new Media(Paths.get("background-music.wav").toUri().toString());
        player = new MediaPlayer(music);
        player.play();

        player.setVolume(10.0);

        musicOn = true;

    }

    public void stopMusic() {

        // Stop playing the music
        player.stop();

        musicOn = false;

    }

    public void arrayListSetup() {

        // Add all outer points to array list
        points.add(head);
        points.add(shoulder);
        points.add(leftHand);
        points.add(rightHand);
        points.add(waist);
        points.add(leftFoot);
        points.add(rightFoot);

        // Add all stickman's nodes to array list
        nodes.add(headCircle);
        nodes.add(headNode);
        nodes.add(shoulderNode);
        nodes.add(leftHandNode);
        nodes.add(rightHandNode);
        nodes.add(waistNode);
        nodes.add(leftFootNode);
        nodes.add(rightFootNode);

        // Add all lines to array list
        lines.add(neck);
        lines.add(leftArm);
        lines.add(rightArm);
        lines.add(back);
        lines.add(leftLeg);
        lines.add(rightLeg);

    }

    public void positionArray() {

        // Create an arraylist of arraylists to store all points at that current
        // position

        for (Point point : points) {

            inner.add(point);
        }

        outer.add(inner);
        inner = new ArrayList<Point>(inner);
        outer.add(movements);

    }

    public Point pivot(Point mousePoint, Point previousPoint, double length) {

        Point newPoint = new Point(0, 0, null, null, null, null);

        double angle = Math.atan2(mousePoint.y - previousPoint.y, mousePoint.x - previousPoint.x);

        double x = previousPoint.x + (length * Math.cos(angle));
        double y = previousPoint.y + (length * Math.sin(angle));

        newPoint.x = x;
        newPoint.y = y;

        return newPoint;

    }

    public void playAnimation(ArrayList<Point> inner, ArrayList<ArrayList<Point>> outer) {

        Play play = new Play();
        play.start(inner, outer);

    }

    public void toolbar() {

        // Set up drop down list so that user can change colour of animation background
        colourComboBox.setPromptText("Background");
        colourComboBox.getItems().addAll("White", "Pink", "Blue", "Green");

        colourComboBox.valueProperty().addListener((options, oldColour, selectedColour) -> {

            if (selectedColour == "White") {
                creationPane.setStyle("-fx-background-color: #ffffff");
            } else if (selectedColour == "Pink") {
                creationPane.setStyle("-fx-background-color: #eb7f95");
            } else if (selectedColour == "Blue") {
                creationPane.setStyle("-fx-background-color: #5dc5ef");
            } else if (selectedColour == "Green") {
                creationPane.setStyle("-fx-background-color: #34da34");
            }

        });

        // Set up drop down list for user to select colour of stickman
        stickmanColour.setPromptText("Stickman");
        stickmanColour.getItems().addAll("White", "Pink", "Blue", "Green");

        stickmanColour.valueProperty().addListener((options, oldColour, selectedColour) -> {

            for (Line line : lines) {
                if (selectedColour == "White") {
                    line.setStroke(Color.WHITE);
                } else if (selectedColour == "Pink") {
                    line.setStroke(Color.PINK);
                } else if (selectedColour == "Blue") {
                    line.setStroke(Color.BLUE);
                } else if (selectedColour == "Green") {
                    line.setStroke(Color.GREEN);
                }
            }

        });

        // When relevant buttons are clicked, call methods to implement button purpose
        addButton.setOnMouseClicked(event -> {
            takeFrameShot();
            positionArray(); // Add current position of all nodes to an array
        });

        undoButton.setOnMouseClicked(event -> {
            undo();
            pointsToShapes();
        });

        resetButton.setOnMouseClicked(event -> {
            resetPositions();
            pointsToShapes();
        });

        // Only let user turn on music if it is currently off
        musicOnButton.setOnMouseClicked(event -> {
            if (musicOn == false) {
                playMusic();
            }
        });

        // Only let user turn off music if it is currently playing
        musicOffButton.setOnMouseClicked(event -> {
            if (musicOn == true) {
                stopMusic();
            }
        });

        // Let user add text to the screen
        textButton.setOnMouseClicked(event -> {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Input Text");
            dialog.setContentText("Enter the text you want to add:");

            Optional<String> inputText = dialog.showAndWait();

            String stringText = inputText.toString();

            text = new Text(stringText);
            text.setFont(Font.font("verdana", FontWeight.BOLD, 30));

            creationPane.getChildren().add(text);

            // Let user drag text around the screen
            text.setOnMouseDragged(event2 -> {

                text.setX(event2.getX());
                text.setY(event2.getY());

            });

        });

        // Allow user to remove text from the screen
        removeTextButton.setOnMouseClicked(event -> {

            creationPane.getChildren().remove(text);

        });

    }

}
