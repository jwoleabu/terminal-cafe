package helpers;

public class Drink {
    String name;
    String customer;
    String id;

    Drink(String Name, String customer, String id) {
        this.name = Name;
        this.customer = customer;
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s for %s", name, customer);
    }
}
