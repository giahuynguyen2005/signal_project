package com.alerts.decorator;

public class PriorityAlertDecorator extends AlertDecorator {
    private String priorityLevel;

    public PriorityAlertDecorator(IAlert decoratedAlert, String priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " [Priority: " + priorityLevel + "]";
    }

}
