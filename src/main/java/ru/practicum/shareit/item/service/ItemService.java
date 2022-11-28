package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDtoWithBooking getById(long itemId, long userId);

    List<ItemDtoWithBooking> getAllByUserId(long userId);

    List<ItemDto> searchByText(String text);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
