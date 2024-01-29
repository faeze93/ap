import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 3333;
    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server is running on port "+ PORT);
            while (true){
                Socket clientSocket= serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                new ClientHandler(clientSocket).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket){
            this.clientSocket = socket;
        }

        public void run() {
            try(
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
                    ){
                String inputLine;
                StringBuilder userData = new StringBuilder();
                while ((inputLine = in.readLine()) != null){
                    userData.append(inputLine);
                }
                String [] userInfo = userData.toString().split(",");
                String email = userInfo[0];
                String password = userInfo[1];
                saveUserToFile(email,password);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveUserToFile(String email, String password){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(email + ".txt"))){
                writer.write(email + "," + password);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}