package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.StateStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingOutputDto add(Long userId, BookingInputDto bookingInputDto) throws ValidationException {
        checkInputBookingDto(userId, bookingInputDto);
        bookingInputDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingMapper.toBooking(bookingInputDto, getUser(userId),
                getItem(bookingInputDto.getItemId()));

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto bookingConfirmation(Long userId, Long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingConfirmationException("Нельзя изменить статус одобренного бронирования");
        }
        BookingStatus status;
        if (approved) {
            status = BookingStatus.APPROVED;
        } else {
            status = BookingStatus.REJECTED;
        }
        if (checkOwner(userId, booking)) {
            booking.setStatus(status);
        } else {
            throw new BookingNotFoundException("Пользователь не является владельцем вещи");
        }

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto getById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (checkOwner(userId, booking) || Objects.equals(booking.getBooker().getId(), userId)) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Пользователь не является владельцем или арендатором вещи");
        }
    }

    @Override
    public List<BookingOutputDto> getAllBookingByUser(Long userId, StateStatus state, int from, int size) {
        getUser(userId);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Pageable page = getOffset(from, size);
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, page);
                break;
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
        }
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getAllBookingByOwner(Long userId, StateStatus state, int from, int size) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        Pageable page = getOffset(from, size);
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, now, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, page);
                break;
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page);
                break;
        }
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирования с таким id: "
                        + bookingId + " не существует"));
    }

    private boolean checkOwner(Long userId, Booking booking) {
        return Objects.equals(booking.getItem().getOwner().getId(), userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id: " + userId + " не существует"));
    }

    private Item getItem(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с таким id: " + itemId + " не существует"));
        if (item.isAvailable()) {
            return item;
        } else {
            throw new ItemAvailableException("Вещь " + itemId + " недоступна");
        }
    }

    private void checkInputBookingDto(long userId, BookingInputDto bookingInputDto) throws ValidationException {
        if (bookingInputDto.getStartDate().isAfter(bookingInputDto.getEndDate())) {
            throw new ValidationException("Время начала бронирования не может быть позже времени" +
                    " окончания бронирования");
        }
        if (bookingInputDto.getStartDate() == bookingInputDto.getEndDate()) {
            throw new ValidationException("Время начала бронирования не может быть равно времени" +
                    " окончания бронирования");
        }
        if (bookingInputDto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала бронирования не может в прошлом");
        }
        if (bookingInputDto.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время конца бронирования не может в прошлом");
        }
        if (userId == getItem(bookingInputDto.getItemId()).getOwner().getId()) {
            throw new BookingNotFoundException("Пользователь " + userId + " не может забронировать свою вещь "
                    + bookingInputDto.getItemId());
        }
    }

    private Pageable getOffset(int from, int size) {
        return PageRequest.of(from / size, size);
    }

}
