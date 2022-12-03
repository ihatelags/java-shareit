package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                                @Valid @RequestBody BookingInputDto bookingInputDto,
                                HttpServletRequest httpServletRequest)
            throws ValidationException {
        log.info("Получен запрос к эндпоинту: {} {}, userId {}, тело запроса {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, bookingInputDto);
        return bookingService.add(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto bookingConfirmation(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam(value = "approved") boolean approved,
                                                HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, bookingId: {}, статус {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                bookingId,
                approved);
        return bookingService.bookingConfirmation(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable Long bookingId, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, bookingId: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "state", required = false,
                                                              defaultValue = "ALL") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from",
                                                              defaultValue = "0") int from,
                                                      @Positive @RequestParam(name = "size",
                                                              defaultValue = "10") int size,
                                                      HttpServletRequest httpServletRequest) {
        StateStatus state = StateStatus.fromString(stateParam);

        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, state {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                state);
        return bookingService.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(value = "state", required = false,
                                                               defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from",
                                                               defaultValue = "0") int from,
                                                       @Positive @RequestParam(name = "size",
                                                               defaultValue = "10") int size,
                                                       HttpServletRequest httpServletRequest) {
        StateStatus state = StateStatus.fromString(stateParam);

        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, state {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                state);
        return bookingService.getAllBookingByOwner(userId, state, from, size);
    }
}
