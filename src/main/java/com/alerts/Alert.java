package com.alerts;

import com.alerts.decorator.IAlert;

// Represents an alert
public class Alert implements IAlert {
    private String patientId;
    private String condition;
    private long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getDetails() {
        return "Alert for patient " + patientId + " due to " + condition + " at " + timestamp;
    }

}
