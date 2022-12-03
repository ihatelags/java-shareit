package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingConfirmationException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;


    private BookingInputDto bookingInputDto;
    private Booking booking;
    private Item item;
    private User userOwner;
    private User userNotOwner;
    private Booking bookingApproved;
    private Booking bookingByNewUser;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

    @BeforeEach
    void beforeEach() {
        timeStart = LocalDateTime.of(2022, 1, 1, 1, 0);
        timeEnd = LocalDateTime.of(2023, 1, 1, 1, 0);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingMapper);
        userOwner = new User(1L, "user 1", "user1@email");
        userNotOwner = new User(2L, "user 2", "user2@email");
        item = new Item(1L, userOwner, "item 1", "item 1 description", true, null);
        booking = new Booking(1L, userOwner, item, timeStart, timeEnd,
                BookingStatus.WAITING);
        bookingInputDto = new BookingInputDto(1L, userNotOwner.getId(), item.getId(),
                LocalDateTime.now().plusMinutes(1), timeEnd.minusMinutes(1), BookingStatus.WAITING);
        bookingByNewUser = new Booking(1L, userNotOwner, item, timeStart, timeEnd,
                BookingStatus.WAITING);
    }

    @Test
    void createBookingTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        bookingService.add(userNotOwner.getId(), bookingInputDto);

        BookingInputDto bookingInputDtoStartPast = new BookingInputDto(1L, userNotOwner.getId(), item.getId(),
                LocalDateTime.now().minusMinutes(15), timeEnd.minusMinutes(1), BookingStatus.WAITING);
        assertThrows(RuntimeException.class, () -> bookingService.add(1L, bookingInputDtoStartPast));

        BookingInputDto bookingInputDtoEndPast = new BookingInputDto(1L, userNotOwner.getId(), item.getId(),
                LocalDateTime.now().plusMinutes(15), LocalDateTime.now().minusMinutes(15), BookingStatus.WAITING);
        assertThrows(RuntimeException.class, () -> bookingService.add(1L, bookingInputDtoEndPast));

        BookingInputDto bookingInputDtoStartEqEnd = new BookingInputDto(1L, userNotOwner.getId(), item.getId(),
                LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusMinutes(15), BookingStatus.WAITING);
        assertThrows(RuntimeException.class, () -> bookingService.add(1L, bookingInputDtoStartEqEnd));

        BookingInputDto bookingInputDtoUserEqOwner = new BookingInputDto(1L, userOwner.getId(), item.getId(),
                LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusMinutes(15), BookingStatus.WAITING);
        assertThrows(RuntimeException.class, () -> bookingService.add(1L, bookingInputDtoUserEqOwner));
    }

    @Test
    void createBookingWithNotAvailableItemTest() {
        Item itemNew = new Item(1L, userOwner, "item 2", "item 2 description", false, 1L);
        bookingInputDto.setStartDate(LocalDateTime.now().plusDays(1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemNew));
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userOwner));
        Exception ex = assertThrows(RuntimeException.class, () -> bookingService.add(1L, bookingInputDto));
        assertEquals("Вещь 1 недоступна", ex.getMessage());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void changeBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userOwner));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        bookingService.bookingConfirmation(userOwner.getId(), booking.getId(), false);

        verify(bookingRepository, times(1)).save(booking);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void changeBookingNotOwnerWithExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenThrow(new BookingNotFoundException("Пользователь не является владельцем вещи"));

        Exception ex = assertThrows(RuntimeException.class,
                () -> bookingService.bookingConfirmation(userNotOwner.getId(), booking.getId(), false));
        assertEquals("Пользователь не является владельцем вещи", ex.getMessage());

        verify(bookingRepository, times(0)).save(booking);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void changeAlreadyApprovedBookingWithExceptionTest() {
        bookingApproved = new Booking(1L, userOwner, item, timeStart, timeEnd,
                BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenThrow(new BookingConfirmationException("Status already APPROVED"));
        Exception ex = assertThrows(RuntimeException.class,
                () -> bookingService.bookingConfirmation(userNotOwner.getId(), bookingApproved.getId(), true));
        assertEquals("Status already APPROVED", ex.getMessage());

        verify(bookingRepository, times(0)).save(booking);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getByIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userOwner));

        bookingService.getById(userOwner.getId(), booking.getId());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getAllBookingByUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingOutputDto> bookingDtos = bookingService.getAllBookingByUser(userNotOwner.getId(),
                StateStatus.ALL, 0, 10);

        assertNotNull(bookingDtos);

        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 10));

        final List<BookingOutputDto> bookingDtosCurrent = bookingService.getAllBookingByUser(userOwner.getId(),
                StateStatus.CURRENT, 0, 10);
        assertNotNull(bookingDtosCurrent);
        final List<BookingOutputDto> bookingDtosPast = bookingService.getAllBookingByUser(userOwner.getId(),
                StateStatus.PAST, 0, 10);
        assertNotNull(bookingDtosPast);
        final List<BookingOutputDto> bookingDtosFuture = bookingService.getAllBookingByUser(userOwner.getId(),
                StateStatus.FUTURE, 0, 10);
        assertNotNull(bookingDtosFuture);
        final List<BookingOutputDto> bookingDtosWaiting = bookingService.getAllBookingByUser(userOwner.getId(),
                StateStatus.WAITING, 0, 10);
        assertNotNull(bookingDtosWaiting);
        final List<BookingOutputDto> bookingDtosRejected = bookingService.getAllBookingByUser(userOwner.getId(),
                StateStatus.REJECTED, 0, 10);
        assertNotNull(bookingDtosRejected);
    }

    @Test
    void getAllBookingByOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userOwner));
        final List<Item> items = new ArrayList<>(Collections.singletonList(item));
        when(itemRepository.findAll())
                .thenReturn(items);
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingOutputDto> bookingDtos = bookingService.getAllBookingByOwner(userOwner.getId(),
                StateStatus.ALL, 0, 10);

        assertNotNull(bookingDtos);

        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(userOwner.getId(), PageRequest.of(0,
                10));

        final List<BookingOutputDto> bookingDtosCurrent = bookingService.getAllBookingByOwner(userOwner.getId(),
                StateStatus.CURRENT, 0, 10);
        assertNotNull(bookingDtosCurrent);
        final List<BookingOutputDto> bookingDtosPast = bookingService.getAllBookingByOwner(userOwner.getId(),
                StateStatus.PAST, 0, 10);
        assertNotNull(bookingDtosPast);
        final List<BookingOutputDto> bookingDtosFuture = bookingService.getAllBookingByOwner(userOwner.getId(),
                StateStatus.FUTURE, 0, 10);
        assertNotNull(bookingDtosFuture);
        final List<BookingOutputDto> bookingDtosWaiting = bookingService.getAllBookingByOwner(userOwner.getId(),
                StateStatus.WAITING, 0, 10);
        assertNotNull(bookingDtosWaiting);
        final List<BookingOutputDto> bookingDtosRejected = bookingService.getAllBookingByOwner(userOwner.getId(),
                StateStatus.REJECTED, 0, 10);
        assertNotNull(bookingDtosRejected);
    }

}