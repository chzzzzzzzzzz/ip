package bot.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the collection of tasks used by the chatbot.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return task at the index
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Deletes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return deleted task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index zero-based task index
     * @return task that was marked
     */
    public Task mark(int index) {
        Task task = getTask(index);
        task.mark();
        return task;
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index zero-based task index
     * @return task that was unmarked
     */
    public Task unmark(int index) {
        Task task = getTask(index);
        task.unmark();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only iterator over the tasks for operations such as saving.
     *
     * @return iterator over tasks in list order
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
