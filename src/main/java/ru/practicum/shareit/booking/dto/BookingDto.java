package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDate;

@Data
public class BookingDto {
    private long id;
    private long bookerId;
    private long itemId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
}
