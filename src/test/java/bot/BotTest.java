package bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bot.storage.Storage;

/**
 * Tests command execution shared by the console and JavaFX interfaces.
 */
class BotTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addAndListTasks_returnsFormattedTaskList() {
        Bot bot = createBot();

        assertEquals("""
                Got it. I've added this task:
                    [T][ ] borrow book
                Now you have 1 tasks in the list.""", bot.getResponse("todo borrow book"));
        assertEquals("""
                Got it. I've added this task:
                    [D][ ] return book (by: Dec 02 2019)
                Now you have 2 tasks in the list.""",
                bot.getResponse("deadline return book /by 2019-12-02"));
        assertEquals("""
                Got it. I've added this task:
                    [E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
                Now you have 3 tasks in the list.""",
                bot.getResponse("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertEquals("""
                Here are the tasks in your list:
                1.[T][ ] borrow book
                2.[D][ ] return book (by: Dec 02 2019)
                3.[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)""",
                bot.getResponse("list"));
    }

    @Test
    void getResponse_markUnmarkAndDelete_updatesTaskList() {
        Bot bot = createBot();
        bot.getResponse("todo read book");
        bot.getResponse("deadline return book /by 2019-12-02");

        assertEquals("""
                Nice! I've marked this task as done:
                    [T][X] read book""", bot.getResponse("mark 1"));
        assertEquals("""
                OK, I've marked this task as not done yet:
                    [T][ ] read book""", bot.getResponse("unmark 1"));
        assertEquals("""
                Noted. I've removed this task:
                    [T][ ] read book
                Now you have 1 tasks in the list.""", bot.getResponse("delete 1"));
        assertEquals("""
                Here are the tasks in your list:
                1.[D][ ] return book (by: Dec 02 2019)""", bot.getResponse("list"));
    }

    @Test
    void getResponse_findAndDateQuery_returnsMatchingTasks() {
        Bot bot = createBot();
        bot.getResponse("todo read book");
        bot.getResponse("deadline return book /by 2019-12-02");
        bot.getResponse("event meeting /from 2019-12-01 1400 /to 2019-12-03 1600");

        assertEquals("""
                Here are the matching tasks in your list:
                1.[T][ ] read book
                2.[D][ ] return book (by: Dec 02 2019)""", bot.getResponse("find book"));
        assertEquals("""
                Here are the deadlines and events on Dec 02 2019:
                2.[D][ ] return book (by: Dec 02 2019)
                3.[E][ ] meeting (from: Dec 01 2019, 2:00PM to: Dec 03 2019, 4:00PM)""",
                bot.getResponse("on 2019-12-02"));
    }

    @Test
    void getResponse_invalidCommands_returnsErrorsAndContinues() {
        Bot bot = createBot();

        assertEquals("OOPS!!! Please enter a command.", bot.getResponse(""));
        assertEquals("OOPS!!! I don't know what \"blah\" means.", bot.getResponse("blah"));
        assertEquals("OOPS!!! The description of a todo cannot be empty.", bot.getResponse("todo"));
        assertEquals("OOPS!!! The task list is empty.", bot.getResponse("mark 1"));
        assertEquals("Here are the tasks in your list:", bot.getResponse("list"));
    }

    @Test
    void getResponse_taskChange_savesAndReloadsTask() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("duke.txt");
        Storage storage = new Storage(dataFile.toFile());
        Bot bot = new Bot(storage);

        bot.getResponse("todo read book");
        bot.getResponse("mark 1");

        assertEquals("T | 1 | read book" + System.lineSeparator(), Files.readString(dataFile));
        Bot reloadedBot = new Bot(storage);
        assertEquals("""
                Here are the tasks in your list:
                1.[T][X] read book""", reloadedBot.getResponse("list"));
        assertEquals("", reloadedBot.getStartupMessage());
    }

    @Test
    void constructor_malformedData_exposesLoadingErrorAndUsesEmptyList() throws IOException {
        Path dataFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(dataFile, "X | 0 | invalid task");

        Bot bot = new Bot(new Storage(dataFile.toFile()));

        assertEquals("""
                OOPS!!! I couldn't load your data file: invalid data on line 1 (unknown task type 'X'). \
                I started with an empty list.""", bot.getStartupMessage());
        assertEquals("Here are the tasks in your list:", bot.getResponse("list"));
    }

    @Test
    void getResponse_byeCommand_setsExitFlag() {
        Bot bot = createBot();

        assertFalse(bot.shouldExit());
        assertEquals("Bye. Hope to see you again soon!", bot.getResponse("bye"));
        assertTrue(bot.shouldExit());
    }

    @Test
    void getResponse_byeWithArguments_returnsErrorWithoutSettingExitFlag() {
        Bot bot = createBot();

        assertEquals("OOPS!!! Use bye without any extra words.", bot.getResponse("bye later"));
        assertFalse(bot.shouldExit());
    }

    /**
     * Creates a bot using an isolated data file for each test.
     *
     * @return bot with temporary storage
     */
    private Bot createBot() {
        Path dataFile = temporaryDirectory.resolve("data").resolve("duke.txt");
        return new Bot(new Storage(dataFile.toFile()));
    }
}
