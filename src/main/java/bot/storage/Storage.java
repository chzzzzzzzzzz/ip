package bot.storage;

import bot.task.Deadline;
import bot.task.Event;
import bot.task.Task;
import bot.task.TaskList;
import bot.task.Todo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Loads and saves the chatbot's tasks using a file on disk.
 */
public class Storage {
    private final File path;

    /**
     * Creates storage that writes to the given file.
     *
     * @param path location of the data file
     */
    public Storage(File path) {
        this.path = path;
    }

    /**
     * Loads tasks from the data file, preserving their types and done statuses.
     *
     * @return tasks stored in the data file, or an empty list if the file does not exist
     * @throws IOException if the data file cannot be read or contains an unknown task type
     */
    public TaskList loadTasks() throws IOException {
        TaskList tasks = new TaskList();
        if (!path.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(path)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(parseTask(line, lineNumber));
            }
        }
        return tasks;
    }

    /**
     * Converts one validated data-file line into a task.
     *
     * @param line line read from the data file
     * @param lineNumber one-based line number used in error messages
     * @return task represented by the line
     * @throws IOException if the line does not follow the storage format
     */
    private Task parseTask(String line, int lineNumber) throws IOException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw invalidData(lineNumber, "every task must contain a type, status, and description");
        }

        String type = parts[0];
        String status = parts[1];
        String description = parts[2];
        if (!status.equals("0") && !status.equals("1")) {
            throw invalidData(lineNumber, "status must be 0 or 1");
        }
        if (description.isEmpty()) {
            throw invalidData(lineNumber, "description cannot be empty");
        }

        Task task;
        switch (type) {
            case "T":
                ensureFieldCount(parts, 3, lineNumber, "todo");
                task = new Todo(description);
                break;
            case "D":
                ensureFieldCount(parts, 4, lineNumber, "deadline");
                if (parts[3].isEmpty()) {
                    throw invalidData(lineNumber, "deadline date cannot be empty");
                }

                task = new Deadline(description, parseDeadlineDate(parts[3], lineNumber));
                break;
            case "E":
                ensureFieldCount(parts, 5, lineNumber, "event");
                if (parts[3].isEmpty() || parts[4].isEmpty()) {
                    throw invalidData(lineNumber, "event start and end times cannot be empty");
                }
                LocalDateTime from = parseEventDateTime(parts[3], lineNumber);
                LocalDateTime to = parseEventDateTime(parts[4], lineNumber);
                if (!to.isAfter(from)) {
                    throw invalidData(lineNumber, "event end must be after its start");
                }
                task = new Event(description, from, to);
                break;
            default:
                throw invalidData(lineNumber, "unknown task type '" + type + "'");
        }

        if (status.equals("1")) {
            task.mark();
        }
        return task;
    }

    /**
     * Parses a deadline date stored in ISO format.
     *
     * @param value saved date text
     * @param lineNumber one-based data-file line number
     * @return parsed deadline date
     * @throws IOException if the saved date is invalid
     */
    private LocalDate parseDeadlineDate(String value, int lineNumber) throws IOException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException error) {
            throw invalidData(lineNumber, "deadline date must use yyyy-MM-dd");
        }
    }

    /**
     * Parses an event date and time stored in ISO format.
     *
     * @param value saved date-time text
     * @param lineNumber one-based data-file line number
     * @return parsed event date and time
     * @throws IOException if the saved date and time are invalid
     */
    private LocalDateTime parseEventDateTime(String value, int lineNumber) throws IOException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException error) {
            throw invalidData(lineNumber, "event date and time must use ISO format");
        }
    }

    /**
     * Checks that a saved task contains exactly the fields required by its type.
     *
     * @param parts fields parsed from the saved task
     * @param expectedCount required number of fields
     * @param lineNumber one-based data-file line number
     * @param taskType name of the task type for the error message
     * @throws IOException if the field count is incorrect
     */
    private void ensureFieldCount(String[] parts, int expectedCount, int lineNumber, String taskType)
            throws IOException {
        if (parts.length != expectedCount) {
            throw invalidData(lineNumber, taskType + " must contain " + expectedCount + " fields");
        }
    }

    /**
     * Creates a consistently formatted exception for invalid saved data.
     *
     * @param lineNumber one-based data-file line number
     * @param reason explanation of the format problem
     * @return exception describing the malformed line
     */
    private IOException invalidData(int lineNumber, String reason) {
        return new IOException("invalid data on line " + lineNumber + " (" + reason + ")");
    }

    /**
     * Overwrites the data file with the current task list.
     *
     * @param tasks tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void saveTasks(TaskList tasks) throws IOException {
        File parentDirectory = path.getParentFile();
        if (parentDirectory != null) {
            if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
                throw new IOException("Could not create data directory: " + parentDirectory);
            }
            if (!parentDirectory.isDirectory()) {
                throw new IOException("Data directory path is not a directory: " + parentDirectory);
            }
        }

        try (FileWriter writer = new FileWriter(path)) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        }
    }
}
