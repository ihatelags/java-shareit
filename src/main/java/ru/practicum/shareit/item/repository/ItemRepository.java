package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    Item getById(long itemId);

    List<Item> getAllByUserId(long userId);

    List<Item> searchByText(String text);

    Item add(User user, ItemDto itemDto);

    Item update(User user, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

}
