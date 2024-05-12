package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.MyDataReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.Patient;
import com.alerts.AlertGenerator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

class DataStorageTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Test
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader
        MyDataReader reader = new MyDataReader("test");

        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testforIncreasingDecreasingTrend() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "SystolicPressure", 1L);
        storage.addPatientData(1, 120.0, "SystolicPressure", 2L);
        storage.addPatientData(1, 140.0, "SystolicPressure", 3L);

        List<PatientRecord> records = storage.getRecords(1, 1L,3L);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertEquals("Alert Triggered:", outContent.toString().trim());
    }

    @Test
    void testforIncreasingDecreasingTrendIfNotEnoughData() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "SystolicPressure", 1L);
        storage.addPatientData(1, 120.0, "SystolicPressure", 2L);

        List<PatientRecord> records = storage.getRecords(1, 1L,2L);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertEquals("", outContent.toString().trim());

    }

    @Test
    void testforCriticalThreshold() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 190.0, "SystolicPressure", 1L);
        storage.addPatientData(1, 50.0, "DiastolicPressure", 2L);

        List<PatientRecord> records = storage.getRecords(1, 1L,2L);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertEquals(("Alert Triggered:\nAlert Triggered:").replace("\n", System.lineSeparator()), outContent.toString().trim());

    }

    @Test
    void testForLowSaturation() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 92.0, "Saturation", 1L);
        storage.addPatientData(1, 90.0, "Saturation", 2L);

        List<PatientRecord> records = storage.getRecords(1, 1L,2L);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertEquals(("Alert Triggered:").replace("\n", System.lineSeparator()), outContent.toString().trim());

    }

    @Test
    void testForRapidDropAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100, "Saturation", 1L);
        storage.addPatientData(1, 100, "Saturation", 2L);
        storage.addPatientData(1, 95, "Saturation", 6000L);
        storage.addPatientData(1, 100, "Saturation", 7000L);
        storage.addPatientData(1, 100, "Saturation", 8000L);

        List<PatientRecord> records = storage.getRecords(1, 1L,2L);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertEquals(("Alert Triggered:").replace("\n", System.lineSeparator()), outContent.toString().trim());

    }

    @Test
    void testForHypotensiveHypoxemiaAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 89, "SystolicPressure", 1L);
        storage.addPatientData(1, 91, "Saturation", 2L);

        List<PatientRecord> records = storage.getRecords(1, 1L,2L);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertEquals(("Alert Triggered:\nAlert Triggered:").replace("\n", System.lineSeparator()), outContent.toString().trim());

    }

    @Test
    void testForAbnormalHeartRateAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 49, "ECG", 1L);
        storage.addPatientData(1, 101, "ECG", 2L);

        List<PatientRecord> records = storage.getRecords(1, 1L,2L);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertEquals(("Alert Triggered:\nAlert Triggered:").replace("\n", System.lineSeparator()), outContent.toString().trim());

    }
}
