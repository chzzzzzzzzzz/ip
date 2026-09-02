package bot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import bot.exception.BotException;
import bot.parser.ParsedCommand;
import bot.parser.Parser;
import bot.storage.Storage;
import bot.task.Task;
import bot.task.TaskList;
import bot.ui.ResponseFormatter;
import bot.ui.Ui;

/**
 * A chatbot that stores tasks and executes commands from console or JavaFX interfaces.
 */
public class Bot {
    private static final String DEFAULT_DATA_FILE_PATH = "./data/duke.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final String startupMessage;
    private boolean shouldExit;

    /**
     * Creates a chatbot backed by the default data file.
     */
    public Bot() {
        this(new Storage(new File(DEFAULT_DATA_FILE_PATH)));
    }

    /**
     * Creates a chatbot backed by the specified storage.
     *
     * @param storage storage used to load and save tasks.
     */
    Bot(Storage storage) {
        this.storage = storage;

        TaskList loadedTasks;
        String loadingMessage = "";
        try {
            loadedTasks = storage.loadTasks();
        } catch (IOException error) {
            loadedTasks = new TaskList();
            loadingMessage = ResponseFormatter.formatLoadingError(error.getMessage());
        }
        this.tasks = loadedTasks;
        this.startupMessage = loadingMessage;
    }

    /**
     * Parses and executes one command, returning a response suitable for display.
     *
     * @param input command entered by the user.
     * @return response describing the result of the command
     */
    public String getResponse(String input) {
        try {
            return executeCommand(Parser.parse(input));
        } catch (BotException error) {
            return ResponseFormatter.formatError(error.getMessage());
        } catch (IOException error) {
            return ResponseFormatter.formatSavingError();
        }
    }

    /**
     * Returns any error encountered while loading tasks during startup.
     *
     * @return loading error response, or an empty string when loading succeeded
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Checks whether the user has entered the bye command.
     *
     * @return {@code true} when the application should exit
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Starts the console interface and handles commands entered by the user.
     *
     * @param args command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Bot bot = new Bot();
        if (!bot.getStartupMessage().isEmpty()) {
            ui.showResponse(bot.getStartupMessage());
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }

            ui.showCommandSeparator();
            ui.showResponse(bot.getResponse(input));
            ui.showCommandSeparator();
        }
        ui.showGoodbye();
    }

    /**
     * Executes a parsed command and applies any resulting task-list changes.
     *
     * @param command parsed command to execute.
     * @return response describing the command result
     * @throws BotException if the command or its arguments are invalid
     * @throws IOException if a task-list change cannot be saved
     */
    private String executeCommand(ParsedCommand command) throws BotException, IOException {
        String arguments = command.getArguments();

        switch (command.getType()) {
            case LIST: {
                Parser.ensureNoArguments(arguments, "list");
                return ResponseFormatter.formatTaskList(tasks);
            }
            case MARK: {
                int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "mark");
                Task task = tasks.mark(taskIndex);
                storage.saveTasks(tasks);
                return ResponseFormatter.formatTaskMarked(task);
            }
            case UNMARK: {
                int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "unmark");
                Task task = tasks.unmark(taskIndex);
                storage.saveTasks(tasks);
                return ResponseFormatter.formatTaskUnmarked(task);
            }
            case DELETE: {
                int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "delete");
                Task taskRemoved = tasks.delete(taskIndex);
                storage.saveTasks(tasks);
                return ResponseFormatter.formatTaskDeleted(taskRemoved, tasks.size());
            }
            case TODO: {
                Task task = Parser.parseTodo(arguments);
                tasks.add(task);
                storage.saveTasks(tasks);
                return ResponseFormatter.formatTaskAdded(task, tasks.size());
            }
            case DEADLINE: {
                Task task = Parser.parseDeadline(arguments);
                tasks.add(task);
                storage.saveTasks(tasks);
                return ResponseFormatter.formatTaskAdded(task, tasks.size());
            }
            case EVENT: {
                Task task = Parser.parseEvent(arguments);
                tasks.add(task);
                storage.saveTasks(tasks);
                return ResponseFormatter.formatTaskAdded(task, tasks.size());
            }
            case FIND: {
                String keyword = Parser.parseFindKeyword(arguments);
                return ResponseFormatter.formatMatchingTasks(tasks.find(keyword));
            }
            case ON: {
                LocalDate date = Parser.parseDateQuery(arguments);
                return ResponseFormatter.formatTasksOnDate(tasks, date);
            }
            case BYE: {
                if (!arguments.isEmpty()) {
                    throw new BotException("Use bye without any extra words.");
                }
                shouldExit = true;
                return ResponseFormatter.formatGoodbye();
            }
            case UNKNOWN: {
                if (command.getCommandWord().isEmpty()) {
                    throw new BotException("Please enter a command.");
                }
                throw new BotException(
                        "I don't know what \"" + command.getCommandWord() + "\" means.");
            }
            default:
                throw new BotException("I don't know what that command means.");
        }
    }
}
