package helpers;

import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    private final AtomicInteger tea;
    private final AtomicInteger coffee;
    private final AtomicInteger brewedTea;
    private final AtomicInteger brewedCoffee;
    private final String id;
    private final String name;
    OrderListener listener;


    Order(int tea, int coffee, String name, OrderListener listener, String id) {
        this.tea = new AtomicInteger(tea);
        this.coffee = new AtomicInteger(coffee);
        this.name = name;
        this.id = id;
        brewedTea = new AtomicInteger();
        brewedCoffee = new AtomicInteger();
        this.listener = listener;
    }

    public boolean isComplete() {
        return tea.get() == brewedTea.get() && coffee.get() == brewedCoffee.get();
    }

    public boolean isEmpty() {
        return brewedTea.get() == 0 && brewedCoffee.get() == 0;
    }

    public void orderTea(int amount){
        tea.getAndAdd(amount);
    }

    public void orderCoffee(int amount){
        coffee.getAndAdd(amount);
    }

    public int getRequiredTea(){
        return tea.get();
    }

    public int getRequiredCoffee(){
        return coffee.get();
    }

    public void brewTea(){
        brewedTea.incrementAndGet();
    }
    public void brewCoffee(){
        brewedCoffee.incrementAndGet();
    }

    public int getBrewedTea(){
        return brewedTea.get();
    }

    public int getBrewedCoffee(){
        return brewedCoffee.get();
    }

    public String getName() {
        return name;
    }

    public String getId() {return id;}

    public void complete() {
        listener.orderComplete(this);
    }

    public synchronized void append(Order order){
        tea.addAndGet(order.tea.get());
        coffee.addAndGet(order.coffee.get());
    }

    public synchronized void appendComplete(Order order){
        brewedTea.addAndGet(order.brewedTea.get());
        brewedCoffee.addAndGet(order.brewedCoffee.get());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (tea.get() > 0) {
            result.append(String.format("%d tea%s", tea.get(), tea.get() == 1 ? "" : "s"));
        }
        if (coffee.get() > 0) {
            if (tea.get() > 0) {
                result.append(" and ");
            }
            result.append(String.format("%d coffee%s", coffee.get(), coffee.get() == 1 ? "" : "s"));
        }

        return result.toString();
    }
}
