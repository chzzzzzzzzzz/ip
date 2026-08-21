import java.util.Scanner;
import java.util.ArrayList;

/**
 * A simple chatbot that stores and displays tasks until the user says goodbye.
 */
public class Bot {
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
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();
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

                if (command.equals("list")) {
                    ensureNoArguments(arguments, "list");
                    printTaskList(tasks);
                } else if (command.equals("mark")) {
                    int taskIndex = parseTaskIndex(arguments, tasks.size(), "mark");
                    tasks.get(taskIndex).mark();
                    System.out.println("    Nice! I've marked this task as done:");
                    System.out.println("        " + tasks.get(taskIndex));
                } else if (command.equals("unmark")) {
                    int taskIndex = parseTaskIndex(arguments, tasks.size(), "unmark");
                    tasks.get(taskIndex).unmark();
                    System.out.println("    OK, I've marked this task as not done yet:");
                    System.out.println("        " + tasks.get(taskIndex));
                } else if (command.equals("delete")) {
                    int taskIndex = parseTaskIndex(arguments, tasks.size(), "delete");
                    Task taskRemoved = tasks.remove(taskIndex);
                    printTaskDeleted(taskRemoved, tasks.size());
                } else if (command.equals("todo")) {
                    Task task = parseTodo(arguments);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                } else if (command.equals("deadline")) {
                    Task task = parseDeadline(arguments);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                } else if (command.equals("event")) {
                    Task task = parseEvent(arguments);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                } else if (command.equals("bye")) {
                    throw new BotException("Use bye without any extra words.");
                } else if (command.isEmpty()) {
                    throw new BotException("Please enter a command.");
                } else {
                    throw new BotException("I don't know what \"" + command + "\" means.");
                }
            } catch (BotException error) {
                System.out.println("    OOPS!!! " + error.getMessage());
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
     * @param tasks array containing the tasks
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
            throw new BotException("A deadline must include /by followed by its date or time.");
        }

        String description = arguments.substring(0, byPosition).trim();
        String by = arguments.substring(byPosition + 3).trim();
        if (description.isEmpty()) {
            throw new BotException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new BotException("The date or time of a deadline cannot be empty.");
        }
        return new Deadline(description, by);
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
        return new Event(description, from, to);
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
     * @param command command being checked
     * @throws BotException if extra text was supplied
     */
    private static void ensureNoArguments(String arguments, String command) throws BotException {
        if (!arguments.isEmpty()) {
            throw new BotException("The " + command + " command does not take extra information.");
        }
    }
}
