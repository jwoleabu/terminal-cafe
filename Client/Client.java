package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import Helpers.Request;
import Helpers.RequestType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Client implements AutoCloseable{
    final int port = 8080;
    private final Scanner reader;
    private final PrintWriter writer;
    Gson gson;

    public Client(String name) throws IOException {
            Socket socket = new Socket("localhost", port);
            reader = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

        gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        Request request = new Request(RequestType.JOIN, data);
        writer.println(gson.toJson(request));
    }

    public ArrayList<String> Order(Map<String, Integer> customerOrder ){

        ArrayList<String> invalidItems = ValidateOrder(customerOrder);

        JsonObject data = new JsonObject();
        JsonObject orderItems = new JsonObject();
        for (Map.Entry<String, Integer> entry : customerOrder.entrySet()) {
            orderItems.addProperty(entry.getKey(), entry.getValue());
        }
        data.add("items", orderItems);
        Request request = new Request(RequestType.ORDER, data);

        writer.println(gson.toJson(request));
        return invalidItems;
    }

    private ArrayList<String> ValidateOrder(Map<String, Integer> customerOrder){
        Map<String, Double> menu = new HashMap<>();
        ArrayList<String> invalidItems = new ArrayList<>();
        menu.put("Coffee", 1.50);
        menu.put("Tea", 3.50);

        Iterator<Map.Entry<String, Integer>> iterator = customerOrder.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (!menu.containsKey(entry.getKey())) {
                invalidItems.add(entry.getKey());
                iterator.remove();
            }
        }
        return invalidItems;
    }

    private void Collect(){
        Request request = new Request(RequestType.COLLECT, null);
        writer.println(gson.toJson(request));
    }

    public void Terminate(){
        Request request = new Request(RequestType.LEAVE, null);
        writer.println(gson.toJson(request));
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }
}
