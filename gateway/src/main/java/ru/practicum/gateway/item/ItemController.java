package ru.practicum.gateway.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.Create;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.item.dto.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("ShareIt-gateway: Получен POST запрос к эндпоинту /items. item id: {}, user id:{}",
                itemDto.getId(), userId);
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated({Update.class}) @PathVariable("itemId") long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("ShareIt-gateway: Получен PATCH запрос к эндпоинту /items{}. user id:{}",
                itemDto.getId(), userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /items{}. user id:{}",
                itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /items. user id:{}",
                userId);
        return itemClient.getAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam("text") String text,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /items/search. text:{}",
                text);
        return itemClient.searchByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                             @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("ShareIt-gateway: Получен POST запрос к эндпоинту /items/{}/comment. comment id:{}",
                itemId, commentDto.getId());
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
