import java.util.Scanner;

public class Bully {
    static final int TOTAL_PROCESSES = 5;
    static boolean[] state = new boolean[TOTAL_PROCESSES];
    static int coordinator = TOTAL_PROCESSES; // Start with highest process as coordinator

    public static void bringProcessUp(int processId) {
        if (processId < 1 || processId > TOTAL_PROCESSES) {
            System.out.println("Invalid process ID.");
            return;
        }

        if (state[processId - 1]) {
            System.out.println("Process " + processId + " is already up.");
            return;
        }

        state[processId - 1] = true;
        System.out.println("Process " + processId + " is now up and initiates election...");

        boolean newCoordinatorElected = false;
        for (int i = processId; i < TOTAL_PROCESSES; i++) {
            System.out.println("Election message sent from Process " + processId + " to Process " + (i + 1));
        }

        for (int i = TOTAL_PROCESSES; i > processId; i--) {
            if (state[i - 1]) {
                coordinator = i;
                System.out.println("Process " + i + " is alive and becomes the new coordinator.");
                newCoordinatorElected = true;
                break;
            }
        }

        if (!newCoordinatorElected) {
            coordinator = processId;
            System.out.println("Process " + processId + " becomes the new coordinator.");
        }
    }

    public static void bringProcessDown(int processId) {
        if (processId < 1 || processId > TOTAL_PROCESSES) {
            System.out.println("Invalid process ID.");
            return;
        }

        if (!state[processId - 1]) {
            System.out.println("Process " + processId + " is already down.");
            return;
        }

        state[processId - 1] = false;
        System.out.println("Process " + processId + " is now down.");
        if (coordinator == processId) {
            System.out.println("Coordinator is down. Election required.");
        }
    }

    public static void sendMessage(int senderId) {
        if (senderId < 1 || senderId > TOTAL_PROCESSES) {
            System.out.println("Invalid process ID.");
            return;
        }

        if (!state[senderId - 1]) {
            System.out.println("Process " + senderId + " is down and cannot send messages.");
            return;
        }

        if (state[coordinator - 1]) {
            System.out.println("Coordinator (Process " + coordinator + ") is alive. OK.");
        } else {
            System.out.println("Coordinator is down. Process " + senderId + " initiates election.");
            for (int i = senderId; i < TOTAL_PROCESSES; i++) {
                System.out.println("Election message sent from Process " + senderId + " to Process " + (i + 1));
            }

            for (int i = TOTAL_PROCESSES; i >= senderId; i--) {
                if (state[i - 1]) {
                    coordinator = i;
                    System.out.println("Process " + i + " is alive and becomes the new coordinator.");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Initialize all processes as up
        for (int i = 0; i < TOTAL_PROCESSES; i++) {
            state[i] = true;
        }

        System.out.println("All processes are up.");
        System.out.println("Initial coordinator is Process " + coordinator);

        int choice;
        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Bring a process UP");
            System.out.println("2. Bring a process DOWN");
            System.out.println("3. Send a message (trigger election if coordinator is down)");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter process ID to bring UP: ");
                    int up = sc.nextInt();
                    bringProcessUp(up);
                break;
                case 2:
                    System.out.print("Enter process ID to bring DOWN: ");
                    int down = sc.nextInt();
                    bringProcessDown(down);
                break;
                case 3:
                    System.out.print("Enter sender process ID: ");
                    int sender = sc.nextInt();
                    sendMessage(sender);
                break;
                case 4: System.out.println("Exiting...");
                break;
                default: System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 4);

        sc.close();
    }
}
