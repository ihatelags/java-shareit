package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.StateStatus;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface BookingService {
    BookingOutputDto add(Long userId, BookingInputDto bookingInputDto) throws ValidationException;

    BookingOutputDto bookingConfirmation(Long userId, Long bookingId, boolean approved);

    BookingOutputDto getById(Long userId, Long bookingId);

    List<BookingOutputDto> getAllBookingByUser(Long userId, StateStatus state, int from, int size);

    List<BookingOutputDto> getAllBookingByOwner(Long userId, StateStatus state, int from, int size);
}
