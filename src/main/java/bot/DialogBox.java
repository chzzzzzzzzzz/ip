package bot;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message together with its sender's avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing a message and an avatar.
     *
     * @param message message to display.
     * @param image avatar to display beside the message.
     */
    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load the dialog box layout.", error);
        }

        dialog.setText(message);
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog for a message sent by the user.
     *
     * @param message message sent by the user.
     * @param image avatar representing the user.
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a left-aligned dialog for a response sent by Bot.
     *
     * @param message response sent by Bot.
     * @param image avatar representing Bot.
     * @return Bot dialog box
     */
    public static DialogBox getBotDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Places the avatar on the left and the message on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        dialog.getStyleClass().add("reply-label");
    }
}
