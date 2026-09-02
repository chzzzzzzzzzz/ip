package bot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the chatbot's main JavaFX window.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.jpg"));
    private final Image botImage = new Image(getClass().getResourceAsStream("/images/DaDuke.jpg"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bot bot;

    /**
     * Creates the controller used by the FXML loader.
     */
    public MainWindow() {
    }

    /**
     * Connects the scroll position to the dialog container's height.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot used to generate responses.
     *
     * @param bot chatbot instance used by this window.
     */
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    /**
     * Displays the user's message and Bot's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String botText = bot.getResponse(userText);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getBotDialog(botText, botImage));
        userInput.clear();
    }
}
