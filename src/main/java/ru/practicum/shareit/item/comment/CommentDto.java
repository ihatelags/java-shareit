package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private ItemDto item;
    private LocalDateTime created;
}
