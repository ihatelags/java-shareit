package ru.practicum.shareit.exception;

public class BadStatusException extends RuntimeException {
    public BadStatusException(String message) {
        super(message);
    }
}
