package bot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests responses generated for the JavaFX interface.
 */
class BotTest {
    @Test
    void getResponse_typicalMessage_echoesMessage() {
        Bot bot = new Bot();

        assertEquals("Bot heard: hello", bot.getResponse("hello"));
    }

    @Test
    void getResponse_emptyMessage_echoesEmptyMessage() {
        Bot bot = new Bot();

        assertEquals("Bot heard: ", bot.getResponse(""));
    }
}
