package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    @Transactional(rollbackOn = Exception.class)
    public ItemDtoWithBooking getById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                      HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, item id: {}, user id: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                itemId,
                userId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    @Transactional(rollbackOn = Exception.class)
    public List<ItemDtoWithBooking> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "from",
                                                           defaultValue = "0") int from,
                                                   @RequestParam(name = "size",
                                                           defaultValue = "10") int size,
                                                   HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId);
        return itemService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<BookingDto> searchByText(@RequestParam(value = "text", required = false) String text,
                                         @RequestParam(name = "from",
                                                 defaultValue = "0") int from,
                                         @RequestParam(name = "size",
                                                 defaultValue = "10") int size,
                                         HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, текст поиска: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                text);
        return itemService.searchByText(text, from, size);
    }

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingDto itemDto,
                          HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, тело запроса: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                itemDto);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long itemId,
                             @RequestBody BookingDto itemDto,
                             HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, item id: {}, тело запроса: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                itemId,
                itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                       HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, item id: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                itemId);
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, item id: {}, тело запроса: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                itemId,
                commentDto);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
