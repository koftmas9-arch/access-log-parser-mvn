package ru.stepup.logparser.exception;

public class TooLongLineException extends RuntimeException {
    public TooLongLineException(String message) {
        super(message);
    }
}
