package helpers;

public class OrderStatus {
    int queueTea, queueCoffee, brewingTea, brewingCoffee, brewedTea, brewedCoffee;
    OrderStatus (int queueTea, int queueCoffee, int brewingTea, int brewingCoffee, int brewedTea, int brewedCoffee){
        this.queueTea = queueTea;
        this.queueCoffee = queueCoffee;
        this.brewingTea = brewingTea;
        this.brewingCoffee = brewingCoffee;
        this.brewedTea = brewedTea;
        this.brewedCoffee = brewedCoffee;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
            teaAndCoffee(stringBuilder, queueTea, queueCoffee, " in waiting area");
            teaAndCoffee(stringBuilder, brewingTea, brewingCoffee, " currently being prepared");
            teaAndCoffee(stringBuilder, brewedTea, brewedCoffee, " in tray");
        return stringBuilder.toString().trim();
    }

    private void teaAndCoffee(StringBuilder stringBuilder, int tea, int coffee, String areaMessage) {
        if (!(tea > 0 || coffee > 0)) {
            return;
        }
            stringBuilder.append("- ");
            if (tea > 0) {
                stringBuilder.append(tea).append(tea == 1 ? " tea" : " teas");
            }
            if (coffee > 0) {
                if (tea > 0) {
                    stringBuilder.append(" and ");
                }
                stringBuilder.append(coffee).append(coffee == 1 ? " coffee" : " coffees");
            }
            stringBuilder.append(areaMessage);
            stringBuilder.append("\n");
    }
}
