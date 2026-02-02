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
        totalTraffic += entry.getDataSize();
        if (entry.getDatetime().isBefore(minTime)) minTime = entry.getDatetime();
        if (entry.getDatetime().isAfter(maxTime)) maxTime = entry.getDatetime();
    }

    public int getTrafficRate() {
        return (int) (totalTraffic / Duration.between(minTime, maxTime).toHours());
    }
}
