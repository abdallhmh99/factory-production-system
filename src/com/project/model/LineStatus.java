package com.project.model;

public enum LineStatus {
    ACTIVE("active"),
    STOPPED("stopped"),
    MAINTENANCE("MAINTENANCE");

    private final String arabicName;

    LineStatus(String arabicName) {
        this.arabicName = arabicName;
    }

    @Override
    public String toString() {
        return arabicName;
    }
}