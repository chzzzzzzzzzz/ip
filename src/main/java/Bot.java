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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(String.format("    %d.[%s] %s", i + 1, tasks[i].getStatusIcon(),
                                                                                tasks[i].description));
                }
            } else if (parts[0].equals("mark")) {
                int num = Integer.parseInt(parts[1]) - 1;
                tasks[num].mark();
                System.out.println("    Nice! I've marked this task as done:");
                System.out.println(String.format("        [%s] %s", tasks[num].getStatusIcon(),
                                                                    tasks[num].description));
            } else if (parts[0].equals("unmark")) {
                int num = Integer.parseInt(parts[1]) - 1;
                tasks[num].unmark();
                System.out.println("    OK! Get it done soon.");
                System.out.println(String.format("        [%s] %s", tasks[num].getStatusIcon(),
                                                                    tasks[num].description));
            }
            else {
                System.out.println(String.format("    added: %s", input));
                tasks[taskCount] = new Task(input);
                taskCount++;
            }
            System.out.println("    " + line);
            input = scanner.nextLine();
            parts = input.split(" ", 2);
        }
        System.out.println(line);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }
}
