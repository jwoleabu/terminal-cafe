package helpers;

public class ServerListener implements Runnable {
    Client client;
    public ServerListener(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (client.listening.get()) {
            String message = client.readMessage();
            if (message != null) {
                System.out.println(message);
            }
        }
    }
}
