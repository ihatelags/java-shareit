package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;


@SpringBootTest
public class CommentMapperTest {
    @Autowired
    private CommentMapper commentMapper;

    private Comment comment;
    private User user;
    private Item item;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        item = new Item(1L, "дрель", "дрель ударная Макита", user, true, 1L);
        comment = new Comment(1L, "работает до 4 часов без подзаряда", item, user, null);
        commentDto = new CommentDto(1L, "работает до 4 часов без подзаряда", 1L, "user 1", null);
    }

    @Test
    void shouldProperlyMapToCommentDtoTest() {
        CommentDto commentDto = commentMapper.toCommentDto(comment);

        Assertions.assertNotNull(commentDto);
        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), commentDto.getText());
    }

    @Test
    void shouldProperlyMapToCommentTest() {
        Comment commentNew = commentMapper.toComment(commentDto, user, item);

        Assertions.assertNotNull(commentNew);
        Assertions.assertEquals(commentDto.getId(), commentNew.getId());
        Assertions.assertEquals(commentDto.getText(), commentNew.getText());
    }

    @Test
    void shouldProperlyMaptoCommentDtoListTest() {
        List<Comment> comments = Collections.singletonList(comment);
        List<CommentDto> commentDtos = commentMapper.toCommentDtoList(comments);

        Assertions.assertNotNull(commentDtos);
        Assertions.assertEquals(1, commentDtos.size());
        Assertions.assertEquals(comment.getId(), commentDtos.get(0).getId());
        Assertions.assertEquals(comment.getText(), commentDtos.get(0).getText());
    }
}
