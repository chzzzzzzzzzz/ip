package bot.parser;

import bot.exception.BotException;
import bot.task.Deadline;
import bot.task.Event;
import bot.task.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Converts user input into commands and validates command arguments.
 */
public final class Parser {
    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Splits one line of input into its command word and arguments.
     *
     * @param input line entered by the user
     * @return parsed command information
     */
    public static ParsedCommand parse(String input) {
        String trimmedInput = input.trim();
        String[] parts = trimmedInput.split("\\s+", 2);
        String commandWord = parts[0];
        String arguments = parts.length == 2 ? parts[1].trim() : "";
        return new ParsedCommand(CommandType.from(commandWord), commandWord, arguments);
    }

    /**
     * Creates a todo after checking that it has a description.
     *
     * @param arguments text following the todo command
     * @return parsed todo
     * @throws BotException if the description is empty
     */
    public static Todo parseTodo(String arguments) throws BotException {
        if (arguments.isEmpty()) {
            throw new BotException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    /**
     * Creates a deadline after checking its description and date.
     *
     * @param arguments text following the deadline command
     * @return parsed deadline
     * @throws BotException if required deadline information is missing or invalid
     */
    public static Deadline parseDeadline(String arguments) throws BotException {
        if (arguments.isEmpty()) {
            throw new BotException("The description of a deadline cannot be empty.");
        }

        int byPosition = arguments.indexOf("/by");
        if (byPosition < 0) {
            throw new BotException("A deadline must include /by followed by its date.");
        }

        String description = arguments.substring(0, byPosition).trim();
        String by = arguments.substring(byPosition + 3).trim();
        if (description.isEmpty()) {
            throw new BotException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new BotException("The date of a deadline cannot be empty.");
        }
        try {
            LocalDate byDate = LocalDate.parse(by, DEADLINE_INPUT_FORMAT);
            return new Deadline(description, byDate);
        } catch (DateTimeParseException error) {
            throw new BotException(
                    "Use yyyy-MM-dd for deadline dates, e.g. 2019-12-02.");
        }
    }

    /**
     * Creates an event after checking its description, start, and end.
     *
     * @param arguments text following the event command
     * @return parsed event
     * @throws BotException if required event information is missing or invalid
     */
    public static Event parseEvent(String arguments) throws BotException {
        if (arguments.isEmpty()) {
            throw new BotException("The description of an event cannot be empty.");
        }

        int fromPosition = arguments.indexOf("/from");
        if (fromPosition < 0) {
            throw new BotException("An event must include /from followed by its start time.");
        }

        int toPosition = arguments.indexOf("/to", fromPosition + 5);
        if (toPosition < 0) {
            throw new BotException("An event must include /to followed by its end time.");
        }

        String description = arguments.substring(0, fromPosition).trim();
        String from = arguments.substring(fromPosition + 5, toPosition).trim();
        String to = arguments.substring(toPosition + 3).trim();
        if (description.isEmpty()) {
            throw new BotException("The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new BotException("The start time of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new BotException("The end time of an event cannot be empty.");
        }
        try {
            LocalDateTime fromDateTime = LocalDateTime.parse(from, EVENT_INPUT_FORMAT);
            LocalDateTime toDateTime = LocalDateTime.parse(to, EVENT_INPUT_FORMAT);
            if (!toDateTime.isAfter(fromDateTime)) {
                throw new BotException(
                        "The event end date and time must be after its start date and time.");
            }
            return new Event(description, fromDateTime, toDateTime);
        } catch (DateTimeParseException error) {
            throw new BotException(
                    "Use yyyy-MM-dd HHmm for event dates and times, e.g. 2019-12-02 1400.");
        }
    }

    /**
     * Parses the date supplied to the on command.
     *
     * @param arguments date text following the on command
     * @return parsed search date
     * @throws BotException if the date is missing or invalid
     */
    public static LocalDate parseDateQuery(String arguments) throws BotException {
        if (arguments.isEmpty()) {
            throw new BotException("Tell me which date to search using yyyy-MM-dd.");
        }
        try {
            return LocalDate.parse(arguments, DEADLINE_INPUT_FORMAT);
        } catch (DateTimeParseException error) {
            throw new BotException(
                    "Use yyyy-MM-dd when searching by date, e.g. 2019-12-02.");
        }
    }

    /**
     * Converts a one-based task number into a valid zero-based index.
     *
     * @param arguments task number entered by the user
     * @param taskCount number of tasks currently stored
     * @param command command being processed
     * @return zero-based index of the selected task
     * @throws BotException if the task number is missing or invalid
     */
    public static int parseTaskIndex(String arguments, int taskCount, String command) throws BotException {
        if (arguments.isEmpty()) {
            throw new BotException("Tell me which task number to " + command + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException error) {
            throw new BotException("The task number must be a whole number.");
        }

        if (taskCount == 0) {
            throw new BotException("The task list is empty.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new BotException("Task number " + taskNumber + " does not exist. Choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Checks that a command which takes no arguments was entered correctly.
     *
     * @param arguments text following the command
     * @param command command being checked
     * @throws BotException if extra text was supplied
     */
    public static void ensureNoArguments(String arguments, String command) throws BotException {
        if (!arguments.isEmpty()) {
            throw new BotException("The " + command + " command does not take extra information.");
        }
    }
}
