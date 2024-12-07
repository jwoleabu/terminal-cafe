package helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Client implements AutoCloseable{
    final int port = 8080;
    private final Scanner reader;
    private final PrintWriter writer;
    private Thread listenerThread;
    public AtomicBoolean listening;
    Gson gson;

    public Client(String name) throws IOException {
        Socket socket = new Socket("localhost", port);
        reader = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
        ServerListener serverListener = new ServerListener(this);
        listening = new AtomicBoolean(false);

        gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        Request request = new Request(RequestType.JOIN, data);
        writer.println(gson.toJson(request));
    }

    public void startListener() {
        ServerListener listener = new ServerListener(this);
        listening.set(true);
        listenerThread = new Thread(listener);
        listenerThread.start();
    }

    public void stopListener() {
        if (listenerThread != null) {
            listening.set(false);
            listenerThread.interrupt();
        }
    }

    public void order(Map<String, Integer> customerOrder ){

        JsonObject data = new JsonObject();
        JsonObject orderItems = new JsonObject();
        for (Map.Entry<String, Integer> entry : customerOrder.entrySet()) {
            orderItems.addProperty(entry.getKey(), entry.getValue());
        }
        data.add("items", orderItems);
        Request request = new Request(RequestType.ORDER, data);

        writer.println(gson.toJson(request));
    }

    public void status(){
        Request request = new Request(RequestType.STATUS, null);
        writer.println(gson.toJson(request));
    }

    public void collect(){
        Request request = new Request(RequestType.COLLECT, null);
        writer.println(gson.toJson(request));
    }

    public void exit(){
        stopListener();
        Request request = new Request(RequestType.EXIT, null);
        writer.println(gson.toJson(request));
    }

    public void terminate(){
        Request request = new Request(RequestType.TERMINATE, null);
        writer.println(gson.toJson(request));
    }

    public String readMessage(){
        if(reader.hasNextLine()) {
            String messageJson = reader.nextLine();
            try {
                Request request = gson.fromJson(messageJson, Request.class);
                RequestType requestType = request.getType();
                if(requestType == RequestType.MESSAGE | requestType == RequestType.ERROR){
                    Message message = request.getDataAs(Message.class);
                    return message.getMessage();
                }
            }
            catch (JsonSyntaxException e){
                System.err.println("Invalid JSON format: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
        stopListener();
    }
}
