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
import bot.ui.Ui;

/**
 * A simple chatbot that stores and displays tasks until the user says goodbye.
 */
public class Bot {
    /**
     * Creates a chatbot application entry point.
     */
    public Bot() {
    }

    /**
     * Starts the chatbot and handles commands entered by the user.
     *
     * @param args command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage = new Storage(new File("./data/duke.txt"));
        TaskList tasks;
        try {
            tasks = storage.loadTasks();
        } catch (IOException error) {
            tasks = new TaskList();
            ui.showLoadingError(error.getMessage());
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.equals("bye")) {
                break;
            }

            ui.showCommandSeparator();
            try {
                ParsedCommand command = Parser.parse(input);
                String arguments = command.getArguments();

                switch (command.getType()) {
                    case LIST: {
                        Parser.ensureNoArguments(arguments, "list");
                        ui.showTaskList(tasks);
                        break;
                    }
                    case MARK: {
                        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "mark");
                        Task task = tasks.mark(taskIndex);
                        storage.saveTasks(tasks);
                        ui.showTaskMarked(task);
                        break;
                    }
                    case UNMARK: {
                        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "unmark");
                        Task task = tasks.unmark(taskIndex);
                        storage.saveTasks(tasks);
                        ui.showTaskUnmarked(task);
                        break;
                    }
                    case DELETE: {
                        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "delete");
                        Task taskRemoved = tasks.delete(taskIndex);
                        storage.saveTasks(tasks);
                        ui.showTaskDeleted(taskRemoved, tasks.size());
                        break;
                    }
                    case TODO: {
                        Task task = Parser.parseTodo(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case DEADLINE: {
                        Task task = Parser.parseDeadline(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case EVENT: {
                        Task task = Parser.parseEvent(arguments);
                        tasks.add(task);
                        storage.saveTasks(tasks);
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case FIND: {
                        String keyword = Parser.parseFindKeyword(arguments);
                        TaskList matchingTasks = tasks.find(keyword);
                        ui.showMatchingTasks(matchingTasks);
                        break;
                    }
                    case ON: {
                        LocalDate date = Parser.parseDateQuery(arguments);
                        ui.showTasksOnDate(tasks, date);
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
                    default: {
                        throw new BotException("I don't know what that command means.");
                    }
                }
            } catch (BotException error) {
                ui.showError(error.getMessage());
            } catch (IOException error) {
                ui.showSavingError();
            }
            ui.showCommandSeparator();
        }
        ui.showGoodbye();
    }
}
