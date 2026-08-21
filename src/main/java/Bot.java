import java.util.Scanner;

/**
 * A simple chatbot that stores and displays tasks until the user says goodbye.
 */
public class Bot {
    /**
     * Starts the chatbot and handles commands entered by the user.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        String line = "_".repeat(60);
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
        System.out.println(line);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] parts = input.split(" ", 2);
        Task[] tasks = new Task[100];
        int taskCount = 0;
        while (!input.equals("bye")) {
            System.out.println("    " + line);
            if (input.equals("list")) {
                System.out.println("    Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(String.format("    %d.%s", i + 1, tasks[i]));
                }
            } else if (parts[0].equals("mark")) {
                int num = Integer.parseInt(parts[1]) - 1;
                tasks[num].mark();
                System.out.println("    Nice! I've marked this task as done:");
                System.out.println("        " + tasks[num]);
            } else if (parts[0].equals("unmark")) {
                int num = Integer.parseInt(parts[1]) - 1;
                tasks[num].unmark();
                System.out.println("    OK, I've marked this task as not done yet:");
                System.out.println("        " + tasks[num]);
            } else if (parts[0].equals("todo")) {
                tasks[taskCount] = new Todo(parts[1]);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (parts[0].equals("deadline")) {
                String[] deadlineParts = parts[1].split(" /by ", 2);
                tasks[taskCount] = new Deadline(deadlineParts[0], deadlineParts[1]);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (parts[0].equals("event")) {
                String[] eventParts = parts[1].split(" /from ", 2);
                String[] timeParts = eventParts[1].split(" /to ", 2);
                tasks[taskCount] = new Event(eventParts[0], timeParts[0], timeParts[1]);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            }
            System.out.println("    " + line);
            input = scanner.nextLine();
            parts = input.split(" ", 2);
        }
        System.out.println(line);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount total number of tasks after the addition
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("    Got it. I've added this task:");
        System.out.println("        " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }
}
