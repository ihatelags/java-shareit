package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@SpringBootTest
@ComponentScan(basePackages = {"ru.practicum.shareit.*"})
@EntityScan("ru.practicum.shareit.*")
@EnableJpaRepositories("ru.practicum.shareit.*")
class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;
    private User user;
    private Item item;
    private BookingDto itemDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        item = new Item(1L, user, "дрель", "дрель ударная Макита", true, 1L);
        itemDto = new BookingDto(1L, "дрель", "дрель ударная Макита", true, 1L, null);
    }

    @Test
    void shouldProperlyMapToItemDtoTest() {
        BookingDto itemDto = itemMapper.toItemDto(item);

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