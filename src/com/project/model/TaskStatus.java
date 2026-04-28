package com.project.model;

public enum TaskStatus {
    PENDING("pending"),
    IN_PROGRESS(" in progress"),
    COMPLETED("completed"),
    CANCELED("cancled");

    private final String arabicName;

    TaskStatus(String arabicName) {
        this.arabicName = arabicName;
    }

    @Override
    public String toString() {
        return arabicName;
    }
}