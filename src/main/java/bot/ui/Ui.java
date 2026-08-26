package bot.ui;

import bot.task.Task;
import bot.task.TaskList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

/**
 * Handles console input and output for the chatbot.
 */
public class Ui {
    private static final String LINE = "_".repeat(60);
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void showWelcome() {
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
        System.out.println(LINE);
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return {@code true} if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return next command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the indented separator surrounding a command response.
     */
    public void showCommandSeparator() {
        System.out.println("    " + LINE);
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Displays an error caused by invalid user input.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        System.out.println("    OOPS!!! " + message);
    }

    /**
     * Displays an error encountered while loading saved tasks.
     *
     * @param message explanation from the storage layer
     */
    public void showLoadingError(String message) {
        System.out.println("    OOPS!!! I couldn't load your data file: " + message
                + ". I started with an empty list.");
    }

    /**
     * Displays an error encountered while saving tasks.
     */
    public void showSavingError() {
        System.out.println("    OOPS!!! I couldn't save your tasks to the data file.");
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format("    %d.%s", i + 1, tasks.getTask(i)));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("    Got it. I've added this task:");
        System.out.println("        " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task task that was marked
     */
    public void showTaskMarked(Task task) {
        System.out.println("    Nice! I've marked this task as done:");
        System.out.println("        " + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("    OK, I've marked this task as not done yet:");
        System.out.println("        " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks after deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("    Noted. I've removed this task:");
        System.out.println("        " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays deadlines and events that occur on a specified date.
     * Original task numbers are preserved for use with other commands.
     *
     * @param tasks task list to search and display
     * @param date date to display tasks for
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
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
