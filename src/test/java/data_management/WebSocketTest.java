package data_management;

import com.alerts.AlertGenerator;
import com.cardio_generator.HealthDataSimulator;
import com.data_management.CustomWebSocketClient;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import static javax.management.Query.times;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class WebSocketTest {

    private CustomWebSocketClient client;
    private static DataStorage dataStorage;
    private HealthDataSimulator server;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        dataStorage = new DataStorage();
        client = new CustomWebSocketClient(new URI("ws://localhost:8080"), dataStorage);

    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    //Test if the Client connects to the Server
    @Test
    public void connectionTest() {
        server = new HealthDataSimulator();
        Assertions.assertDoesNotThrow(()->client.connect());
    }

    //Test what happens when the message is wrongly formatted
    @Test
    public void testParseMessageInvalidFormat() {
        String invalidMessage = "invalid_message";
        Exception exception = assertThrows(Exception.class, () -> client.parseMessage(invalidMessage));
        Assertions.assertEquals("Invalid format", exception.getMessage());
    }

    //Test if the CustomWebSocketClient integrates with DataStorage
    @Test
    public void testIntegration() {
        String validData1 = "1,92.0,Saturation,1";
        String validData2 = "1,90.0,Saturation,2";
        client.onMessage(validData1);
        client.onMessage(validData2);

        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);
        for (Patient patient : dataStorage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
        Assertions.assertEquals("Alert Triggered:", outContent.toString().trim());
    }

}
