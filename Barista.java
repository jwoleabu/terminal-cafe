import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import helpers.Cafe;
import helpers.ClientHandler;

public class Barista {
    private final static int port = 8080;
    private final static Cafe cafe = new Cafe();
    public static void main(String[] args) {
        startServer();

    }

    private static void startServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Awaiting incoming connections at port " + port + "...");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, cafe)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
		
}
