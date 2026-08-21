/**
 * Represents a task that can be marked as done or not done.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description and an initial not-done status.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void mark() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the symbol used to show whether this task is done.
     *
     * @return {@code X} if done, or a space if not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
