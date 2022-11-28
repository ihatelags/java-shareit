package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BadStatusException;

import java.util.Arrays;

public enum StateStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static StateStatus fromString(String s) throws BadStatusException {
        return Arrays.stream(StateStatus.values())
                .filter(v -> v.name().equals(s))
                .findFirst()
                .orElseThrow(() -> new BadStatusException(s));
    }

}
