package test.ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private NewBookingDto newBookingDto;
    private Booking booking;
    private Item item;
    private User userOwner;
    private User userNotOwner;
    private Booking bookingByNewUser;


    @BeforeEach
    void beforeEach() {
        userOwner = new User(null, "user 1", "user1@email");
        userNotOwner = new User(null, "user 2", "user2@email");
        item = new Item(null, "дрель", "дрель ударная Макита", userOwner, true, null);
        newBookingDto = new NewBookingDto(LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), 1L);
        booking = new Booking(null, LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), item, userOwner, WAITING);
        bookingByNewUser = new Booking(null, LocalDateTime.of(2022, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), item, userNotOwner, WAITING);
    }

    @Test
    void findAllBookingsOfUserTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findAllBookingsOfUser(userNotOwner.getId(), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findBookingsOfUserBetweenTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findBookingsOfUserBetween(userNotOwner.getId(), LocalDateTime.of(2022, 9, 3, 9, 55), LocalDateTime.now(), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(0);
    }

    @Test
    void findBookingsOfUserPastTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findBookingsOfUserPast(userNotOwner.getId(), LocalDateTime.of(2022, 12, 3, 9, 55), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findBookingsOfUserFutureTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findBookingsOfUserFuture(userNotOwner.getId(), LocalDateTime.of(2022, 2, 3, 9, 55), PageRequest.of(0, 10));

        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllBookingsOfUserWithStatusTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsListWait = bookingRepository.findAllBookingsOfUserWithStatus(userNotOwner.getId(), WAITING, PageRequest.of(0, 10));
        List<Booking> bookingsListApp = bookingRepository.findAllBookingsOfUserWithStatus(userNotOwner.getId(), APPROVED, PageRequest.of(0, 10));
        then(bookingsListWait).size().isEqualTo(1);
        then(bookingsListApp).size().isEqualTo(0);
    }

    //for owner
    @Test
    void findAllBookingsOfItemsUserOwnerTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findAllBookingsOfItemsUser(List.of(item.getId()), PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findBookingsOfUserOwnerItemsBetweenTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findBookingsOfUserItemsBetween(List.of(item.getId()), LocalDateTime.of(2022, 11, 4, 9, 55), LocalDateTime.of(2022, 11, 6, 19, 55), PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findBookingsOfUserOwnerItemsInPastTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findBookingsOfUserItemsInPast(List.of(item.getId()), LocalDateTime.of(2023, 12, 8, 19, 55), PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findBookingsOfUserOwnerItemsInFutureTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findBookingsOfUserItemsInFuture(List.of(item.getId()), LocalDateTime.now(), PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findAllBookingsOfUserOwnerItemsWithStatusTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        em.persist(bookingByNewUser);

        List<Booking> bookingsList = bookingRepository.findAllBookingsOfUserItemsWithStatus(List.of(item.getId()), WAITING, PageRequest.of(0, 10));
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findLastBookingsTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        booking = new Booking(null, LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2021, 11, 8, 19, 55), item, userNotOwner, WAITING);
        em.persist(booking);

        List<Booking> bookingsList = bookingRepository.findLastBookings(userOwner.getId(), item.getId(), LocalDateTime.now());
        then(bookingsList).size().isEqualTo(1);
    }

    @Test
    void findFutureBookingsTest() {
        em.persist(userOwner);
        em.persist(userNotOwner);
        em.persist(item);
        booking = new Booking(null, LocalDateTime.of(2022, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), item, userNotOwner, WAITING);
        em.persist(booking);

        List<Booking> bookingsList = bookingRepository.findFutureBookings(userOwner.getId(), item.getId(), LocalDateTime.now());
        then(bookingsList).size().isEqualTo(1);
    }
}
