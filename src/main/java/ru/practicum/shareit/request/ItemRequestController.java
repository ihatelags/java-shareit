package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody NewItemRequestDto newItemRequestDto,
                                        HttpServletRequest httpServletRequest)
            throws ValidationException {
        log.info("Получен запрос к эндпоинту: {} {}, userId {}, тело запроса {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, newItemRequestDto);
        return itemRequestService.createRequest(userId, newItemRequestDto);
    }


    @GetMapping
    public List<ItemRequestDto> getRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(name = "from",
                                                          defaultValue = "0") int from,
                                                  @Positive @RequestParam(name = "size",
                                                          defaultValue = "10") int size,
                                                  HttpServletRequest httpServletRequest) {

        log.info("Получен запрос {} к эндпоинту: {}, user id: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId);
        return itemRequestService.getRequestsOfUser(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getExistingRequestsOfUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PositiveOrZero @RequestParam(name = "from",
                                                                   defaultValue = "0") int from,
                                                           @Positive @RequestParam(name = "size",
                                                                   defaultValue = "10") int size,
                                                           HttpServletRequest httpServletRequest) {

        log.info("Получен запрос {} к эндпоинту: {}, user id: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId);
        return itemRequestService.getExistingRequestsOfUsers(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable Long requestId, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, requestId: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                requestId);
        return itemRequestService.getRequest(userId, requestId);
    }
}
