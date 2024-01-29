import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class UserRegistrationServer {
    private static final int PORT = 12345;
    private static final String USER_DATA_PATH = "https://github.com/faeze93/ap/tree/main";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                String inputLine;
                StringBuilder userData = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    userData.append(inputLine);
                }
                // Parse the received data
                String[] dataParts = userData.toString().split(",");
                String operation = dataParts[0];
                String username = dataParts[1];
                String password = dataParts[2];

                // Perform operation based on the request
                if (operation.equals("REGISTER")) {
                    String fileName = USER_DATA_PATH + username + ".txt";
                    File file = new File(fileName);
                    if (file.exists()) {
                        out.println("User already exists");
                    } else {
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                            writer.write("Username: " + username + ", Password: " + password);
                            out.println("User registered successfully");
                        }
                    }
                } else if (operation.equals("LOGIN")) {
                    String fileName = USER_DATA_PATH + username + ".txt";
                    File file = new File(fileName);
                    if (!file.exists()) {
                        out.println("User does not exist");
                    } else {
                        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                            String storedPassword = reader.readLine().split(":")[1].trim();
                            if (storedPassword.equals(password)) {
                                out.println("Login successful");
                            } else {
                                out.println("Invalid password");
                            }
                        }
                    }
                } else {
                    out.println("Invalid operation");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
