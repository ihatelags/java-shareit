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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceImplTest {

    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemMapper itemMapper;

    private Item item;
    private User user;
    private User user2;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, itemMapper);
        user = new User(1L, "user 1", "user1@email");
        user2 = new User(2L, "user 2", "user2@email");
        item = new Item(1L, "дрель", "дрель ударная Макита", user, true, 1L);
        itemDto = new ItemDto(1L, "дрель", "дрель ударная Макита", true, 1L);
    }

    @Test
    void saveItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemMapper.toItemDto(any()))
                .thenReturn(itemDto);

        ItemDto itemDto2 = itemService.saveItem(1L, itemDto);

        assertNotNull(itemDto2);
        assertEquals(1L, itemDto2.getId());

        verify(itemRepository, times(1)).save(item);

    }

    @Test
    void saveItemWithNotFoundOwnerExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemService.saveItem(user.getId(), itemDto));
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
        itemService.updateItem(user.getId(), itemDto);
        verify(itemRepository, times(1)).save(item);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void updateItemWithResponseStatusExceptionTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.save(any()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemService.updateItem(user.getId(), itemDto));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void updateItemWithNotFoundItemExTest() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());
        final var ex = assertThrows(RuntimeException.class, () -> itemService.updateItem(user.getId(), itemDto));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void updateItemWithNotFoundOwnerExTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemService.updateItem(user.getId(), itemDto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        itemService.get(item.getId(), user.getId());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void notFoundItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());
        final var ex = assertThrows(RuntimeException.class, () -> itemService.get(item.getId(), user.getId()));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getListOfItemsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        itemService.saveItem(user.getId(), itemDto);
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(itemPage);

        final List<ItemDtoWithBookings> itemDtos = itemService.getListOfItems(1L, 0, 10);

        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());

        verify(itemRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void getListOfItemsWithNotFoundOwnerExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> itemService.getListOfItems(1L, 0, 10));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void searchItemsByTextTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        itemService.saveItem(user.getId(), itemDto);
        final String text = "дрель";
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(itemPage);

        itemService.searchItemsByText(text, 0, 10);

        verify(itemRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

}
