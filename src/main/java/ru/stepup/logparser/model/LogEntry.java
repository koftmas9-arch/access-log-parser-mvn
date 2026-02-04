package ru.stepup.logparser.model;

import lombok.Getter;
import ru.stepup.logparser.enums.HttpMethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class LogEntry {

    private static final Pattern LOG_PATTERN = Pattern.compile("^(\\S+) \\S+ \\S+ \\[(.+?)] \"(\\S+) (\\S+) \\S+\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\"$");

    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String log) {
        Matcher m = LOG_PATTERN.matcher(log);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid log line: " + log);
        }

        ipAddr = m.group(1);
        time = LocalDateTime.parse(m.group(2), DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH));
        method = HttpMethod.valueOf(m.group(3));
        path = m.group(4);
        responseCode = Integer.parseInt(m.group(5));
        responseSize = Integer.parseInt(m.group(6));
        referer = m.group(7);
        userAgent = new UserAgent(m.group(8));
    }

    @Getter
    public static class UserAgent {

        private final String OS;
        private final String browser;

        public UserAgent(String userAgent) {
            OS = parseOS(userAgent);
            browser = parseBrowser(userAgent);
        }

        private String parseOS(String ua) {
            ua = ua.toLowerCase();

            if (ua.contains("windows")) return "Windows";
            if (ua.contains("mac os")) return "macOS";
            if (ua.contains("android")) return "Android";
            if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
            if (ua.contains("linux")) return "Linux";

            return "Unknown OS";
        }

        private String parseBrowser(String ua) {
            ua = ua.toLowerCase();

            if (ua.contains("edg")) return "Edge";
            if (ua.contains("chrome")) return "Chrome";
            if (ua.contains("firefox")) return "Firefox";
            if (ua.contains("safari") && !ua.contains("chrome")) return "Safari";
            if (ua.contains("opera") || ua.contains("opr")) return "Opera";
            if (ua.contains("msie") || ua.contains("trident")) return "Internet Explorer";

            return "Unknown Browser";
        }

        @Override
        public String toString() {
            return "UserAgent{" + "OS='" + OS + '\'' + ", browser='" + browser + '\'' + '}';
        }
    }

    @Override
    public String toString() {
        return "LogEntry{" + "ip='" + ipAddr + '\'' + ", datetime=" + time + ", method=" + method + ", path='" + path + '\'' + ", responseCode='" + responseCode + '\'' + ", dataSize=" + responseSize + ", referer='" + referer + '\'' + ", userAgent=" + userAgent + '}';
    }
}
