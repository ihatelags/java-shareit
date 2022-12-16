package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NotFoundException;

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
public class BookingServiceImplTest {

    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;
    private NewBookingDto newBookingDto;
    private Booking booking;
    private Item item;
    private User user;
    private User userNotOwner;
    private Booking bookingApproved;
    private Booking bookingByNewUser;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper);
        user = new User(1L, "user 1", "user1@email");
        userNotOwner = new User(2L, "user 2", "user2@email");
        item = new Item(1L, "дрель", "дрель ударная Макита", user, true, 1L);
        newBookingDto = new NewBookingDto(LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), 1L);
        booking = new Booking(1L, LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), item, user, BookingStatus.WAITING);
        bookingByNewUser = new Booking(1L, LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), item, userNotOwner, BookingStatus.WAITING);
    }

    @Test
    void createBookingTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        when(bookingMapper.toBooking(any(), any(), any()))
                .thenReturn(booking);
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        bookingService.createBooking(userNotOwner.getId(), newBookingDto);

        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBookingWithNotFoundUserTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());
        final var ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(user.getId(), newBookingDto));

        verify(itemRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createBookingByOwnerWithExceptionTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        final var ex = assertThrows(NotFoundException.class, () -> bookingService.createBooking(user.getId(), newBookingDto));
        assertEquals(ex.getClass(), NotFoundException.class);
        verify(itemRepository, times(1)).findById(1L);
        verify(bookingRepository, times(0)).save(booking);
    }

    @Test
    void createBookingWithNotAvailableItemTest() {
        Item itemNew = new Item(1L, "дрель", "дрель ударная Макита", user, false, 1L);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemNew));
        final var ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(user.getId(), newBookingDto));
        assertEquals("item not available for booking", ex.getMessage());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void changeBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        bookingService.changeBooking(user.getId(), booking.getId(), false);

        verify(bookingRepository, times(1)).save(booking);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void changeBookingNotOwnerWithExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));

        final var ex = assertThrows(NotOwnerException.class, () -> bookingService.changeBooking(userNotOwner.getId(), booking.getId(), false));
        assertEquals("the request can only change by the owner", ex.getMessage());

        verify(bookingRepository, times(0)).save(booking);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void changeWithNotFoundUserExTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> bookingService.changeBooking(userNotOwner.getId(), booking.getId(), false));
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void changeWithNotFoundBookingExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> bookingService.changeBooking(userNotOwner.getId(), booking.getId(), false));
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void changeAlreadyApprovedBookingWithExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        bookingApproved = new Booking(1L, LocalDateTime.of(2021, 11, 3, 9, 55), LocalDateTime.of(2022, 11, 8, 19, 55), item, user, BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingApproved));
        final var ex = assertThrows(BadRequestException.class, () -> bookingService.changeBooking(user.getId(), bookingApproved.getId(), true));
        assertEquals("Status already APPROVED", ex.getMessage());

        verify(bookingRepository, times(0)).save(booking);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        bookingService.getBooking(user.getId(), booking.getId());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getWithNotFoundBookingExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> bookingService.getBooking(user.getId(), booking.getId()));
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getWithNotOwnerExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));

        final var ex = assertThrows(NotOwnerException.class, () -> bookingService.getBooking(2L, booking.getId()));
        assertEquals("the request can only be made by the owner or booker", ex.getMessage());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void getWithNotFoundUserExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> bookingService.getBooking(user.getId(), booking.getId()));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getListOfBookingsWithBookingStateAllTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllBookingsOfUser(anyLong(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfUser(userNotOwner.getId(), BookingState.ALL, 0, 10);

        assertNotNull(bookingDtos);

        verify(bookingRepository, times(1)).findAllBookingsOfUser(2L, PageRequest.of(0, 10));
    }

    @Test
    void getListOfBookingsWithBookingStateCURRENTTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findBookingsOfUserBetween(anyLong(), any(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfUser(userNotOwner.getId(), BookingState.CURRENT, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getListOfBookingsWithBookingStatePASTTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findBookingsOfUserPast(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfUser(userNotOwner.getId(), BookingState.PAST, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getListOfBookingsWithBookingStateFUTURETest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findBookingsOfUserFuture(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfUser(userNotOwner.getId(), BookingState.FUTURE, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getListOfBookingsWithBookingStateWAITINGTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllBookingsOfUserWithStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfUser(userNotOwner.getId(), BookingState.WAITING, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getListOfBookingsWithBookingStateREJECTEDTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllBookingsOfUserWithStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfUser(userNotOwner.getId(), BookingState.REJECTED, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getListOfBookingsWithBookingStateNULLTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNotOwner));
        when(bookingRepository.findAllBookingsOfUserWithStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenThrow(new IllegalArgumentException());
        final var ex = assertThrows(RuntimeException.class, () -> bookingService.getBookingOfUser(userNotOwner.getId(), null, 0, 10));
    }


    @Test
    void getListBookingsWithNotFoundUserTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> bookingService.getBookingOfUser(userNotOwner.getId(), BookingState.ALL, 0, 10));
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void getBookingOfOwnerWithStateAllTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<Item> items = new ArrayList<>(Collections.singletonList(item));
        when(itemRepository.findAll())
                .thenReturn(items);
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllBookingsOfItemsUser(any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfOwner(user.getId(), BookingState.ALL, 0, 10);

        assertNotNull(bookingDtos);

        verify(bookingRepository, times(1)).findAllBookingsOfItemsUser(List.of(1L), PageRequest.of(0, 10));
    }

    @Test
    void getBookingOfOwnerWithStateCURRENTTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<Item> items = new ArrayList<>(Collections.singletonList(item));
        when(itemRepository.findAll())
                .thenReturn(items);
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findBookingsOfUserItemsBetween(anyList(), any(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfOwner(user.getId(), BookingState.CURRENT, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getBookingOfOwnerWithStatePASTTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<Item> items = new ArrayList<>(Collections.singletonList(item));
        when(itemRepository.findAll())
                .thenReturn(items);
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findBookingsOfUserItemsInPast(anyList(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfOwner(user.getId(), BookingState.PAST, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getBookingOfOwnerWithStateFUTURETest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<Item> items = new ArrayList<>(Collections.singletonList(item));
        when(itemRepository.findAll())
                .thenReturn(items);
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findBookingsOfUserItemsInFuture(anyList(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfOwner(user.getId(), BookingState.FUTURE, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getBookingOfOwnerWithStateWAITINGTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<Item> items = new ArrayList<>(Collections.singletonList(item));
        when(itemRepository.findAll())
                .thenReturn(items);
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllBookingsOfUserItemsWithStatus(anyList(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfOwner(user.getId(), BookingState.WAITING, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getBookingOfOwnerWithStateREJECTEDTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<Item> items = new ArrayList<>(Collections.singletonList(item));
        when(itemRepository.findAll())
                .thenReturn(items);
        final List<Booking> bookings = new ArrayList<>(Collections.singletonList(bookingByNewUser));
        when(bookingRepository.findAllBookingsOfUserItemsWithStatus(anyList(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(bookings);

        final List<BookingDto> bookingDtos = bookingService.getBookingOfOwner(user.getId(), BookingState.REJECTED, 0, 10);

        assertNotNull(bookingDtos);
    }

    @Test
    void getBookingOfOwnerWithNotFoundUserTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> bookingService.getBookingOfOwner(user.getId(), BookingState.ALL, 0, 10));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getBookingOfOwnerWithStatusNULLTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final var ex = assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingOfOwner(user.getId(), BookingState.valueOf("null"), 0, 10));
        assertEquals(ex.getClass(), IllegalArgumentException.class);
    }
}
