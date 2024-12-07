package helpers;

import java.util.concurrent.LinkedBlockingQueue;

class Brewer implements Runnable {
    public double brewTime;
    private final LinkedBlockingQueue<Drink> queue;
    private final Drink[] brewingChamber;
    private final Tray tray;
    private final Cafe cafe;
    int index;

    Brewer(float brewTime, LinkedBlockingQueue<Drink> queue, Tray tray, Cafe cafe, Drink[] brewingChamber, int index) {
        this.brewTime = brewTime;
        this.queue = queue;
        this.tray = tray;
        this.brewingChamber = brewingChamber;
        this.index = index;
        this.cafe = cafe;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Drink drink = queue.take();
                if (!cafe.contains(drink.id)){
                    System.out.println("the customer left the cafe not making this drink " + drink.customer);
                    continue;
                }

                    brewingChamber[index] = drink;
                    cafe.cafeState(String.format("put %s in brewing area", drink.name));

                    Thread.sleep((long) (brewTime * 1000));
                    brewingChamber[index] = null;

                    synchronized (tray) {
                        Order order = cafe.orders.get(drink.id);

                        if (drink.name.equals("tea")) {
                            order.brewTea();
                        } else if (drink.name.equals("coffee")) {
                            order.brewCoffee();
                        }

                        if (!tray.contains(order)) {
                            tray.add(order);
                            cafe.cafeState(String.format("put %s in tray", drink.name));
                        } else {
                            if (drink.name.equals("tea")) {
                                tray.addBrewedTea();
                                    cafe.cafeState(String.format("put %s in tray", drink.name));
                            } else if (drink.name.equals("coffee")) {
                                tray.addBrewedCoffee();
                                    cafe.cafeState(String.format("put %s in tray", drink.name));
                            }
                        }
                        if (order.isComplete()) {
                            order.complete();
                        }
                    }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }


    }
}
