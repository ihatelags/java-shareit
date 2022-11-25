package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingOutputDto toBookingDto(Booking booking) {
        return new BookingOutputDto(
                booking.getId(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static BookingOutputDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingOutputDtoForItem(
                booking.getId(),
                booking.getBooker().getId(),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingInputDto bookingInputDto, User booker, Item item) {
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
