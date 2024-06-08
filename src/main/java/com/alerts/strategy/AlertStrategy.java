package com.alerts.strategy;

import com.alerts.AlertGenerator;
import com.alerts.factory.AlertFactory;
import com.data_management.Patient;

public interface AlertStrategy {
    void checkAlert(Patient patient, AlertGenerator alertGenerator);
}
