package Client;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Customer {
    public static void main(String[] args) {
        System.out.print("Enter your name: ");
        try{
            Scanner in = new Scanner(System.in);
            String name = in.nextLine();
            try(Client client = new Client(name)){
                AtomicBoolean running = new AtomicBoolean(true);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    client.Terminate();
                    running.set(false);
                    System.out.println("Thank you for visiting the cafe, goodbye!");
                }));

                while (running.get()){

                    String command = in.nextLine();

                    switch (command.toLowerCase()) {
                        case "order":
                            HashMap<String, Integer> order = new HashMap<>();
                            order.put("Tea", 2);
                            order.put("Coffee", 1);
                            order.put("Carrots", 1);
                            order.put("Milk", 1);
                            order.put("Apple", 1);

                            ArrayList<String> invalidItems = client.Order(order);
                            System.out.println("Order sent!");
                            if (!invalidItems.isEmpty()){
                                System.out.println("The following items you tried to order are unavailable: " + String.join(", ", invalidItems));
                            }

                            break;
                        case "status":
                            System.out.println("Order status");
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
}
