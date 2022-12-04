package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestMapper {
    public ItemRequest toItemRequest(NewItemRequestDto newItemRequestDto, LocalDateTime createdDateTime, User user) {
        return new ItemRequest(
                null,
                newItemRequestDto.getDescription(),
                user,
                createdDateTime
        );
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getItems().stream()
                        .map(x -> new ItemRequestDto.Item(x.getId(), x.getName(), x.getDescription(), x.isAvailable(),
                                itemRequest.getRequestor().getId()))
                        .collect(Collectors.toList())
        );
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> requests) {
        return requests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
