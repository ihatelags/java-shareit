package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.StateStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingOutputDto add(Long userId, BookingInputDto bookingInputDto) throws ValidationException {
        checkInputBookingDto(userId, bookingInputDto);

        bookingInputDto.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository
                .save(BookingMapper.toBooking(bookingInputDto, getUser(userId), getItem(bookingInputDto.getItemId()))));
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

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto getById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (checkOwner(userId, booking) || booking.getBooker().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Пользователь не является владельцем или арендатором вещи");
        }
    }

    @Override
    public List<BookingOutputDto> getAllBookingByUser(Long userId, StateStatus state) {
        getUser(userId);
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now())
                        .orElseThrow(() -> new BookingNotFoundException("Текущих бронирований для пользователя "
                                + userId + " не найдено"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .orElseThrow(() -> new BookingNotFoundException("Завершенных бронирований для пользователя "
                                + userId + " не найдено"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .orElseThrow(() -> new BookingNotFoundException("Будущих бронирований для пользователя "
                                + userId + " не найдено"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.WAITING)
                        .orElseThrow(() -> new BookingNotFoundException("Ожидающих подтверждения бронирований " +
                                "для пользователя " + userId + " не найдено"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.REJECTED)
                        .orElseThrow(() -> new BookingNotFoundException("Отклоненных бронирований для пользователя "
                                + userId + " не найдено"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .orElseThrow(() -> new BookingNotFoundException("Бронирований для пользователя "
                                + userId + " не найдено"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingOutputDto> getAllBookingByOwner(Long userId, StateStatus state) {
        getUser(userId);
        List<Item> items = itemRepository.findByOwner(getUser(userId));
        if (itemRepository.findByOwner(getUser(userId)).size() == 0) {
            throw new ItemNotFoundException("У данного пользователя " + userId + " нет вещей");
        }
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case CURRENT:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(item.getId(), LocalDateTime.now(),
                                LocalDateTime.now())));
                if (bookings.size() != 0) {
                    return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                } else {
                    throw new BookingNotFoundException("Текущих бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case PAST:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItemIdAndEndIsBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())));
                if (bookings.size() != 0) {
                    return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case FUTURE:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItemIdAndStartIsAfterOrderByStartDesc(item.getId(), LocalDateTime.now())));
                if (bookings.size() != 0) {
                    return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case WAITING:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItemIdAndStatusOrderByStartDesc(item.getId(), BookingStatus.WAITING)));
                if (bookings.size() != 0) {
                    return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case REJECTED:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItemIdAndStatusOrderByStartDesc(item.getId(), BookingStatus.REJECTED)));
                if (bookings.size() != 0) {
                    return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            default:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItemIdOrderByStartDesc(item.getId())));
                if (bookings.size() != 0) {
                    return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
        }
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирования с таким id: "
                        + bookingId + " не существует"));
    }

    private boolean checkOwner(Long userId, Booking booking) {
        return booking.getItem().getOwner().getId() == userId;
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

}
