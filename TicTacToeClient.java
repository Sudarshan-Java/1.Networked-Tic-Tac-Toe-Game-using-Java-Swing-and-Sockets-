import java.io.*;
import java.net.*;

public class TicTacToeClient {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java TicTacToeClient <server_ip>");
            return;
        }

        String serverIP = args[0];
        try {
            System.out.println("Connecting to server at " + serverIP + "...");
            Socket socket = new Socket(serverIP, 5000);
            System.out.println("Connected to server!");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            GameWindow game = new GameWindow("Client - You are O", 'O', false);

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
        } catch (UnknownHostException e) {
            System.err.println("Could not find server: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
