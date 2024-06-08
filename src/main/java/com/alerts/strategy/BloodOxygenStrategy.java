package com.alerts.strategy;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodOxygenStrategy implements AlertStrategy {
    @Override
    public void checkAlert(Patient patient, AlertGenerator alertGenerator) {
        /*Extract all records*/
        List<PatientRecord> records = patient.getAllPatientRecords();

        /*Extract all records with blood saturation*/
        List<PatientRecord> bloodSaturationRecords = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation")) {
                bloodSaturationRecords.add(record);
            }
        }

        if (bloodSaturationRecords.isEmpty()) {
            return;
        }

        /*check if blood saturation in any record drops below 92*/
        for (PatientRecord record : bloodSaturationRecords) {
            if (record.getMeasurementValue() < 92) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Blood Saturation Drops Below 92", record.getTimestamp());
                alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Significant Changes To Blood Oxygen Level", record.getTimestamp());
            }
        }

        /*check if blood saturation drops 5% or more within 10 mins time interval*/
        long interval = 600000; //600000 milisecs in 10 mins
        long startTime = records.get(0).getTimestamp();
        long endTime = records.get(records.size() - 1).getTimestamp();

        for (long currentTime = startTime; currentTime < endTime; currentTime += interval) {
            long intervalStart = currentTime;
            long intervalEnd = currentTime + interval;

            ArrayList<PatientRecord> tempList = new ArrayList<>();
            for (PatientRecord record : records) {
                if (record.getTimestamp() >= intervalStart && record.getTimestamp() <= intervalEnd) {
                    tempList.add(record);
                }
            }

            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (PatientRecord record : tempList) {
                min = Math.min(min, record.getMeasurementValue());
                max = Math.max(max, record.getMeasurementValue());
            }

            if (Math.abs(max - min) >= 5) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Saturation Drops Rapidly", intervalStart);
                alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Significant Changes To Blood Oxygen Level", intervalStart);
            }
        }
    }
}
