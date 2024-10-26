package server;

/**
 * Response class encapsulates a response message sent from the server to the client.
 */
public class Response {
    private String message;  // The message to be returned to the client

    // Constructor to initialize the response with a message
    public Response(String message) {
        this.message = message;
    }

    // Returns the response message
    public String getMessage() {
        return message;
    }

    // Converts the response to a string format
    @Override
    public String toString() {
        return message;
    }


}
