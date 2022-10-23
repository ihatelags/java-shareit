package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
public class BookingDto {
    private long id;
    private User booker;
    private Item item;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
}
