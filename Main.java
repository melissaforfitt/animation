import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    Canvas canvas = new Canvas(800, 600);

    public static void main(String[] args) {

        // Run the animation program
        launch(args);
    }

    public void start(Stage stage) {

        stage.setTitle("Animation");
        stage.setScene(new Scene(canvas, 800, 600));
        stage.show();
    }
}
