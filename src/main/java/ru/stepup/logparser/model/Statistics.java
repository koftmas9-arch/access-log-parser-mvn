package ru.stepup.logparser.model;

import lombok.Getter;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {

    int totalTraffic;
    int browserVisitsCount;
    Map<String, Integer> visitsPerUniqueIps;
    LocalDateTime minTime;
    LocalDateTime maxTime;
    @Getter
    Set<String> existingPages;
    @Getter
    Set<String> nonExistingPages;
    Map<String, Integer> osStatistics;
    Map<String, Integer> browserStatistics;
    int errorRequestCount;
    Map<Integer, Integer> visitsPerSecond;
    Map<LocalDateTime, Integer> visitsPerTime;
    @Getter
    HashSet<String> domains;

    public Statistics() {
        totalTraffic = 0;
        browserVisitsCount = 0;
        minTime = LocalDateTime.MAX;
        maxTime = LocalDateTime.MIN;
        existingPages = new HashSet<>();
        nonExistingPages = new HashSet<>();
        osStatistics = new HashMap<>();
        browserStatistics = new HashMap<>();
        errorRequestCount = 0;
        visitsPerUniqueIps = new HashMap<>();
        visitsPerSecond = new HashMap<>();
        visitsPerTime = new HashMap<>();
        domains = new HashSet<>();
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        if (entry.getTime().isBefore(minTime)) minTime = entry.getTime();
        if (entry.getTime().isAfter(maxTime)) maxTime = entry.getTime();
        if (entry.getResponseCode() == 200) existingPages.add(entry.getPath());
        if (entry.getResponseCode() == 404) nonExistingPages.add(entry.getPath());
        osStatistics.put(entry.getUserAgent().getOS(), osStatistics.getOrDefault(entry.getUserAgent().getOS(), 0) + 1);
        browserStatistics.put(entry.getUserAgent().getBrowser(), browserStatistics.getOrDefault(entry.getUserAgent().getBrowser(), 0) + 1);
        if (!entry.getUserAgent().isBot()) {
            browserVisitsCount++;
            visitsPerUniqueIps.put(entry.getIpAddr(), visitsPerUniqueIps.getOrDefault(entry.getIpAddr(), 0) + 1);
            visitsPerTime.put(entry.getTime(), visitsPerTime.getOrDefault(entry.getTime(), 0) + 1);
        }
        if (entry.getResponseCode() != 200) errorRequestCount++;
        var domain = extractDomain(entry.getReferer());
        if (domain != null) domains.add(domain);
    }

    private static String extractDomain(String url) {
        try {
            URI uri = new URI(url);

            return uri.getHost();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getVisitsPerSecond(Integer second) {
        for (Map.Entry<LocalDateTime, Integer> entry : visitsPerTime.entrySet()) {
            if (visitsPerSecond.getOrDefault(entry.getKey().getSecond(), 0) == 0
                    || visitsPerSecond.getOrDefault(entry.getKey().getSecond(), 0) < entry.getValue()) {
                visitsPerSecond.put(entry.getKey().getSecond(), entry.getValue());
            }
        }
        return visitsPerSecond.getOrDefault(second, 0);
    }

    public int getTrafficPerHour() {
        return (int) (totalTraffic / Duration.between(minTime, maxTime).toHours());
    }

    public int getBrowserVisitsPerHour() {
        return (int) (browserVisitsCount / Duration.between(minTime, maxTime).toHours());
    }

    public int getErrorRequestPerHour() {
        return (int) (errorRequestCount / Duration.between(minTime, maxTime).toHours());
    }

    public int getVisitsPerUser() {
        return browserVisitsCount / visitsPerUniqueIps.size();
    }

    public int getMaxVisitsPerUser() {
        return browserVisitsCount / visitsPerUniqueIps
                .values()
                .stream()
                .sorted()
                .limit(1)
                .reduce(0, Integer::max);
    }

    public Map<String, Double> getOsStatistics() {
        var statistics = new HashMap<String, Double>();
        for (String os : osStatistics.keySet()) {
            statistics.put(os, (double) osStatistics.get(os) / osStatistics.values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .sum());
        }
        return statistics;
    }

    public Map<String, Double> getBrowserStatistics() {
        var statistics = new HashMap<String, Double>();
        for (String browser : browserStatistics.keySet()) {
            statistics.put(browser,
                    (double) browserStatistics.get(browser) / browserStatistics.values()
                            .stream()
                            .mapToInt(Integer::intValue)
                            .sum());
        }
        return statistics;
    }
}
