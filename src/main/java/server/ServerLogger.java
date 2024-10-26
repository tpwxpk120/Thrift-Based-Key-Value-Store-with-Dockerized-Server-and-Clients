package server;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

/**
 * ServerLogger logs server activity with timestamps to a file.
 * It supports thread-safe logging and ensures directories exist for the log file.
 */
public class ServerLogger {
    private FileWriter writer;  // Writer to log messages to a file
    private final Object lock = new Object();  // Lock object for thread safety

    // Initializes the logger and opens the log file in append mode
    public ServerLogger(String filename) throws IOException {
        // Ensure directory exists
        File file = new File("serverlog", filename);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        writer = new FileWriter(file, true);
    }

    // Logs a message with a timestamp to the log file (thread-safe)
    public void log(String message) throws IOException {
        synchronized (lock) {  // Ensure thread safety
            String timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
            writer.write(timeStamp + " - " + message + "\n");
            writer.flush();
        }
    }

    // Closes the log file (thread-safe)
    public void close() throws IOException {
        synchronized (lock) {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
