package Helpers;
import com.google.gson.JsonObject;

public class Request {

    private final RequestType type;
    private final JsonObject data;

    public Request(RequestType request, JsonObject data) {
        this.type = request;
        this.data = data;
    }

    public RequestType getType() {
        return type;
    }
    public JsonObject getData() {
        return data;
    }
}
