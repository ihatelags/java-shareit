package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@SpringBootTest
class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;
    private User user;
    private Item item;
    private Booking booking;
    private LocalDateTime timeEnd;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        item = new Item(1L, user, "дрель", "дрель ударная Макита", true, 1L);
        LocalDateTime timeStart = LocalDateTime.now().plusMinutes(15);
        timeEnd = LocalDateTime.now().plusDays(15);
        booking = new Booking(1L, user, item, timeStart, timeEnd, BookingStatus.APPROVED);
    }

    @Test
    void shouldProperlyMapToBookingOutputDtoTest() {
        BookingOutputDto bookingOutputDto = bookingMapper.toBookingDto(booking);

        Assertions.assertNotNull(bookingOutputDto);
        Assertions.assertEquals(booking.getId(), bookingOutputDto.getId());
        Assertions.assertEquals(booking.getBooker().getId(), bookingOutputDto.getBooker().getId());
    }

    @Test
    void shouldProperlyMapToBookingTest() {
        BookingInputDto bookingInputDto = new BookingInputDto(1L, 1L, item.getId(),
                LocalDateTime.now().plusMinutes(1), timeEnd.minusMinutes(1), BookingStatus.WAITING);
        booking = bookingMapper.toBooking(bookingInputDto, user, item);

        Assertions.assertNotNull(booking);
        Assertions.assertEquals(booking.getItem().getId(), bookingInputDto.getItemId());
    }

    @Test
    void shouldProperlyMapTotoBookingOutputDtoForItemTest() {
        BookingOutputDtoForItem bookingOutputDtoForItem = bookingMapper.toBookingDtoForItem(booking);

        Assertions.assertNotNull(bookingOutputDtoForItem);
        Assertions.assertEquals(item.getId(), bookingOutputDtoForItem.getItemId());
    }

}