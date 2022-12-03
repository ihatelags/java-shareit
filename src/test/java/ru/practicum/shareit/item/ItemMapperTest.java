package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@SpringBootTest
class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;
    private User user;
    private Item item;
    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        item = new Item(1L, user, "дрель", "дрель ударная Макита", true, 1L);
        itemDto = new ItemDto(1L, "дрель", "дрель ударная Макита", true, 1L, null);
    }

    @Test
    void shouldProperlyMapToItemDtoTest() {
        ItemDto itemDto = itemMapper.toItemDto(item);

        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
    }

    @Test
    void shouldProperlyMapToItemTest() {
        Item item = itemMapper.toItem(itemDto, user);

        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
    }

    @Test
    void shouldProperlyMapTotoItemDtoWithBookingTest() {
        Comment comment = new Comment(1L, "comment", item, user, null);
        List<Comment> comments = Collections.singletonList(comment);
        ItemDtoWithBooking itemDtoWithBooking = itemMapper.toItemDtoWithBooking(item, comments, null, null);

        Assertions.assertNotNull(itemDtoWithBooking);
        Assertions.assertEquals(item.getId(), itemDtoWithBooking.getId());
        Assertions.assertEquals(item.getName(), itemDtoWithBooking.getName());

        ItemDtoWithBooking itemDtoWithBooking2 = itemMapper.toItemDtoWithBooking(item, new ArrayList<>(), null, null);
        Assertions.assertNotNull(itemDtoWithBooking2);
    }

}