import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

/**
 * A simple chatbot that stores and displays tasks until the user says goodbye.
 */
public class Bot {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

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
        TaskList tasks;
        try {
            tasks = storage.loadTasks();
        } catch (IOException error) {
            tasks = new TaskList();
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
                ParsedCommand command = Parser.parse(input);
                String arguments = command.getArguments();

                switch (command.getType()) {
                    case LIST: {
                        Parser.ensureNoArguments(arguments, "list");
                        printTaskList(tasks);
                        break;
                    }
                    case MARK: {
                        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "mark");
                        Task task = tasks.mark(taskIndex);
                        storage.saveTasks(tasks);
                        System.out.println("    Nice! I've marked this task as done:");
                        System.out.println("        " + task);
                        break;
                    }
                    case UNMARK: {
                        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "unmark");
                        Task task = tasks.unmark(taskIndex);
                        storage.saveTasks(tasks);
                        System.out.println("    OK, I've marked this task as not done yet:");
                        System.out.println("        " + task);
                        break;
                    }
                    case DELETE: {
                        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "delete");
                        Task taskRemoved = tasks.delete(taskIndex);
                        storage.saveTasks(tasks);
                        printTaskDeleted(taskRemoved, tasks.size());
                        break;
                    }
                    case TODO: {
                        Task task = Parser.parseTodo(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        printTaskAdded(task, tasks.size());
                        break;
                    }
                    case DEADLINE: {
                        Task task = Parser.parseDeadline(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        printTaskAdded(task, tasks.size());
                        break;
                    }
                    case EVENT: {
                        Task task = Parser.parseEvent(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        printTaskAdded(task, tasks.size());
                        break;
                    }
                    case ON: {
                        LocalDate date = Parser.parseDateQuery(arguments);
                        printTasksOnDate(tasks, date);
                        break;
                    }
                    case BYE: {
                        throw new BotException("Use bye without any extra words.");
                    }
                    case UNKNOWN: {
                        if (command.getCommandWord().isEmpty()) {
                            throw new BotException("Please enter a command.");
                        } else {
                            throw new BotException(
                                    "I don't know what \"" + command.getCommandWord() + "\" means.");
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
    private static void printTaskList(TaskList tasks) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format("    %d.%s", i + 1, tasks.getTask(i)));
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
     * Displays deadlines and events that occur on a specified date.
     * Original task numbers are preserved so they can be used with other commands.
     *
     * @param tasks list containing all tasks
     * @param date date to search for
     */
    private static void printTasksOnDate(TaskList tasks, LocalDate date) {
        System.out.println("    Here are the deadlines and events on "
                + date.format(DATE_DISPLAY_FORMAT) + ":");
        boolean hasMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.getTask(i).occursOn(date)) {
                System.out.println(String.format("    %d.%s", i + 1, tasks.getTask(i)));
                hasMatch = true;
            }
        }
        if (!hasMatch) {
            System.out.println("    No deadlines or events found.");
        }
    }

}
