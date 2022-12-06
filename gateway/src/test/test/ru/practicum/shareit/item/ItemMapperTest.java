package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;
    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        item = new Item(1L, "дрель", "дрель ударная Макита", user, true, 1L);
    }

    @Test
    void shouldProperlyMapToItemDtoTest() {
        ItemDto itemDto1 = itemMapper.toItemDto(item);

        Assertions.assertNotNull(itemDto1);
        Assertions.assertEquals(item.getId(), itemDto1.getId());
        Assertions.assertEquals(item.getName(), itemDto1.getName());
    }

    @Test
    void shouldProperlyMaptoItemDtoListTest() {
        List<Item> items = Collections.singletonList(item);
        List<ItemDto> itemDtos = itemMapper.toItemDtoList(items);

        Assertions.assertNotNull(itemDtos);
        Assertions.assertEquals(1, itemDtos.size());
        Assertions.assertEquals(item.getId(), itemDtos.get(0).getId());
        Assertions.assertEquals(item.getName(), itemDtos.get(0).getName());
    }
}
