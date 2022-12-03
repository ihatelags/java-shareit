package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.InvalidCommentException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentsRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceImplTest {
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;

    private Item item;
    private User user;
    private User user2;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentsRepository,
                itemMapper,
                commentMapper);
        user = new User(1L, "user 1", "user1@email");
        user2 = new User(2L, "user 2", "user2@email");
        item = new Item(1L, user, "дрель", "дрель ударная Макита", true, 1L);
        itemDto = new ItemDto(1L, "дрель", "дрель ударная Макита", true, 1L, null);
    }

    @Test
    void addTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemMapper.toItemDto(any()))
                .thenReturn(itemDto);

        ItemDto itemDto2 = itemService.add(1L, itemDto);

        assertNotNull(itemDto2);
        assertEquals(1L, itemDto2.getId());
    }

    @Test
    void saveItemWithNotFoundOwnerExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException("Message"));

        final var ex = assertThrows(RuntimeException.class, () -> itemService.add(user.getId(), itemDto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.update(user.getId(), item.getId(), itemDto);
        verify(itemRepository, times(1)).save(item);
        verify(itemRepository, times(2)).findById(1L);
    }

    @Test
    void updateItemWithResponseStatusExceptionTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.save(any()))
                .thenThrow(new ItemNotFoundException("Message"));

        final var ex = assertThrows(RuntimeException.class, () -> itemService.update(user.getId(), item.getId(),
                itemDto));
        verify(itemRepository, times(2)).findById(1L);
    }

    @Test
    void updateItemWithNotFoundItemExTest() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException("Message"));
        final var ex = assertThrows(RuntimeException.class, () -> itemService.update(user.getId(), item.getId(),
                itemDto));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void updateItemWithNotFoundOwnerExTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException("Message"));

        final var ex = assertThrows(RuntimeException.class, () -> itemService.update(3L, item.getId(), itemDto));
        verify(userRepository, times(0)).findById(1L);
    }

    @Test
    void getItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        itemService.getById(item.getId(), user.getId());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void notFoundItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException("Message"));
        final var ex = assertThrows(RuntimeException.class, () -> itemService.getById(item.getId(), user.getId()));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getListOfItemsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        itemService.add(user.getId(), itemDto);
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(itemPage);

        final List<ItemDtoWithBooking> itemDtos = itemService.getAllByUserId(1L, 0, 10);

        assertNotNull(itemDtos);

        verify(itemRepository, times(1)).findAllByOwner(user, PageRequest.of(0, 10));
    }

    @Test
    void getListOfItemsWithNotFoundOwnerExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException("Message"));

        final var ex = assertThrows(RuntimeException.class, () -> itemService.getAllByUserId(1L, 0, 10));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void searchItemsByTextTest() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.findAllByOwner(any(User.class), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(item));
        when(itemRepository.searchByText("дрель", PageRequest.of(1, 10)))
                .thenReturn(List.of(item));
        List<ItemDto> result = itemService.searchByText("дрель", 1, 10);

        assertNotNull(result);
    }

    @Test
    void saveCommentWithNotFoundItemExTest() {
        CommentDto commentDto = new CommentDto(1L, "работает до 4 часов без подзаряда",
                1L, 1L, "user 1", null);
        when(commentsRepository.findById(anyLong()))
                .thenThrow(new InvalidCommentException("Message"));
        final var ex = assertThrows(RuntimeException.class, () -> itemService.addComment(1L, 1L, commentDto));
        verify(commentsRepository, times(0)).findById(any());
    }

    @Test
    void saveCommentWithIllegalArgumentExceptionTest() {
        Item itemNew = new Item(1L, user, "дрель", "дрель ударная Макита", false, 1L);
        User userNew = new User(2L, "user 2", "user2@email");
        CommentDto commentDto = new CommentDto(1L, "", 1L, 1L, "user 1", null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemNew));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNew));
        final var ex = assertThrows(RuntimeException.class, () -> itemService.addComment(2L, 1L, commentDto));
        assertNull(ex.getMessage());
        verify(itemRepository, times(1)).findById(1L);
    }

}