package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private BookingOutputDto bookingOutputDto;
    private Booking booking;
    private Item item;
    private User userOwner;
    private User userNotOwner;
    private Booking bookingByNewUser;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;


    @BeforeEach
    void beforeEach() {
        timeStart = LocalDateTime.of(2022, 1, 1, 1, 0);
        timeEnd = LocalDateTime.of(2023, 1, 1, 1, 0);
        userOwner = new User(null, "user 1", "user1@email");
        em.persist(userOwner);
        userNotOwner = new User(null, "user 2", "user2@email");
        em.persist(userOwner);
        item = new Item(null, userOwner, "item 1", "item 1 description", true, null);
        em.persist(item);
        bookingOutputDto = new BookingOutputDto(1L, new BookingOutputDto.User(1L),
                new BookingOutputDto.Item(1L, "item 1"),
                timeStart, timeEnd,
                BookingStatus.WAITING);
        bookingByNewUser = new Booking(null, userNotOwner, item, timeStart, timeEnd,
                BookingStatus.WAITING);
        em.persist(bookingByNewUser);
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {

        List<Booking> bookingsList = bookingRepository.findAllByBookerIdOrderByStartDesc(
                userNotOwner.getId(), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        List<Booking> bookingsList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                userNotOwner.getId(), timeStart.minusMinutes(1), timeEnd.plusMinutes(1), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(0);
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeOrderByStartDescTest() {
        List<Booking> bookingsList = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                userNotOwner.getId(), timeEnd.plusMinutes(1), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllByBookerIdAndStartIsAfterOrderByStartDescTest() {
        List<Booking> bookingsList = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(
                userNotOwner.getId(), timeStart.minusMinutes(1), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookingsListWait = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                userNotOwner.getId(), WAITING, PageRequest.of(0, 10));
        List<Booking> bookingsListApp = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                userNotOwner.getId(), APPROVED, PageRequest.of(0, 10));
        then(bookingsListWait).size().isEqualTo(1);
        then(bookingsListApp).size().isEqualTo(0);
    }

    //for owner
    @Test
    void findAllBookingsOfItemsUserOwnerTest() {
        List<Booking> bookingsList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                userOwner.getId(), PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        List<Booking> bookingsList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                userOwner.getId(), timeStart.plusMinutes(1), timeEnd.minusMinutes(1), PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBeforeOrderByStartDescTest() {
        List<Booking> bookingsList = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                userOwner.getId(), timeEnd.plusMinutes(1), PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfterOrderByStartDescTest() {
        List<Booking> bookingsList = bookingRepository
                .findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userOwner.getId(), timeStart.minusMinutes(1),
                        PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookingsList = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(userOwner.getId(), WAITING, PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findFirstBookingByItemIdAndEndIsBeforeOrderByEndDescTest() {
        booking = new Booking(null, userNotOwner, item, timeStart, timeEnd, WAITING);
        em.persist(booking);
        List<Booking> bookingsList = bookingRepository
                .findFirstBookingByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), timeEnd.plusMinutes(1))
                .stream().collect(Collectors.toList());
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findFirstBookingByItemIdAndStartIsAfterOrderByStartTest() {
        booking = new Booking(null, userNotOwner, item, timeStart.plusDays(1), timeEnd, WAITING);
        em.persist(booking);

        List<Booking> bookingsList = bookingRepository
                .findFirstBookingByItemIdAndStartIsAfterOrderByStart(item.getId(), timeStart.plusMinutes(1))
                .stream().collect(Collectors.toList());
        then(bookingsList).size().isEqualTo(1);
    }
}