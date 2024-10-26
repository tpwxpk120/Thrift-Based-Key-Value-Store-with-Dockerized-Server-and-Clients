package client;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

/**
 * ClientLogger logs client activities to a file with timestamps.
 * Thread-safe to handle concurrent writes.
 */
public class ClientLogger implements AutoCloseable {
    private FileWriter writer;
    private final Object lock = new Object();  // Lock object for thread safety

    // Constructor to initialize the logger with the specified file name
    public ClientLogger(String filename) throws IOException {
        // Creates log file inside "client" directory
        File file = new File("clientlog", filename);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        writer = new FileWriter(file, true);
    }

    // Logs a message with a timestamp to the file (thread-safe)
    public void log(String message) throws IOException {
        synchronized (lock) {
            String timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .format(LocalDateTime.now());
            writer.write(timeStamp + " - " + message + "\n");
            writer.flush();
        }
    }

    // Closes the FileWriter when done (thread-safe)
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
