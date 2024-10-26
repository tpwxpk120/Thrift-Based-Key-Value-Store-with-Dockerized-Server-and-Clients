package server;

import gen_java.KeyValueStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValue implements KeyValueStore.Iface {
    private Map<String, String> store = new ConcurrentHashMap<>();
    private String serverName;

    // Constructor to set the server name
    public KeyValue(String serverName) {
        this.serverName = serverName;
    }

    // Utility method to check if the request is for this server
    private boolean isRequestForThisServer(String requestServerName) {
        return this.serverName.equals(requestServerName);
    }

    @Override
    public void put(String key, String value) {
        // Extract server name from the key (or you can pass it separately via a different method)
        String[] parts = key.split(":");
        if (parts.length != 2) {
            System.out.println("Invalid key format");
            return;
        }
        String requestServerName = parts[0];
        String actualKey = parts[1];

        if (!isRequestForThisServer(requestServerName)) {
            System.out.println("PUT operation rejected: Wrong server (" + requestServerName + ")");
            return;
        }

        store.put(actualKey, value);
        System.out.println("PUT operation on server '" + serverName + "': " + actualKey + " -> " + value);
    }

    @Override
    public String get(String key) {
        // Extract server name from the key
        String[] parts = key.split(":");
        if (parts.length != 2) {
            System.out.println("Invalid key format");
            return "Invalid key format";
        }
        String requestServerName = parts[0];
        String actualKey = parts[1];

        if (!isRequestForThisServer(requestServerName)) {
            System.out.println("GET operation rejected: Wrong server (" + requestServerName + ")");
            return "Wrong server";
        }

        String value = store.getOrDefault(actualKey, "Key not found");
        System.out.println("GET operation on server '" + serverName + "': " + actualKey + " -> " + value);
        return value;
    }

    @Override
    public void delete(String key) {
        // Extract server name from the key
        String[] parts = key.split(":");
        if (parts.length != 2) {
            System.out.println("Invalid key format");
            return;
        }
        String requestServerName = parts[0];
        String actualKey = parts[1];

        if (!isRequestForThisServer(requestServerName)) {
            System.out.println("DELETE operation rejected: Wrong server (" + requestServerName + ")");
            return;
        }

        store.remove(actualKey);
        System.out.println("DELETE operation on server '" + serverName + "': " + actualKey);
    }
}
