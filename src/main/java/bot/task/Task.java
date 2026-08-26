package bot.task;

import java.time.LocalDate;

/**
 * Represents a task that can be marked as done or not done.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description and an initial not-done status.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the symbol used to show whether this task is done.
     *
     * @return {@code X} if done, or a space if not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the common task fields used when saving this task.
     *
     * @return done status and description in storage format
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Checks whether this task occurs on the given date.
     * Tasks without dates do not occur on any specific date.
     *
     * @param date date to check.
     * @return {@code true} if the task occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the task's completion status and description for display.
     *
     * @return display representation of the task
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
