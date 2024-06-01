package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Connects to a WebSocket server to read real-time data.
     *
     * @param uri the URI of the WebSocket server
     * @throws IOException if there is an error connecting to the WebSocket server
     */
    void connect(String uri) throws IOException;

    /**
     * Handles incoming messages from the WebSocket server.
     *
     * @param message the message received from the WebSocket server
     */
    void onMessage(String message);

    /**
     * Disconnects from the WebSocket server.
     */
    void disconnect();
}
