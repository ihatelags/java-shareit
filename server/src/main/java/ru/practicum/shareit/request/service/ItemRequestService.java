package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(long userId, NewItemRequestDto newItemRequestDto);

    List<ItemRequestDto> getRequestsOfUser(long userId, int from, int size);

    List<ItemRequestDto> getExistingRequestsOfUsers(long userId, int from, int size);

    ItemRequestDto getRequest(long userId, long requestId);
}
