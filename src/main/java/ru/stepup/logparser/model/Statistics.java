package ru.stepup.logparser.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {

    int totalTraffic;
    LocalDateTime minTime;
    LocalDateTime maxTime;

    public Statistics() {
        totalTraffic = 0;
        minTime = LocalDateTime.MAX;
        maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        if (entry.getTime().isBefore(minTime)) minTime = entry.getTime();
        if (entry.getTime().isAfter(maxTime)) maxTime = entry.getTime();
    }

    public int getTrafficRate() {
        return (int) (totalTraffic / Duration.between(minTime, maxTime).toHours());
    }
}
