package bot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Displays the JavaFX interface for the chatbot.
 */
public class Main extends Application {
    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        Label greeting = new Label("Hello World!");
        Scene scene = new Scene(greeting, 400, 200);

        stage.setTitle("Bot");
        stage.setScene(scene);
        stage.show();
    }
}
