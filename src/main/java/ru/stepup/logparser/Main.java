package ru.stepup.logparser;

import ru.stepup.logparser.exception.TooLongLineException;
import ru.stepup.logparser.model.LogEntry;
import ru.stepup.logparser.model.Statistics;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        String path = "src/main/resources/access.log";
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
            System.out.printf("Средний объём трафика сайта за час: %d байт%n", statistics.getTrafficPerHour());
            System.out.println("Существующих адресов страниц в файле: " + statistics.getExistingPages().size());
            System.out.println("Несуществующих адресов страниц в файле: " + statistics.getNonExistingPages().size());
            System.out.println("Доли операционных систем: " + statistics.getOsStatistics());
            System.out.println("Сумма всех долей операционных систем: " + statistics.getOsStatistics().values()
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .sum());
            System.out.println("Доли браузеров: " + statistics.getBrowserStatistics());
            System.out.println("Сумма всех долей браузеров: " + statistics.getBrowserStatistics().values()
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .sum());
            System.out.println("Среднее количество посещений сайта за час: " + statistics.getBrowserVisitsPerHour());
            System.out.println("Среднее количество ошибочных запросов за час: " + statistics.getErrorRequestPerHour());
            System.out.println("Средняя посещаемость одним пользователем: " + statistics.getVisitsPerUser());
            System.out.println("Максимальная посещаемость одним пользователем: " + statistics.getMaxVisitsPerUser());
            System.out.println("Пиковой посещаемость сайта в секунду 5: " + statistics.getVisitsPerSecond(5));
            System.out.println("Пиковой посещаемость сайта в секунду 17: " + statistics.getVisitsPerSecond(17));
            System.out.println("Домены:\n" + String.join("\n", statistics.getDomains()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}