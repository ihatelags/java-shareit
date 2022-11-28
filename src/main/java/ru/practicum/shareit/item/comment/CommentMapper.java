package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                LocalDateTime.now()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                ItemMapper.toItemDto(comment.getItem()).getId(),
                comment.getCreated()
        );
    }

    public static ItemDto.Comment toItemComment(Comment comment) {
        return new ItemDto.Comment(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static ItemDtoWithBooking.Comment toItemWithBookingComment(Comment comment) {
        return new ItemDtoWithBooking.Comment(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
