package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getAllByUserId(long userId) {
        return itemRepository.getAllByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.searchByText(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemRepository.add(userRepository.getById(userId), itemDto));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemRepository.update(userRepository.getById(userId), itemId, itemDto));
    }

    @Override
    public void delete(long userId, long itemId) {
        itemRepository.delete(userId, itemId);
    }
}
