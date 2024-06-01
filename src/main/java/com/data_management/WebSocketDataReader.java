package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketDataReader implements DataReader{

    private CustomWebSocketClient webSocketClient;
    private DataStorage dataStorage;

    public WebSocketDataReader(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public void connect(String uri) throws IOException {
        try {
            webSocketClient = new CustomWebSocketClient(new URI(uri), dataStorage);
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI", e);
        }
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
