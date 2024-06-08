package com.alerts.decorator;

import com.alerts.Alert;

import java.util.Timer;
import java.util.TimerTask;

public class RepeatedAlertDecorator extends AlertDecorator {
    private int interval;
    private Timer timer;

    public RepeatedAlertDecorator(IAlert decoratedAlert, int interval) {
        super(decoratedAlert);
        this.interval = interval;
        this.timer = new Timer(true);
        scheduleRecheck();
    }

    private void scheduleRecheck() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Here you can re-check the condition and handle the alert again if necessary
                System.out.println("Rechecking alert: " + getDetails());
            }
        }, interval, interval);
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " [Repeated every " + interval / 1000 + " seconds]";
    }

}
