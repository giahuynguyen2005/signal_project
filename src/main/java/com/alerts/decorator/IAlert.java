package com.alerts.decorator;

public interface IAlert {
    String getPatientId();
    String getCondition();
    long getTimestamp();
    String getDetails();
}
