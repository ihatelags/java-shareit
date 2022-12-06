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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceImplTest {

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
        itemRequest = new ItemRequest(1L, "газонокосилка", user, LocalDateTime.now());
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
    void createRequestWithNotFoundUserExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemRequestService.createRequest(user.getId(), newItemRequestDto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getRequestsOfUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<ItemRequest> requests = new ArrayList<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findItemRequestsOfUser(anyLong(), PageRequest.of(anyInt(), 10)))
                .thenReturn(requests);

        final List<ItemRequestDto> requestDtos = itemRequestService.getRequestsOfUser(user.getId(), 0, 10);

        assertNotNull(requestDtos);

        verify(itemRequestRepository, times(1)).findItemRequestsOfUser(1L, PageRequest.of(0, 10));
    }

    @Test
    void getRequestsWithNotFoundUserExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemRequestService.getRequestsOfUser(user.getId(), 0, 10));
        verify(userRepository, times(1)).findById(1L);
    }


    @Test
    void getExistingRequestsOfUsersTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final List<ItemRequest> requests = new ArrayList<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findItemRequestsOfAllUsers(anyLong(), PageRequest.of(anyInt(), 10)))
                .thenReturn(requests);

        final List<ItemRequestDto> requestDtos = itemRequestService.getExistingRequestsOfUsers(user.getId(), 0, 10);

        assertNotNull(requestDtos);

        verify(itemRequestRepository, times(1)).findItemRequestsOfAllUsers(1L, PageRequest.of(0, 10));
    }

    @Test
    void getExistingRequestsWithNotFoundUserExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemRequestService.getExistingRequestsOfUsers(user.getId(), 0, 10));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        itemRequestService.getRequest(user.getId(), itemRequest.getId());
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void getRequestOfUserWithNotFoundUserExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemRequestService.getRequest(user.getId(), itemRequest.getId()));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getRequestWithNotFoundItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemRequestService.getRequest(user.getId(), itemRequest.getId()));
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void createAndGetRequestTest() throws Exception {
        User user = new User(1L, "user 1", "user1@email");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "газонокосилка", LocalDateTime.now(), null);
        ItemRequest itemRequest = new ItemRequest(1L, "газонокосилка", user, null);
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("газонокосилка");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(any(), any(), any()))
                .thenReturn(itemRequest);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(itemRequestService.createRequest(anyLong(), newItemRequestDto))
                .thenReturn(itemRequestDto);

        ItemRequestDto iDto = itemRequestService.createRequest(1L, newItemRequestDto);

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        assertThat(itemRequestService.getRequest(user.getId(), itemRequestDto.getId()).getDescription().equals(iDto.getDescription()));
    }


}
