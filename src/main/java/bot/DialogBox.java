package bot;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message together with its sender's avatar.
 */
public class DialogBox extends HBox {
    private static final double DISPLAY_IMAGE_SIZE = 100.0;

    private final Label text;
    private final ImageView displayPicture;

    /**
     * Creates a dialog box containing a message and an avatar.
     *
     * @param message message to display.
     * @param image avatar to display beside the message.
     */
    public DialogBox(String message, Image image) {
        text = new Label(message);
        displayPicture = new ImageView(image);

        text.setWrapText(true);
        displayPicture.setFitWidth(DISPLAY_IMAGE_SIZE);
        displayPicture.setFitHeight(DISPLAY_IMAGE_SIZE);
        setAlignment(Pos.TOP_RIGHT);

        getChildren().addAll(text, displayPicture);
    }
}
