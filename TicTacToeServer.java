import java.io.*;
import java.net.*;

public class TicTacToeServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server started. Waiting for client to connect...");
            System.out.println("Server IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Port: 5000");
            
            Socket socket = serverSocket.accept();
            System.out.println("Client connected from: " + socket.getInetAddress().getHostAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            GameWindow game = new GameWindow("Server - You are X", 'X', true);

            game.setMoveListener(index -> {
                try {
                    out.writeInt(index);
                } catch (IOException e) {
                    System.err.println("Error sending move: " + e.getMessage());
                    game.showError("Connection lost!");
                }
            });

            while (true) {
                try {
                    int move = in.readInt();
                    game.receiveMove(move);
                } catch (IOException e) {
                    System.err.println("Error receiving move: " + e.getMessage());
                    game.showError("Connection lost!");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
