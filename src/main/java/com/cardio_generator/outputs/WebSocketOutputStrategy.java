package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;

    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        /*In DataStorage, we have the method "addPatientData(int patientId, double measurementValue, String recordType, long timestamp)",
        * which in this case we have to format the message into that types of parameters*/
        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);

        double measurementValue;
        try {
            // Blood Saturation is in %, so to parse it, we have to get rid of the & sign
            if (data.endsWith("%")) {
                String numericString = data.substring(0, data.length() - 1);
                measurementValue = Double.parseDouble(numericString);
            }
            else {
                measurementValue = Double.parseDouble(data);
            }
        } catch (Exception e) {
            System.err.println("Unable to parse to double " + data);
            return;
        }

        String message = String.format("%d,%s%n,%s,%d", patientId, measurementValue, label, timestamp);
        // Broadcast the message to all connected clients
        for (WebSocket conn : server.getConnections()) {
            conn.send(message);
        }
    }

    private static class SimpleWebSocketServer extends WebSocketServer {

        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
