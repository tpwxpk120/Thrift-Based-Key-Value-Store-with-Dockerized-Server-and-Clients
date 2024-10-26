package server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import gen_java.KeyValueStore;
import java.io.IOException;

public class ServerApp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ServerApp <port> <serverName>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String serverName = args[1];
        ServerLogger logger = null;  // Initialize the logger

        try {
            // Initialize logger to write logs to "server.log"
            logger = new ServerLogger("server.log");
            logger.log("Server '" + serverName + "' starting on port: " + port);

            // Set up Thrift key-value store handler and processor with server name
            KeyValueStore.Iface keyValueHandler = new KeyValue(serverName);  // Pass server name
            KeyValueStore.Processor<KeyValueStore.Iface> processor = new KeyValueStore.Processor<>(keyValueHandler);

            // Set up the server transport and server
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Thrift server '" + serverName + "' started on port " + port);
            logger.log("Thrift server '" + serverName + "' started on port " + port);

            // Start the server
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
            if (logger != null) {
                try {
                    logger.log("Error: " + e.getMessage());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } finally {
            // Ensure logger is closed properly
            if (logger != null) {
                try {
                    logger.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
