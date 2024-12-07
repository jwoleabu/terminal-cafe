package helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;


public class ClientHandler implements Runnable, OrderListener {
    private final Cafe cafe;
    private final Socket socket;
    private final Gson gson;
    private final Scanner reader;
    private final PrintWriter writer;
    private CafeCustomer customer;

    public ClientHandler(Socket socket, Cafe cafe) throws IOException {
        this.socket = socket;
        reader = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
        gson = new Gson();
        this.cafe = cafe;
        try{
        while (reader.hasNextLine()) {
                String input = reader.nextLine();
                if(!isValidRequest(input)){
                    message("Invalid JSON format received. Closing connection.");
                    System.err.println("Invalid JSON: " + input);
                    socket.close();
                    return;
                }
                Request joinRequest = gson.fromJson(input, Request.class);
                String customerName = joinRequest.getData().get("name").getAsString();
                if (joinRequest.getType() != RequestType.JOIN || customerName == null){
                    continue;
                }
                this.customer = new CafeCustomer(customerName);
                System.out.println(joinRequest);
                cafe.enter(customer.getId());
                cafe.cafeState(String.format("%s joined", customer.getName()));
                break;
        }}
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            if (customer == null){
                Thread.currentThread().interrupt();
            }
            else{
                String welcomeMessage = String.format("Welcome to the terminal cafe %s", customer.getName());
                message(welcomeMessage);
            }
                while (reader.hasNextLine()) {
                    String command = reader.nextLine();
                    if (!isValidRequest(command)){
                        continue;
                    }
                    Request incomingRequest = gson.fromJson(command, Request.class);
                    System.out.println(incomingRequest);

                    switch (incomingRequest.getType()){
                        case ORDER -> {
                            JsonObject order = incomingRequest.getData();
                            JsonObject items = order.getAsJsonObject("items");
                            int teaCount = getAsIntOrDefault(items, "tea", 0);
                            int coffeeCount = getAsIntOrDefault(items, "coffee", 0);
                            if (teaCount > 0 | coffeeCount > 0) {
                                Order order2 = new Order(teaCount, coffeeCount, customer.getName(), this, customer.getId());
                                cafe.handleOrder(order2);
                                customer.setState(CafeCustomer.CustomerState.WAITING);
                                message(String.format("Order received for %s (%s)",customer.getName(), order2));
                            }
                            else{
                                message("Please order a valid amount");
                            }
                        }

                        case STATUS -> {
                            if (customer.getState() == CafeCustomer.CustomerState.IDLE){
                                message("No order placed for " + customer.getName());
                            }
                            else {
                                OrderStatus orderStatus = cafe.clientStatus(customer.getId());
                                message(Objects.requireNonNullElseGet(orderStatus, () -> "No order found for " + customer.getName()));
                            };
                        }

                        case EXIT, TERMINATE -> {
                            try {
                                cafe.leave(customer.getId());
                                if (customer.stateIs(CafeCustomer.CustomerState.WAITING)){
                                    cafe.transferItems(customer.getId());
                                    cafe.leaveOrder(customer.getId());
                                } else if (customer.stateIs(CafeCustomer.CustomerState.COMPLETED)) {
                                    cafe.leaveOrder(customer.getId());
                                }
                                cafe.cafeState(String.format("%s left", customer.getName()));
                                socket.close();
                                Thread.currentThread().interrupt();
                            }
                            catch (IOException e) {}
                        }

                        case COLLECT -> {
                            switch (customer.getState()){
                                case WAITING -> {
                                    message("Your order is still processing, please wait");
                                }
                                case IDLE -> {
                                    message("You have no current orders");
                                }
                                case COMPLETED -> {
                                    if (cafe.collect(customer.getId()))
                                    {
                                        customer.setState(CafeCustomer.CustomerState.IDLE);
                                        cafe.cafeState(String.format("%s collected order", customer.getName()));
                                        message("Your order has been collected");

                                    }
                                    else {
                                        message("Your order is still processing, please wait");
                                    }
                                }
                            }
                        }
                    }


                }
            } catch (JsonSyntaxException e) {
                message("Invalid command sent please use the appropriate syntax");
                System.err.println("Invalid JSON format: " + e.getMessage());
            }
        finally {
            System.out.println("handler done");
            Thread.currentThread().interrupt();
        }
    }

    public void message(String input) {
        Message message = new Message(input);
        JsonObject messageJson = gson.toJsonTree(message).getAsJsonObject();
        Request outgoingRequest = new Request(RequestType.MESSAGE, messageJson);
        writer.println(gson.toJson(outgoingRequest));
    }

    public void message(Object input) {
        Message message = new Message(input.toString());
        JsonObject messageJson = gson.toJsonTree(message).getAsJsonObject();
        Request outgoingRequest = new Request(RequestType.MESSAGE, messageJson);
        writer.println(gson.toJson(outgoingRequest));
    }

    private int getAsIntOrDefault(JsonObject jsonObject, String key, int defaultValue) {
        return jsonObject.has(key) && !jsonObject.get(key).isJsonNull() ? jsonObject.get(key).getAsInt() : defaultValue;
    }

    @Override
    public void orderComplete(Order order) {
        customer.setState(CafeCustomer.CustomerState.COMPLETED);
        message("Order completed for " + customer.getName() + " (" + order + "), please collect");
    }

    private boolean isValidRequest(String input) {
        try {
            Request request = gson.fromJson(input, Request.class);
            if (request == null || request.getType() == null) {
                return false;
            }

            return switch (request.getType()) {
                case JOIN -> request.getData() != null && request.getData().has("name");
                case STATUS, EXIT, TERMINATE, COLLECT, ORDER -> true;
                default -> false;
            };
        }
        catch (JsonSyntaxException e) {
            return false;
        }
    }

}
