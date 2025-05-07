import java.io.*;
import java.net.*;
import java.util.*;

public class Berkeley {

    static final int PORT = 9876;
    static final int CLIENTS = 3;

    // Server class
static class Server extends Thread {
    static class ClientConn {
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientConn(Socket s) throws IOException {
            socket = s;
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }
    }

    List<ClientConn> clients = new ArrayList<>();

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server waiting for clients...");
            for (int i = 0; i < CLIENTS; i++) {
                Socket s = serverSocket.accept();
                ClientConn conn = new ClientConn(s);
                clients.add(conn);
                System.out.println("Client connected.");
            }

            while (true) {
                List<Long> times = new ArrayList<>();
                long serverTime = System.currentTimeMillis();
                times.add(serverTime);

                for (ClientConn c : clients) {
                    c.out.writeObject("TIME?");
                    c.out.flush();
                }

                for (ClientConn c : clients) {
                    times.add((Long) c.in.readObject());
                }

                long avg = times.stream().mapToLong(Long::longValue).sum() / times.size();
                System.out.println("\n[Server] Average time: " + new Date(avg));

                int idx = 1;
                for (ClientConn c : clients) {
                    long offset = avg - times.get(idx++);
                    c.out.writeObject(offset);
                    c.out.flush();
                }

                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//client class
static class Client extends Thread {
    int id;
    long clock;

    Client(int id, long initialClock) {
        this.id = id;
        this.clock = initialClock;
    }

    public void run() {
        try (Socket socket = new Socket("localhost", PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Client " + id + " started. Clock: " + new Date(clock));

            while (true) {
                Object cmdObj = in.readObject();
                if (cmdObj instanceof String) {
                    String cmd = (String) cmdObj;
                    if ("TIME?".equals(cmd)) {
                        out.writeObject(clock);
                        out.flush();
                        long offset = (Long) in.readObject();
                        clock += offset;
                        System.out.println("[Client " + id + "] Adjusted clock: " + new Date(clock));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
    // Main method
    public static void main(String[] args) throws Exception {
        new Server().start();

        for (int i = 1; i <= CLIENTS; i++) {
            long skewedClock = System.currentTimeMillis() + (long) (Math.random() * 3000 - 1500); // ±1500ms
            new Client(i, skewedClock).start();
        }
    }
}
