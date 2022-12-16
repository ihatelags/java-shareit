package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private static final String USER_NOT_FOUND_MESSAGE = "Такого пользователя не существует";

    @Override
    public ItemRequestDto createRequest(long userId, NewItemRequestDto newItemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        if (newItemRequestDto.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        LocalDateTime createdDateTime = LocalDateTime.now();
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(newItemRequestDto, createdDateTime, user);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsOfUser(long userId, int from, int size) {
        validateUser(userId);
        int page = from / size;
        List<ItemRequest> requests = itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(userId,
                PageRequest.of(page, size));
        return itemRequestMapper.toItemRequestDtoList(requests);
    }

    @Override
    public List<ItemRequestDto> getExistingRequestsOfUsers(long userId, int from, int size) {
        validateUser(userId);
        int page = from / size;
        List<ItemRequest> requests = itemRequestRepository
                .findItemRequestByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(page, size));
        return itemRequestMapper.toItemRequestDtoList(requests);
    }

    @Override
    public ItemRequestDto getRequest(long userId, long requestId) {
        validateUser(userId);
        ItemRequest itemRequestFromStorage = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        return itemRequestMapper.toItemRequestDto(itemRequestFromStorage);
    }

    private void validateUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
    }
}