package ru.aston.rest_service.exception;

public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}