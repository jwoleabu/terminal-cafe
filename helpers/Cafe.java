package helpers;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Cafe {
    private final LinkedBlockingQueue<Drink> teaQueue;
    private final LinkedBlockingQueue<Drink> coffeeQueue;
    private final Drink[] brewingArea;
    public ConcurrentHashMap<String, Order> orders;
    private final Set<String> users;
    private final Tray tray;
    private Logger logger;

    public Cafe() {
        teaQueue = new LinkedBlockingQueue<>();
        coffeeQueue = new LinkedBlockingQueue<>();
        brewingArea = new Drink[4];
        this.orders = new ConcurrentHashMap<String, Order>();
        this.users = ConcurrentHashMap.newKeySet();
        this.tray = new Tray();
        try {
            logger = new Logger();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (logger != null) {
                try {
                    logger.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Shutting down server");
        }));


        // Creating the 4 brewer threads, setting their collection and deposit sources and their brewing area index
        Brewer teabrewer1 = new Brewer(3, teaQueue, tray, this, brewingArea, 0);
        Brewer teabrewer2 = new Brewer(3, teaQueue, tray, this,brewingArea, 1 );
        Brewer coffeebrewer1 = new Brewer(5, coffeeQueue, tray, this, brewingArea, 2);
        Brewer coffeebrewer2 = new Brewer(5, coffeeQueue, tray, this, brewingArea, 3);

        Thread teaThread1 = new Thread(teabrewer1);
        Thread teaThread2 = new Thread(teabrewer2);
        Thread coffeeThread1 = new Thread(coffeebrewer1);
        Thread coffeeThread2 = new Thread(coffeebrewer2);

        teaThread1.start();
        teaThread2.start();
        coffeeThread1.start();
        coffeeThread2.start();
    }

    public void enter(String clientId) {
        users.add(clientId);
    }

    public void leave(String clientId) {
        users.remove(clientId);
    }

    public void leaveOrder(String clientName) {
        orders.remove(clientName);
        tray.remove(clientName);
    }
    public Boolean contains(String clientName) {
        return users.contains(clientName);
    }

    // loads the tea and coffee queues iteratively creating an object for each drink on each queue
    public synchronized void handleOrder(Order order) {
        if (!orders.containsKey(order.getId())) {
            orders.put(order.getId(), order);
        }
        else {
            orders.get(order.getId()).append(order);
        }
        for (int i = 0; i < order.getRequiredTea(); i++) {
            Drink tea = new Drink("tea", order.getName(), order.getId());
            synchronized (this) {
                teaQueue.add(tea);
                cafeState("add tea");
            }
        }
        for (int i = 0; i < order.getRequiredCoffee(); i++) {
            Drink coffee = new Drink("coffee", order.getName(), order.getId());
            synchronized (this) {
                coffeeQueue.add(coffee);
                cafeState("add coffee");
            }
        }
        System.out.println("order made for " + order.getName());
    }

    public void cafeState(String context) {
            synchronized (brewingArea){
                int brewTeaCount = 0;
                int brewCoffeeCount = 0;
                for (Drink drink : brewingArea){
                    if (drink != null && "tea".equals(drink.name)) {
                        brewTeaCount++;
                    } else if (drink != null && "coffee".equals(drink.name)) {
                        brewCoffeeCount++;
                    }
                }

                String cafeState = String.format("waiting area[%d tea][%d coffee] brewing area [%d tea] [%d coffee] Tray [%d tea] [%d coffee] [%d clients] [%d waiting] %s", teaQueue.size(), coffeeQueue.size(), brewTeaCount, brewCoffeeCount, tray.teaSize(), tray.coffeeSize(), users.size(), orders.size(), context);
                System.out.println(cafeState);
                try {
                    logger.write(cafeState);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void transferItems(String clientName) {
        synchronized (brewingArea) {
            for (Drink drink : brewingArea) {
                if (drink == null) {
                    continue;
                }
                if ("tea".equals(drink.name) && drink.id.equals(clientName)){
                    processTeaQueue(clientName, drink);
                }
                else if ("coffee".equals(drink.name) && drink.id.equals(clientName)){
                    processCoffeeQueue(clientName, drink);
                }
            }
        }

    }

    public void transferTray(String clientName) {
        if (!tray.contains(clientName)) {
            return;
        }
        Order order = tray.get(clientName);
        if (order.getBrewedTea() > 0) {
            synchronized (teaQueue) {
                while (true) {
                    Drink transfer = teaQueue.poll();
                    if (transfer == null) {
                        break;
                    } else if (transfer.id.equals(clientName)) {
                        System.out.println("the customer left the cafe not making this drink " + clientName);
                        continue;
                    }

                    }}
            return;
        }
    }

    private void processTeaQueue(String clientName, Drink drink) {
        synchronized (teaQueue) {
            while (true) {
                Drink transfer = teaQueue.poll();
                if (transfer == null) {
                    break;
                } else if (transfer.id.equals(clientName)) {
                    System.out.println("the customer left the cafe not making this drink " + drink.customer);
                    continue;
                }
                drink.id = transfer.id;
                System.out.println("transferred tea from " + drink.customer + " to " + transfer.customer);
                break;
            }
        }
    }

    private void processCoffeeQueue(String clientName, Drink drink) {
        synchronized (coffeeQueue) {
            while (true) {
                Drink transfer = coffeeQueue.poll();
                if (transfer == null) {
                    break;
                } else if (transfer.id.equals(clientName)) {
                    System.out.println("the customer left the cafe not making this drink " + drink.customer);
                    continue;
                }
                drink.id = transfer.id;
                System.out.println("transferred coffee from " + drink.customer + " to " + transfer.customer);
                break;
            }
        }
    }

    public OrderStatus clientStatus(String clientName) {
        Order order = orders.get(clientName);
        if (order == null) {
            return null;
        }

        synchronized (brewingArea) {
            int brewTeaCount = 0;
            int brewCoffeeCount = 0;

            for (Drink drink : brewingArea) {
                if (drink != null && clientName.equals(drink.id)) {
                    if ("tea".equals(drink.name)) {
                        brewTeaCount++;
                    } else if ("coffee".equals(drink.name)) {
                        brewCoffeeCount++;
                    }
                }
            }

            return new OrderStatus(order.getRequiredTea() - order.getBrewedTea() - brewTeaCount,
                    order.getRequiredCoffee() - order.getBrewedCoffee() - brewCoffeeCount,
                    brewTeaCount,
                    brewCoffeeCount,
                    order.getBrewedTea(),
                    order.getBrewedCoffee());
        }
    }

    public boolean collect(String name) {
        Order operationStatus = orders.compute(name, (key, order) -> {
            if (order != null && order.isComplete()) {
                tray.remove(name);
                return null;
            }
            return order;
        });
        return operationStatus == null;
    }
}
