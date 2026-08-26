import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple chatbot that stores and displays tasks until the user says goodbye.
 */
public class Bot {
    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Starts the chatbot and handles commands entered by the user.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {

        String line = "_".repeat(60);
        String banner = """
                         ____        _
                        | __ )  ___ | |_
                        |  _ \\ / _ \\| __|
                        | |_) | (_) | |_
                        |____/ \\___/ \\__|
                        """;
        System.out.print(banner);
        System.out.println("Yo! I'm Bot.");
        System.out.println("What can I do for you?");
        System.out.println(line);
        Storage storage = new Storage(new File("./data/duke.txt"));
        ArrayList<Task> tasks;
        try {
            tasks = storage.loadTasks();
        } catch (IOException error) {
            tasks = new ArrayList<>();
            System.out.println("    OOPS!!! I couldn't load your data file: " + error.getMessage()
                    + ". I started with an empty list.");
        }
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("bye")) {
                break;
            }

            System.out.println("    " + line);
            try {
                String[] parts = input.split("\\s+", 2);
                String command = parts[0];
                String arguments = parts.length == 2 ? parts[1].trim() : "";
                CommandType commandType = CommandType.from(command);

                switch (commandType) {
                    case LIST: {
                        ensureNoArguments(arguments);
                        printTaskList(tasks);
                        break;
                    }
                    case MARK: {
                        int taskIndex = parseTaskIndex(arguments, tasks.size(), "mark");
                        tasks.get(taskIndex).mark();
                        storage.saveTasks(tasks);
                        System.out.println("    Nice! I've marked this task as done:");
                        System.out.println("        " + tasks.get(taskIndex));
                        break;
                    }
                    case UNMARK: {
                        int taskIndex = parseTaskIndex(arguments, tasks.size(), "unmark");
                        tasks.get(taskIndex).unmark();
                        storage.saveTasks(tasks);
                        System.out.println("    OK, I've marked this task as not done yet:");
                        System.out.println("        " + tasks.get(taskIndex));
                        break;
                    }
                    case DELETE: {
                        int taskIndex = parseTaskIndex(arguments, tasks.size(), "delete");
                        Task taskRemoved = tasks.remove(taskIndex);
                        storage.saveTasks(tasks);
                        printTaskDeleted(taskRemoved, tasks.size());
                        break;
                    }
                    case TODO: {
                        Task task = parseTodo(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        printTaskAdded(task, tasks.size());
                        break;
                    }
                    case DEADLINE: {
                        Task task = parseDeadline(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        printTaskAdded(task, tasks.size());
                        break;
                    }
                    case EVENT: {
                        Task task = parseEvent(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        printTaskAdded(task, tasks.size());
                        break;
                    }
                    case BYE: {
                        throw new BotException("Use bye without any extra words.");
                    }
                    case UNKNOWN: {
                        if (command.isEmpty()) {
                            throw new BotException("Please enter a command.");
                        } else {
                            throw new BotException(
                                    "I don't know what \"" + command + "\" means.");
                        }
                    }
                }
            } catch (BotException error) {
                System.out.println("    OOPS!!! " + error.getMessage());
            } catch (IOException error) {
                System.out.println("    OOPS!!! I couldn't save your tasks to the data file.");
            }
            System.out.println("    " + line);
        }
        System.out.println(line);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks list containing the tasks
     */
    private static void printTaskList(ArrayList<Task> tasks) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format("    %d.%s", i + 1, tasks.get(i)));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount total number of tasks after the addition
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("    Got it. I've added this task:");
        System.out.println("        " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task task that was deleted
     * @param taskCount total number of tasks after deletion
     */

    private static void printTaskDeleted(Task task, int taskCount) {
        System.out.println("    Noted. I've removed this task:");
        System.out.println("        " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Creates a todo after checking that it has a description.
     *
     * @param arguments text following the todo command
     * @return the parsed todo
     * @throws BotException if the description is empty
     */
    private static Todo parseTodo(String arguments) throws BotException {
        if (arguments.isEmpty()) {
            throw new BotException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    /**
     * Creates a deadline after checking its description and /by value.
     *
     * @param arguments text following the deadline command
     * @return the parsed deadline
     * @throws BotException if required deadline information is missing
     */
    private static Deadline parseDeadline(String arguments) throws BotException {
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
     * Creates an event after checking its description, /from value, and /to value.
     *
     * @param arguments text following the event command
     * @return the parsed event
     * @throws BotException if required event information is missing
     */
    private static Event parseEvent(String arguments) throws BotException {
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
     * Converts a one-based task number into a valid array index.
     *
     * @param arguments task number entered after mark or unmark
     * @param taskCount number of tasks currently stored
     * @param command command being processed
     * @return zero-based index of the selected task
     * @throws BotException if the task number is missing or invalid
     */
    private static int parseTaskIndex(String arguments, int taskCount, String command) throws BotException {
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
     * @throws BotException if extra text was supplied
     */
    private static void ensureNoArguments(String arguments) throws BotException {
        if (!arguments.isEmpty()) {
            throw new BotException("The " + "list" + " command does not take extra information.");
        }
    }
}
