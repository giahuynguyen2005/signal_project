package data_management;

import com.alerts.AlertGenerator;
import com.alerts.strategy.BloodOxygenStrategy;
import com.alerts.strategy.BloodPressureStrategy;
import com.alerts.strategy.ECGAlertStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Week7Test {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void newSystemTest() {
        DataStorage storage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        storage.addPatientData(1, 89, "SystolicPressure", 1L);
        storage.addPatientData(1, 101, "ECG", 1L);
        storage.addPatientData(1, 100, "Saturation", 1L);
        storage.addPatientData(1, 100, "Saturation", 2L);
        storage.addPatientData(1, 95, "Saturation", 6000L);
        storage.addPatientData(1, 100, "Saturation", 6001L);
        storage.addPatientData(1, 100, "Saturation", 6002L);

        List<PatientRecord> records = storage.getRecords(1, 1L,3L);

        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.setAlertStrategy(new BloodPressureStrategy());
            alertGenerator.evaluateData(patient);

            alertGenerator.setAlertStrategy(new BloodOxygenStrategy());
            alertGenerator.evaluateData(patient);

            alertGenerator.setAlertStrategy(new ECGAlertStrategy());
            alertGenerator.evaluateData(patient);
        }

        Assertions.assertEquals(("Alert triggered: Blood Pressure Anomalies for patient ID: 1" +
                        "\nAlert triggered: Significant Changes To Blood Oxygen Level for patient ID: 1" +
                        "\nAlert triggered: Irregular Heart Rates And Rhythms for patient ID: 1").replace("\n", System.lineSeparator()),
                outContent.toString().trim());
    }
}
