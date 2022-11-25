package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemDao {
    Item getById(long itemId);

    List<Item> getAllByUserId(long userId);

    List<Item> searchByText(String text);

    Item add(User user, ItemDto itemDto);

    Item update(User user, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

}
