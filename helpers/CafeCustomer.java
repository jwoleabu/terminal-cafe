package helpers;
import java.util.UUID;

public class CafeCustomer {
    private final String name;
    private final UUID id;
    private CustomerState state;
    private final String idString;

    public enum CustomerState{
        IDLE,
        WAITING,
        COMPLETED,
    }

    public void setState(CustomerState state){
        this.state = state;
    }

    public CustomerState getState(){
        return state;
    }

    public boolean stateIs(CustomerState state){
        return this.state == state;
    }

    CafeCustomer(String name){
        this.name = name;
        this.id = UUID.randomUUID();
        this.state = CustomerState.IDLE;
        this.idString = id.toString();
    }

    public String getName(){
        return name;
    }
    public String getId(){
        return idString;
    }
}
