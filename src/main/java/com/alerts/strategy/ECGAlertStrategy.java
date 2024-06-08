package com.alerts.strategy;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class ECGAlertStrategy implements AlertStrategy {
    @Override
    public void checkAlert(Patient patient, AlertGenerator alertGenerator) {
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
                alertGenerator.triggerAlert(Integer.toString(patient.getPatientId()), "Irregular Heart Rates And Rhythms", ecgRecord.getTimestamp());
            }
        }
    }
}
