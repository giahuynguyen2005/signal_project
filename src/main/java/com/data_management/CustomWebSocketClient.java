package com.data_management;

import com.cardio_generator.HealthDataSimulator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class CustomWebSocketClient extends WebSocketClient {

    private DataStorage dataStorage;

    public CustomWebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to WebSocket server");
    }

    @Override
    public void onMessage(String message) {
        try {
            parseMessage(message);
        } catch (Exception e) {
            System.err.println("Error Parsing or Storing: " + e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server: " + reason);
    }

    @Override
    public void onError(Exception e) {
        System.err.println("WebSocket error: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * This method will handle parsing and then storing the records received
     * @param message input the message to be processed
     */
    public void parseMessage(String message) throws Exception {
        String[] split = message.split(",");
        if (split.length != 4) {
            throw new Exception("Invalid format");
        }
        try {
            int patientId = Integer.parseInt(split[0]);
            double measurementValue = Double.parseDouble(split[1]);
            String recordType = split[2];
            long timestamp = Long.parseLong(split[3]);
            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
//            System.out.println("Successful");
        } catch (Exception e) {
            throw new Exception("Error parsing: " + e);
        }
    }

    public static void main(String[] args) throws Exception {
        // Use the correct URI
        URI uri = new URI("ws://localhost:8080");
        DataStorage dataStorage = new DataStorage();
        HealthDataSimulator server = new HealthDataSimulator();
        CustomWebSocketClient client = new CustomWebSocketClient(uri, dataStorage);
        client.connect();
    }

}
