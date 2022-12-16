package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.StateStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID_HEADER) long userId,
                                      @RequestBody @Valid BookingDto bookingDto) {
        log.info("ShareIt-gateway: Получен POST запрос к эндпоинту /bookings. booking: {}, user id:{}",
                bookingDto, userId);
        return bookingClient.add(userId, bookingDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingByUser(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from",
                                                              defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        StateStatus state = StateStatus.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /bookings. user id: {}, state {}",
                userId, state);
        return bookingClient.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable Long bookingId) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /bookings/{}. user id: {}",
                bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingConfirmation(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @PathVariable("bookingId") long bookingId,
                                                      @RequestParam("approved") Boolean approved) {
        log.info("ShareIt-gateway: Получен PATCH запрос к эндпоинту /bookings/{}", bookingId);
        return bookingClient.bookingConfirmation(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0"
                                                       ) int from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        StateStatus state = StateStatus.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /bookings/owner. user id: {}, state {}",
                userId, state);
        return bookingClient.getAllBookingByOwner(userId, state, from, size);
    }
}
