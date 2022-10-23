package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AccessErrorException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private long id = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item getById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllByUserId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByText(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter((item) -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item add(User user, ItemDto itemDto) {
        long itemId = getNextId();
        items.put(itemId, ItemMapper.toItem(itemDto));
        items.get(itemId).setId(itemId);
        items.get(itemId).setOwner(user);
        return items.get(itemId);
    }

    @Override
    public Item update(User user, long itemId, ItemDto itemDto) {
        checkId(itemId);
        isOwner(user.getId(), items.get(itemId));
        if (itemDto.getName() != null) {
            items.get(itemId).setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            items.get(itemId).setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            items.get(itemId).setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequest() != null) {
            items.get(itemId).setRequest(itemDto.getRequest());
        }
        return items.get(itemId);
    }

    @Override
    public void delete(long userId, long itemId) {
        checkId(itemId);
        isOwner(userId, items.get(itemId));
        items.remove(itemId);
    }

    private long getNextId() {
        return ++id;
    }

    private void checkId(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Предмета с таким id: " + itemId + " не существует");
        }
    }

    private void isOwner(long userId, Item item) {
        if (userId != item.getOwner().getId()) {
            throw new AccessErrorException("Пользователь не является владельцем вещи");
        }
    }
}
