package com.phonecompany.models;

import java.time.LocalDateTime;

public class CallRecord {
    private final String phoneNumber;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public CallRecord(String phoneNumber, LocalDateTime startTime, LocalDateTime endTime) {
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}