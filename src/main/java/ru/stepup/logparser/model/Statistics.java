package ru.stepup.logparser.model;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {

    int totalTraffic;
    LocalDateTime minTime;
    LocalDateTime maxTime;
    @Getter
    Set<String> existingPages;
    @Getter
    Set<String> nonExistingPages;
    Map<String, Integer> osStatistics;
    Map<String, Integer> browserStatistics;

    public Statistics() {
        totalTraffic = 0;
        minTime = LocalDateTime.MAX;
        maxTime = LocalDateTime.MIN;
        existingPages = new HashSet<>();
        nonExistingPages = new HashSet<>();
        osStatistics = new HashMap<>();
        browserStatistics = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        if (entry.getTime().isBefore(minTime)) minTime = entry.getTime();
        if (entry.getTime().isAfter(maxTime)) maxTime = entry.getTime();
        if (entry.getResponseCode() == 200) existingPages.add(entry.getPath());
        if (entry.getResponseCode() == 404) nonExistingPages.add(entry.getPath());
        osStatistics.put(entry.getUserAgent().getOS(), osStatistics.getOrDefault(entry.getUserAgent().getOS(), 0) + 1);
        browserStatistics.put(entry.getUserAgent().getBrowser(), browserStatistics.getOrDefault(entry.getUserAgent().getBrowser(), 0) + 1);
    }

    public int getTrafficRate() {
        return (int) (totalTraffic / Duration.between(minTime, maxTime).toHours());
    }

    public Map<String, Double> getOsStatistics() {
        var statistics = new HashMap<String, Double>();
        for (String os : osStatistics.keySet()) {
            statistics.put(os, (double) osStatistics.get(os) / osStatistics.values().stream().mapToInt(Integer::intValue).sum());
        }
        return statistics;
    }

    public Map<String, Double> getBrowserStatistics() {
        var statistics = new HashMap<String, Double>();
        for (String browser : browserStatistics.keySet()) {
            statistics.put(browser,
                    (double) browserStatistics.get(browser) / browserStatistics.values().stream().mapToInt(Integer::intValue).sum());
        }
        return statistics;
    }
}
