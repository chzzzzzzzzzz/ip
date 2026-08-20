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
        String[] tasks = new String[100];
        int taskCount = 0;
        while (!input.equals("bye")) {
            System.out.println("    " + line);
            if (input.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(String.format("    %d. %s", i + 1, tasks[i]));
                }
            } else {
                System.out.println(String.format("    added: %s", input));
                tasks[taskCount] = input;
                taskCount++;
            }
            System.out.println("    " + line);
            input = scanner.nextLine();
        }
        System.out.println(line);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }
}
