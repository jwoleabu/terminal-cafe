package helpers;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.RandomAccessFile;


public class Logger implements AutoCloseable {
    private static final String JSON_LOG_FILE = "logs.json";
    private RandomAccessFile file;
    boolean firstWrites;

    Logger() throws IOException {
        file = new RandomAccessFile(JSON_LOG_FILE, "rw");
        long fileLength = file.length();
        if (fileLength == 0) {
            file.writeBytes("[\n");
        }
        else {
            file.seek(file.length() - 1);
        }
        if (fileLength > -1 && fileLength < 3) {
            firstWrites = true;
        }
    }

    public void write(String message) throws IOException {
        JsonObject logEntry = new JsonObject();
        logEntry.addProperty("timestamp", System.currentTimeMillis());
        logEntry.addProperty("message", message);
        file.writeBytes("\n");
        if (firstWrites) {
            file.writeBytes(String.format("%s,",logEntry));
        }
        else {
            file.writeBytes(String.format(",%s\n",logEntry));
        }
    }

    @Override
    public void close() throws Exception {
        if (file != null) {
            long length = file.length();
            file.seek(length - 2);
            byte[] lastBytes = new byte[1];
            file.read(lastBytes);
            if (lastBytes[0] == ',') {
                file.seek(length - 2);
                file.writeBytes("\n");
            }

            file.writeBytes("]");
            file.close();
        }    }
}
