package client;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import gen_java.KeyValueStore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for a Thrift-based client. Provides basic operations for PUT, GET, and DELETE.
 * Enhances error handling, logging, and input validation to improve robustness.
 */
public abstract class AbstractClient {
    private static final Logger logger = Logger.getLogger(AbstractClient.class.getName());

    protected KeyValueStore.Client thriftClient;
    private TTransport transport;
    private String serverName;

    // Constructor to set server name
    public AbstractClient(String serverName) {
        if (serverName == null || serverName.isEmpty()) {
            throw new IllegalArgumentException("Server name cannot be null or empty");
        }
        this.serverName = serverName;
    }

    // Initialize the Thrift client connection with logging and error handling
    public void initializeClient(String host, int port) {
        try {
            transport = new TSocket(host, port);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            thriftClient = new KeyValueStore.Client(protocol);
            logger.log(Level.INFO, "Connected to Thrift server at {0}:{1}", new Object[]{host, port});
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to Thrift server at {0}:{1}: {2}", new Object[]{host, port, e.getMessage()});
            throw new RuntimeException("Failed to initialize client", e);
        }
    }

    // Prepend server name to the key
    private String prependServerName(String key) {
        if (key == null || key.isEmpty()) {
            logger.log(Level.WARNING, "Key cannot be null or empty");
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        return serverName + ":" + key;
    }

    // Perform a PUT request with logging and validation
    public void put(String key, String value) {
        if (value == null || value.isEmpty()) {
            logger.log(Level.WARNING, "Value cannot be null or empty");
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        try {
            String fullKey = prependServerName(key);
            thriftClient.put(fullKey, value);
            logger.log(Level.INFO, "PUT request: {0} -> {1}", new Object[]{fullKey, value});
        } catch (TException e) {
            logger.log(Level.SEVERE, "Failed to perform PUT operation: {0}", e.getMessage());
            throw new RuntimeException("PUT operation failed", e);
        }
    }

    // Perform a GET request with logging and validation
    public String get(String key) {
        try {
            String fullKey = prependServerName(key);
            String value = thriftClient.get(fullKey);
            logger.log(Level.INFO, "GET request: {0} -> {1}", new Object[]{fullKey, value});
            return value;
        } catch (TException e) {
            logger.log(Level.SEVERE, "Failed to perform GET operation: {0}", e.getMessage());
            throw new RuntimeException("GET operation failed", e);
        }
    }

    // Perform a DELETE request with logging and validation
    public void delete(String key) {
        try {
            String fullKey = prependServerName(key);
            thriftClient.delete(fullKey);
            logger.log(Level.INFO, "DELETE request: {0}", fullKey);
        } catch (TException e) {
            logger.log(Level.SEVERE, "Failed to perform DELETE operation: {0}", e.getMessage());
            throw new RuntimeException("DELETE operation failed", e);
        }
    }

    // Close the Thrift client connection with logging and error handling
    public void close() {
        if (transport != null && transport.isOpen()) {
            try {
                transport.close();
                logger.log(Level.INFO, "Thrift client connection closed");
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to close Thrift client connection: {0}", e.getMessage());
            }
        }
    }
}
