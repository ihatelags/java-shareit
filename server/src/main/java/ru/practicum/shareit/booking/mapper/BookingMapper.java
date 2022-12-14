package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Service
public class BookingMapper {

    public BookingOutputDto toBookingDto(Booking booking) {
        return new BookingOutputDto(
                booking.getId(),
                new BookingOutputDto.User(booking.getBooker().getId()),
                new BookingOutputDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public BookingOutputDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingOutputDtoForItem(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public Booking toBooking(BookingInputDto bookingInputDto, User booker, Item item) {
        return new Booking(
                bookingInputDto.getId(),
                booker,
                item,
                bookingInputDto.getStartDate(),
                bookingInputDto.getEndDate(),
                bookingInputDto.getStatus()
        );
    }
}
