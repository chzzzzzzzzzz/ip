package bot.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task
     * @param by date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline in the format used by the storage file.
     *
     * @return storage representation containing the task type and deadline date
     */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + by;
    }

    /**
     * Checks whether this deadline falls on the specified date.
     *
     * @param date date to check
     * @return {@code true} if the deadline is on the specified date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns the deadline's type, status, description, and formatted date for display.
     *
     * @return display representation of the deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
