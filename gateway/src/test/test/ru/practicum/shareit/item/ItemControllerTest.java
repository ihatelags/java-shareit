package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.user.User;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;
    private final ObjectMapper mapper = new ObjectMapper();
    private ItemDto itemDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        itemDto = new ItemDto(1L, "дрель", "дрель ударная Макита", true, 1L);
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.saveItem(anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        verify(itemService, times(1))
                .saveItem(1L, itemDto);
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        verify(itemService, times(1))
                .updateItem(1L, itemDto);
    }

    @Test
    void getItemTest() throws Exception {
        ItemDtoWithBookings itemDtoWB = new ItemDtoWithBookings(1L, "дрель", "дрель ударная Макита", true, 1L, null, null, null);
        itemService.saveItem(1L, itemDto);

        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemDtoWB);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));

        verify(itemService, times(1))
                .get(1L, 1L);
    }

    @Test
    void getItemsOfUserTest() throws Exception {
        when(itemService.getListOfItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .getListOfItems(1L, 0, 10);
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", "home"))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .searchItemsByText("home", 0, 10);
    }

    @Test
    void saveCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "полезная вещь", 1L, "Анна", null);
        when(commentService.saveComment(any(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

        verify(commentService, times(1))
                .saveComment(commentDto, 1L);
    }

}
