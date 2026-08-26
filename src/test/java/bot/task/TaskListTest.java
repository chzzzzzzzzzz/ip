package bot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void find_caseInsensitivePartialKeyword_returnsMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 6, 6)));
        tasks.add(new Todo("buy milk"));

        TaskList matchingTasks = tasks.find("BoO");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] Read Book", matchingTasks.getTask(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", matchingTasks.getTask(1).toString());
        assertEquals(3, tasks.size());
    }

    @Test
    void find_keywordAppearsOnlyInTaskDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDate.of(2019, 12, 2)));

        TaskList matchingTasks = tasks.find("2019");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    void find_noMatchingDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(0, matchingTasks.size());
    }
}
