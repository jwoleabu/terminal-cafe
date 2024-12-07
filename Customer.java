import helpers.Client;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Customer {
    public static void main(String[] args) {
        System.out.print("Enter your name: ");
        try{
            Scanner in = new Scanner(System.in);
            String name = in.nextLine();
            try(Client client = new Client(name)){
                client.startListener();
                 AtomicBoolean running = new AtomicBoolean(true);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    client.terminate();
                    if(running.get()){
                        System.out.println("Interrupt received, terminating the program");
                    }
                    else {
                        System.out.println("Goodbye, thank you for visiting the cafe!");
                    }
                }));

                while (running.get()){

                    String command = in.nextLine();
                    command = command.toLowerCase();


                    if (command.startsWith("order")){

                        if (command.length() < 6){
                            System.out.println("Please enter a valid order");
                            continue;
                        }

                        String order = command.substring(6);
                        order = order.trim();
                        if (order.isEmpty()){
                            break;
                        }
                        String[] items = order.split(" and ");
                        Map<String, Integer> itemsMap = new HashMap<>();
                        int failedOrder = 0;
                        for (String item : items){
                            item = item.trim();
                            String[] parts = item.split("\\s+", 2);

                            if (parts.length == 2 && isNumeric(parts[0])) {
                                int quantity = Integer.parseInt(parts[0]);
                                String itemName = parts[1].trim();
                                switch (itemName){
                                    case "tea", "teas":
                                        itemsMap.merge("tea", quantity, Integer::sum);
                                        break;
                                    case "coffee", "coffees":
                                        itemsMap.merge("coffee", quantity, Integer::sum);
                                        break;
                                }
                            } else {
                                System.out.println("Invalid item format: " + item);
                                failedOrder++;
                                break;
                            }
                        }

                        if (failedOrder > 0 || itemsMap.isEmpty()){
                            System.out.println("Your order has incorrect items, it has been terminated please make a new order");
                        }
                        else {
                            client.order(itemsMap);
                        }
                        continue;
                    }

                    switch (command) {
                        case "status":
                            client.status();
                            break;

                        case "collect":
                            client.collect();
                            break;

                        case "exit":
                            client.exit();
                            running.set(false);
                            break;

                        default:
                            System.out.println("Unknown command. Available commands: order, status, terminate.");
                    }
                }

            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static boolean isNumeric(String str){
        return str != null && str.matches("[0-9]+");
    }
}
