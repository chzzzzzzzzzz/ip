package bot.ui;

import java.time.LocalDate;
import java.util.Scanner;

import bot.task.Task;
import bot.task.TaskList;

/**
 * Handles console input and output for the chatbot.
 */
public class Ui {
    private static final String LINE = "_".repeat(60);

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
        System.out.println(ResponseFormatter.formatGoodbye());
        System.out.println(LINE);
    }

    /**
     * Displays a response with the indentation used by the console interface.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        response.lines().forEach(line -> System.out.println("    " + line));
    }

    /**
     * Displays an error caused by invalid user input.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        showResponse(ResponseFormatter.formatError(message));
    }

    /**
     * Displays an error encountered while loading saved tasks.
     *
     * @param message explanation from the storage layer.
     */
    public void showLoadingError(String message) {
        showResponse(ResponseFormatter.formatLoadingError(message));
    }

    /**
     * Displays an error encountered while saving tasks.
     */
    public void showSavingError() {
        showResponse(ResponseFormatter.formatSavingError());
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks task list to display.
     */
    public void showTaskList(TaskList tasks) {
        showResponse(ResponseFormatter.formatTaskList(tasks));
    }

    /**
     * Displays tasks that match a find command in their original order.
     *
     * @param matchingTasks matching tasks to display.
     */
    public void showMatchingTasks(TaskList matchingTasks) {
        showResponse(ResponseFormatter.formatMatchingTasks(matchingTasks));
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showResponse(ResponseFormatter.formatTaskAdded(task, taskCount));
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        showResponse(ResponseFormatter.formatTaskMarked(task));
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        showResponse(ResponseFormatter.formatTaskUnmarked(task));
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showResponse(ResponseFormatter.formatTaskDeleted(task, taskCount));
    }

    /**
     * Displays deadlines and events that occur on a specified date.
     * Original task numbers are preserved for use with other commands.
     *
     * @param tasks task list to search and display.
     * @param date date to display tasks for.
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        showResponse(ResponseFormatter.formatTasksOnDate(tasks, date));
    }
}
