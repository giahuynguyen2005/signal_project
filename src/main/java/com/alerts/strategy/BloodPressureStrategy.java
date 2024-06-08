package com.alerts.strategy;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public void checkAlert(Patient patient, AlertGenerator alertGenerator) {
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

                    alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Blood Pressure Anomalies", systolicBloodPressureRecord.get(i).getTimestamp());
                }
            }
        }

        /*Check diastolic increasing/decreasing trend*/
        if (diastolicBloodPressureRecord.size()>=3) {
            for (int i = 0; i < diastolicBloodPressureRecord.size() - 2; i++) {
                if ((Math.abs(diastolicBloodPressureRecord.get(i).getMeasurementValue() - diastolicBloodPressureRecord.get(i + 1).getMeasurementValue())) > 10
                        && (Math.abs(diastolicBloodPressureRecord.get(i + 1).getMeasurementValue() - diastolicBloodPressureRecord.get(i + 2).getMeasurementValue()) > 10)) {
                    alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Blood Pressure Anomalies", diastolicBloodPressureRecord.get(i).getTimestamp());
                }
            }
        }

        /*Check systolic exceeding/dropping*/
        for (PatientRecord record: systolicBloodPressureRecord) {
            if (record.getMeasurementValue() > 180) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Systolic Pressure Exceeds 180", record.getTimestamp());
                alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Blood Pressure Anomalies", record.getTimestamp());
            }
            if (record.getMeasurementValue() < 90) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Systolic Pressure Drops Below 90", record.getTimestamp());
                alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Blood Pressure Anomalies", record.getTimestamp());
            }
        }

        /*Check diastolic exceeding/dropping*/
        for (PatientRecord record: diastolicBloodPressureRecord) {
            if (record.getMeasurementValue() > 120) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Diastolic Pressure Exceeds 120", record.getTimestamp());
                alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Blood Pressure Anomalies", record.getTimestamp());
            }
            if (record.getMeasurementValue() < 60) {
                Alert alert = new Alert(Integer.toString(patient.getPatientId()), "Diastolic Pressure Drops Below 60", record.getTimestamp());
                alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Blood Pressure Anomalies", record.getTimestamp());
            }
        }
    }
}
