package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class ItemRequestMapperTest {
    @Autowired
    private ItemRequestMapper itemRequestMapper;

    private ItemRequest itemRequest;
    private NewItemRequestDto newItemRequestDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        itemRequest = new ItemRequest(1L, "газонокосилка", user, LocalDateTime.now());
        newItemRequestDto = new NewItemRequestDto("газонокосилка");
    }

    @Test
    void shouldProperlyMapToRequestDtoTest() {
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);

        Assertions.assertNotNull(itemRequestDto);
        Assertions.assertEquals(itemRequest.getId(), itemRequestDto.getId());
        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void shouldProperlyMapToRequestTest() {
        ItemRequest itemRequest1 = itemRequestMapper.toItemRequest(newItemRequestDto, LocalDateTime.now(), user);

        Assertions.assertNotNull(itemRequest1);
        Assertions.assertEquals(newItemRequestDto.getDescription(), itemRequest1.getDescription());
    }

    @Test
    void shouldProperlyMaptoCommentDtoListTest() {
        List<ItemRequest> requests = Collections.singletonList(itemRequest);
        List<ItemRequestDto> requestsDtos = itemRequestMapper.toItemRequestDtoList(requests);

        Assertions.assertNotNull(requestsDtos);
        Assertions.assertEquals(1, requestsDtos.size());
        Assertions.assertEquals(itemRequest.getId(), requestsDtos.get(0).getId());
        Assertions.assertEquals(itemRequest.getDescription(), requestsDtos.get(0).getDescription());
    }

}
