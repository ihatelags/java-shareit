package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getById(long itemId);

    List<ItemDto> getAllByUserId(long userId);

    List<ItemDto> searchByText(String text);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);
}
