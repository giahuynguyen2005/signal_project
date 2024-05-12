package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
        checkBloodPressure(patient);
        checkBloodSaturation(patient);
        checkHypotensiveHypoxemia(patient);
        checkECG(patient);
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff

        // log the alert information
        System.out.println("Alert Triggered:");
//        System.out.println("Patient ID: " + alert.getPatientId());
//        System.out.println("Condition: " + alert.getCondition());
//        System.out.println("Timestamp: " + alert.getTimestamp());
//
//        // notify staff members (example: print to console)
//        System.out.println("Notify medical staff about the alert...");
//        System.out.println();

    }

    private void checkBloodPressure(Patient patient) {
        /*Extract all records*/
        List<PatientRecord> records = patient.getAllPatientRecords();

        /*Extract individual records for systolic & diastolic*/
        List<PatientRecord> systolicBloodPressureRecord = new ArrayList<>();
        List<PatientRecord> diastolicBloodPressureRecord = new ArrayList<>();

        for (PatientRecord record: records) {
            if (record.getRecordType().equals("SystolicPressure")) {
                systolicBloodPressureRecord.add(record);
            }
        }

        for (PatientRecord record: records) {
            if (record.getRecordType().equals("DiastolicPressure")) {
                diastolicBloodPressureRecord.add(record);
            }
        }

        /*Check systolic increasing/decreasing trend*/
        if (systolicBloodPressureRecord.size()>=3) {
            for (int i = 0; i < systolicBloodPressureRecord.size() - 2; i++) {
                if ((Math.abs(systolicBloodPressureRecord.get(i).getMeasurementValue() - systolicBloodPressureRecord.get(i + 1).getMeasurementValue())) > 10
                        && (Math.abs(systolicBloodPressureRecord.get(i + 1).getMeasurementValue() - systolicBloodPressureRecord.get(i + 2).getMeasurementValue()) > 10)) {
                    Alert systolicPressureAlert = new Alert(Integer.toString(patient.getPatientId()), "Blood Pressure Increasing/Decreasing", systolicBloodPressureRecord.get(i).getTimestamp());
                    triggerAlert(systolicPressureAlert);
                }
            }
        }

        /*Check diastolic increasing/decreasing trend*/
        if (diastolicBloodPressureRecord.size()>=3) {
            for (int i = 0; i < diastolicBloodPressureRecord.size() - 2; i++) {
                if ((Math.abs(diastolicBloodPressureRecord.get(i).getMeasurementValue() - diastolicBloodPressureRecord.get(i + 1).getMeasurementValue())) > 10
                        && (Math.abs(diastolicBloodPressureRecord.get(i + 1).getMeasurementValue() - diastolicBloodPressureRecord.get(i + 2).getMeasurementValue()) > 10)) {
                    Alert diastolicPressureAlert = new Alert(Integer.toString(patient.getPatientId()), "Blood Pressure Increasing/Decreasing", diastolicBloodPressureRecord.get(i).getTimestamp());
                    triggerAlert(diastolicPressureAlert);
                }
            }
        }

        /*Check systolic exceeding/dropping*/
        for (PatientRecord record: systolicBloodPressureRecord) {
            if (record.getMeasurementValue() > 180) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Systolic Pressure Exceeds 180", record.getTimestamp());
                triggerAlert(alert);
            }
            if (record.getMeasurementValue() < 90) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Systolic Pressure Drops Below 90", record.getTimestamp());
                triggerAlert(alert);
            }
        }

        /*Check diastolic exceeding/dropping*/
        for (PatientRecord record: diastolicBloodPressureRecord) {
            if (record.getMeasurementValue() > 120) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Diastolic Pressure Exceeds 120", record.getTimestamp());
                triggerAlert(alert);
            }
            if (record.getMeasurementValue() < 60) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Diastolic Pressure Drops Below 60", record.getTimestamp());
                triggerAlert(alert);
            }
        }
    }


    private void checkBloodSaturation(Patient patient) {
        /*Extract all records*/
        List<PatientRecord> records = patient.getAllPatientRecords();

        /*Extract all records with blood saturation*/
        List<PatientRecord> bloodSaturationRecords = new ArrayList<>();

        for (PatientRecord record: records) {
            if (record.getRecordType().equals("Saturation")) {
                bloodSaturationRecords.add(record);
            }
        }

        if (bloodSaturationRecords.isEmpty()) {
            return;
        }

        /*check if blood saturation in any record drops below 92*/
        for (PatientRecord record: bloodSaturationRecords) {
            if (record.getMeasurementValue()<92) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Blood Saturation Drops Below 92", record.getTimestamp());
                triggerAlert(alert);
            }
        }

        /*check if blood saturation drops 5% or more within 10 mins time interval*/
        long interval = 600000; //600000 milisecs in 10 mins
        long startTime = records.get(0).getTimestamp();
        long endTime = records.get(records.size()-1).getTimestamp();

        for (long currentTime = startTime; currentTime < endTime; currentTime+=interval) {
            long intervalStart = currentTime;
            long intervalEnd = currentTime + interval;

            ArrayList<PatientRecord> tempList = new ArrayList<>();
            for (PatientRecord record: records) {
                if(record.getTimestamp() >= intervalStart && record.getTimestamp() <= intervalEnd) {
                    tempList.add(record);
                }
            }

            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (PatientRecord record : tempList) {
                min = Math.min(min, record.getMeasurementValue());
                max = Math.max(max, record.getMeasurementValue());
            }

            if ((max - min) >= 5) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Saturation Drops Rapidly", intervalStart);
                triggerAlert(alert);
            }
        }
    }

    private void checkHypotensiveHypoxemia(Patient patient) {
        /*Extract all records*/
        List<PatientRecord> records = patient.getAllPatientRecords();

        /*check if in one timestamp, the conditions for an alert of both systolic pressure & saturation are met*/
        List<Long> timestamps = new ArrayList<>();
        for (PatientRecord record : records) {
            timestamps.add(record.getTimestamp());
        }
        for (long time : timestamps) {
            for (PatientRecord record : records) {
                if ( (record.getRecordType().equals("SystolicPressure") && record.getMeasurementValue() < 90)
                        && (record.getRecordType().equals("Saturation") && record.getMeasurementValue() < 92) ) {
                    Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Hypotensive Hypoxemia", time);
                    triggerAlert(alert);
                }
            }
        }
    }

    private void checkECG(Patient patient) {
        /*Extract all records*/
        List<PatientRecord> records = patient.getAllPatientRecords();

        List<PatientRecord> ECG = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("ECG")) {
                ECG.add(record);
            }
        }

        for (PatientRecord ecgRecord : ECG) {
            if (ecgRecord.getMeasurementValue()<50 || ecgRecord.getMeasurementValue()>100) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Abnormal Heart Rate Alert", ecgRecord.getTimestamp());
                triggerAlert(alert);
            }
        }

    }

}
