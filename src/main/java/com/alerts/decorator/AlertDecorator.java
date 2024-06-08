package com.alerts.decorator;

public abstract class AlertDecorator implements IAlert {
    protected IAlert decoratedIAlert;

    public AlertDecorator(IAlert decoratedIAlert) {
        this.decoratedIAlert = decoratedIAlert;
    }

    @Override
    public String getPatientId() {
        return decoratedIAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return decoratedIAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return decoratedIAlert.getTimestamp();
    }

    @Override
    public String getDetails() {
        return decoratedIAlert.getDetails();
    }
}
