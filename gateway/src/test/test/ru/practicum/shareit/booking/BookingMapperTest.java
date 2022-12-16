package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;

    private NewBookingDto newBookingDto;
    private Booking booking;
    private Item item;
    private User user;


    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        item = new Item(1L, "дрель", "дрель ударная Макита", user, true, 1L);
        newBookingDto = new NewBookingDto(LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), 1L);
        booking = new Booking(1L, LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), item, user, BookingStatus.WAITING);
    }

    @Test
    void shouldProperlyMapToBookingDtoTest() {
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        Assertions.assertNotNull(bookingDto);
        Assertions.assertEquals(booking.getId(), bookingDto.getId());
        Assertions.assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void shouldProperlyMapToBookingTest() {
        Booking booking1 = bookingMapper.toBooking(newBookingDto, item, user);

        Assertions.assertNotNull(booking1);
        Assertions.assertEquals(newBookingDto.getStart(), booking1.getStart());
    }

    @Test
    void shouldProperlyMaptoBookingDtoListTest() {
        List<Booking> bookingsList = Collections.singletonList(booking);
        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoList(bookingsList);

        Assertions.assertNotNull(bookingDtos);
        Assertions.assertEquals(1, bookingDtos.size());
        Assertions.assertEquals(booking.getId(), bookingDtos.get(0).getId());
    }
}
