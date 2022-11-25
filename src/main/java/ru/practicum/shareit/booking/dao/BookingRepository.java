package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findAllByBookerIdOrderByStartDesc(long bookerId);

    Optional<List<Booking>> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    Optional<List<Booking>> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                                       LocalDateTime start,
                                                                                       LocalDateTime end);

    Optional<List<Booking>> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    Optional<List<Booking>> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    List<Booking> findAllByItemIdOrderByStartDesc(long itemId);

    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(long itemId, BookingStatus status);

    List<Booking> findAllByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(long itemId,
                                                                           LocalDateTime start,
                                                                           LocalDateTime end);

    List<Booking> findAllByItemIdAndStartIsAfterOrderByStartDesc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndEndIsBeforeOrderByStartDesc(long itemId, LocalDateTime end);

    boolean existsBookingByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(long bookerId, long itemId,
                                                                          BookingStatus status, LocalDateTime end);

    Optional<Booking> findFirstBookingByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime now);

    Optional<Booking> findFirstBookingByItemIdAndStartIsAfterOrderByStart(long itemId, LocalDateTime now);

}
