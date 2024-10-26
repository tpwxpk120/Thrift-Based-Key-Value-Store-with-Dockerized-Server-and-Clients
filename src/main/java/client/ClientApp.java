package client;

import org.apache.thrift.TException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import gen_java.KeyValueStore;

/**
 * ClientApp is a simple client that communicates with a Thrift server.
 * It performs automatic actions and then accepts user commands for manual input.
 */
public class ClientApp extends AbstractClient {

    public ClientApp(String serverName) {
        super(serverName);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java ClientApp <hostname> <port> <serverName>");
            return;
        }

        String hostname = args[0];
        int port;
        String serverName = args[2];

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer.");
            return;
        }

        try (ClientLogger logger = new ClientLogger("client.log")) {
            ClientApp clientApp = new ClientApp(serverName);
            clientApp.initializeClient(hostname, port);  // Initialize the Thrift client
            clientApp.performAutomaticActions(logger);   // Perform automatic actions
            clientApp.enterManualMode(logger);           // Switch to manual mode for user input
        } catch (IOException | TException e) {
            System.err.println("An error occurred while communicating with the server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    // Automatically performs PUT, GET, DELETE operations 5 times.
    private void performAutomaticActions(ClientLogger logger) throws TException, IOException {
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            String value = "value" + i;

            sendAndLogRequest(logger, "PUT " + key + " " + value);
            sendAndLogRequest(logger, "GET " + key);
            sendAndLogRequest(logger, "DELETE " + key);
        }
        System.out.println("Completed 15 automatic operations.");
        logger.log("Completed 15 automatic operations.");
    }

    // Switches to manual command input from the user.
    private void enterManualMode(ClientLogger logger) throws TException, IOException {
        System.out.println("\nEntering manual mode. You can now enter commands:");
        System.out.println("Use 'PUT <key> <value>', 'GET <key>', 'DELETE <key>', or 'CLOSE' to exit.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nEnter command: ");
                if (!scanner.hasNextLine()) {
                    System.out.println("No input detected. Exiting manual mode.");
                    logger.log("Manual mode exited due to no input.");
                    break;
                }

                String command = scanner.nextLine().trim();
                if (command.equalsIgnoreCase("CLOSE")) {
                    logger.log("Client connection closing.");
                    System.out.println("Closing client connection.");
                    break;
                }

                try {
                    if (isValidCommand(command)) {
                        sendAndLogRequest(logger, command);
                    } else {
                        System.out.println("Invalid command format. Use 'PUT <key> <value>', 'GET <key>', 'DELETE <key>'.");
                        logger.log("Invalid command: " + command);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    logger.log("Error: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Input stream closed. Exiting manual mode.");
            logger.log("Input stream closed. Manual mode exited.");
        }
    }

    // Sends a request to the server and logs the request and response.
    private void sendAndLogRequest(ClientLogger logger, String request) throws TException, IOException {
        String[] parts = request.split("\\s+");
        String action = parts[0].toUpperCase();

        try {
            switch (action) {
                case "PUT":
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("PUT command requires a key and a value.");
                    }
                    put(parts[1], parts[2]);
                    logger.log("Request: PUT " + parts[1] + " " + parts[2]);
                    System.out.println("PUT operation completed.");
                    break;

                case "GET":
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("GET command requires a key.");
                    }
                    String value = get(parts[1]);
                    System.out.println("GET response: " + value);
                    logger.log("Request: GET " + parts[1] + " | Response: " + value);
                    break;

                case "DELETE":
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("DELETE command requires a key.");
                    }
                    delete(parts[1]);
                    logger.log("Request: DELETE " + parts[1]);
                    System.out.println("DELETE operation completed.");
                    break;

                default:
                    throw new IllegalArgumentException("Unknown command: " + action);
            }
        } catch (IllegalArgumentException e) {
            logger.log("Error with command: " + request + " | Error: " + e.getMessage());
            throw e;
        }
    }

    // Check if the command has a valid format for PUT, GET, or DELETE.
    private boolean isValidCommand(String command) {
        String[] parts = command.split("\\s+");
        String action = parts[0].toUpperCase();

        if (action.equals("PUT") && parts.length == 3) {
            return true;
        } else if ((action.equals("GET") || action.equals("DELETE")) && parts.length == 2) {
            return true;
        }
        return false;
    }
}
