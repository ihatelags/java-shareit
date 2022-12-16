package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDtoWithBooking getById(long itemId, long userId);

    List<ItemDtoWithBooking> getAllByUserId(long userId, int from, int size);

    List<BookingDto> searchByText(String text, int from, int size);

    BookingDto add(long userId, BookingDto itemDto);

    BookingDto update(long userId, long itemId, BookingDto itemDto);

    void delete(long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
