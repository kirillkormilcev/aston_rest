package ru.aston.rest_service.exception;

public class DataBaseStatementException extends RuntimeException {
    public DataBaseStatementException(String message) {
        super(message);
    }
}