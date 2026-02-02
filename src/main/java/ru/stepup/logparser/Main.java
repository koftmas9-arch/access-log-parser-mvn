package ru.stepup.logparser;

import ru.stepup.logparser.exception.TooLongLineException;
import ru.stepup.logparser.model.LogEntry;
import ru.stepup.logparser.model.Statistics;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        String path = "C:\\Users\\AAKHramova\\Khramova\\AT\\access-log-parser-mvn\\src\\main\\resources\\access.log";
        System.out.println("Считывается файл по пути:\n" + path);
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            int lineCount = 0;
            Statistics statistics = new Statistics();
            while ((line = reader.readLine()) != null) {
                lineCount++;
                int length = line.length();
                if (length > 1024) {
                    throw new TooLongLineException("Длина строки больше 1024");
                }
                LogEntry logEntry = new LogEntry(line);
                statistics.addEntry(logEntry);
            }
            System.out.println("Общее количество строк в файле: " + lineCount);
            System.out.printf("Средний объём трафика сайта за час: %d байт%n", statistics.getTrafficRate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}