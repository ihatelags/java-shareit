package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private final List<CommentDto> comments = new ArrayList<>();

    private User user;
    private User user2;
    private Item item1;
    private CommentDto comment1;

    private Booking bookingByUser1;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

    private BookingInputDto bookingInputDto;
    private BookingOutputDto bookingOutputDto;

    @BeforeEach
    void beforeEach() {
        timeStart = LocalDateTime.of(2022, 1, 1, 1, 0);
        timeEnd = LocalDateTime.of(2023, 1, 1, 1, 0);
        user = new User(1L, "user", "user@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        itemDto = new BookingDto(1L, "item", "description", false, null, comments);
        itemDtoWithBooking = new ItemDtoWithBooking(1L, "item", "description", false,
                null, null, null);
        item1 = new Item(1L, user, "item 1", "item 1 description", true, null);
        comment1 = new CommentDto(1L, "Comment 1", 1L, 1L, "user2", timeStart);


        bookingByUser1 = new Booking(1L, user, item1, timeStart, timeEnd,
                BookingStatus.WAITING);
        bookingInputDto = new BookingInputDto(1L, 1L, item1.getId(), timeStart, timeEnd,
                BookingStatus.WAITING);
        bookingOutputDto = new BookingOutputDto(1L, new BookingOutputDto.User(1L),
                new BookingOutputDto.Item(1L, "item 1"),
                timeStart, timeEnd,
                BookingStatus.WAITING);
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.add(any(), any(BookingInputDto.class))).thenReturn(bookingOutputDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInputDto.getId()), Long.class));

        verify(bookingService, times(1))
                .add(anyLong(), any(BookingInputDto.class));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.bookingConfirmation(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingOutputDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInputDto.getId()), Long.class));

        verify(bookingService, times(1))
                .bookingConfirmation(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingOutputDto);

        mockMvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingByUser1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingByUser1.getId()), Long.class));

        verify(bookingService, times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void getBookingsByUser() throws Exception {
        when(bookingService.getAllBookingByUser(anyLong(), any(StateStatus.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto));

        mockMvc.perform(get("/bookings/?state=ALL")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingByUser1.getId()), Long.class));

        verify(bookingService, times(1))
                .getAllBookingByUser(anyLong(), any(StateStatus.class), anyInt(), anyInt());
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getAllBookingByOwner(anyLong(), any(StateStatus.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingByUser1.getId()), Long.class));

        verify(bookingService, times(1))
                .getAllBookingByOwner(anyLong(), any(StateStatus.class), anyInt(), anyInt());

        assertThrows(RuntimeException.class, () -> bookingService.getAllBookingByOwner(1L,
                StateStatus.fromString("bla"), 1, 10));
    }

}