package helpers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Tray {
    private final ConcurrentHashMap<String, Order> trayMap;
    private final AtomicInteger coffeeCount;
    private final AtomicInteger teaCount;
    public Tray() {
        trayMap = new ConcurrentHashMap<>();
        teaCount = new AtomicInteger(0);
        coffeeCount = new AtomicInteger(0);
    }

    public void add(Order order) {
        trayMap.put(order.getId(), order);
        teaCount.addAndGet(order.getBrewedTea());
        coffeeCount.addAndGet(order.getBrewedCoffee());
    }

    public void addBrewedTea() {
        teaCount.incrementAndGet();
    }

    public void addBrewedCoffee() {
        coffeeCount.incrementAndGet();
    }

    public boolean contains(Order order) {
        return trayMap.containsKey(order.getId());
    }

    public boolean contains(String name) {
        return trayMap.containsKey(name);
    }

    public void remove(String name) {
        trayMap.computeIfPresent(name, (key, value) -> {
            teaCount.addAndGet(-value.getBrewedTea());
            coffeeCount.addAndGet(-value.getBrewedCoffee());
            return null;
        });
    }

    public void removeAll() {
        trayMap.clear();
        teaCount.set(0);
        coffeeCount.set(0);
    }

    public int teaSize() {
        return teaCount.get();

    }

    public int coffeeSize(){
        return coffeeCount.get();
    }


    public Order get(String clientName) {
        return trayMap.get(clientName);
    }
}
