package helpers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

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

    public <T> T getDataAs(Class<T> type) throws JsonSyntaxException {
        return new Gson().fromJson(data, type);
    }
    public <T> T getDataAs(Type type) {
        return new Gson().fromJson(data, type);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
