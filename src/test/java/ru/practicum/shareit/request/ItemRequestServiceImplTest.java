package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceImplTest {

    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    private ItemRequest itemRequest;
    private NewItemRequestDto newItemRequestDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRequestMapper);
        user = new User(1L, "user 1", "user1@email");
        itemRequest = new ItemRequest(1L, "item desc", user, LocalDateTime.now());
        newItemRequestDto = new NewItemRequestDto("газонокосилка");
    }

    @Test
    void createRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(any(), any(), any()))
                .thenReturn(itemRequest);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        itemRequestService.createRequest(user.getId(), newItemRequestDto);

        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    void getRequestsOfUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<ItemRequest> requests = new ArrayList<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(anyLong(), PageRequest.of(anyInt(),
                10)))
                .thenReturn(requests);

        final List<ItemRequestDto> requestDtos = itemRequestService.getRequestsOfUser(user.getId(), 0, 10);

        assertNotNull(requestDtos);

        verify(itemRequestRepository, times(1)).findItemRequestByRequestorIdOrderByCreatedDesc(1L, PageRequest.of(0,
                10));
    }


    @Test
    void getExistingRequestsOfUsersTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<ItemRequest> requests = new ArrayList<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findItemRequestByRequestorIdNotOrderByCreatedDesc(anyLong(),
                PageRequest.of(anyInt(), 10)))
                .thenReturn(requests);

        final List<ItemRequestDto> requestDtos = itemRequestService.getExistingRequestsOfUsers(user.getId(), 0, 10);

        assertNotNull(requestDtos);

        verify(itemRequestRepository, times(1)).findItemRequestByRequestorIdNotOrderByCreatedDesc(1L,
                PageRequest.of(0, 10));
    }

    @Test
    void getRequestTest() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));

        itemRequestService.getRequest(itemRequest.getId(), user.getId());

        assertNotNull(itemRequest);
        assertEquals(1, itemRequest.getId());
    }

}