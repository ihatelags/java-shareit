package ru.practicum.gateway.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.Create;
import ru.practicum.gateway.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ShareIt-gateway: Получен POST запрос к эндпоинту /requests. item request: {}, user id:{}",
                itemRequestDto, userId);
        return requestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /requests. user id:{}",
                userId);
        return requestClient.getRequestsOfUser(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getExistingRequestsOfUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @PositiveOrZero @RequestParam(name = "from",
                                                                     defaultValue = "0") int from,
                                                             @Positive @RequestParam(name = "size", defaultValue =
                                                                     "10") int size) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /requests/all. user id:{}",
                userId);
        return requestClient.getExistingRequestsOfUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("requestId") long requestId) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /requests/{}. user id:{}",
                requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }


}
